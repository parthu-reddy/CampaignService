package com.fooddelivery.ad.campaign.kafka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.ad.campaign.entity.CampaignPerformance;
import com.fooddelivery.ad.campaign.repository.CampaignPerformanceRepository;
import com.fooddelivery.common.constants.EventPayloadConstants;
import com.fooddelivery.common.constants.KafkaConstants;
import com.fooddelivery.common.entity.IdempotencyKey;
import com.fooddelivery.common.repository.IIdempotencyKeyRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Service
@lombok.extern.slf4j.Slf4j
public class KafkaAnalyticsConsumer {

    private final CampaignPerformanceRepository performanceRepository;
    private final ObjectMapper objectMapper;
    private final IIdempotencyKeyRepository idempotencyKeyRepository;
    private final java.time.Clock clock;
    private final io.micrometer.core.instrument.MeterRegistry meterRegistry;

    public KafkaAnalyticsConsumer(CampaignPerformanceRepository performanceRepository, ObjectMapper objectMapper, IIdempotencyKeyRepository idempotencyKeyRepository, java.time.Clock clock, io.micrometer.core.instrument.MeterRegistry meterRegistry) {
        this.performanceRepository = performanceRepository;
        this.objectMapper = objectMapper;
        this.idempotencyKeyRepository = idempotencyKeyRepository;
        this.clock = clock;
        this.meterRegistry = meterRegistry;
    }

    @RetryableTopic(attempts = "5", backoff = @Backoff(delay = 1000, multiplier = 2.0), autoCreateTopics = "true", dltStrategy = DltStrategy.FAIL_ON_ERROR)
    @KafkaListener(topics = KafkaConstants.TOPIC_AD_TRACKING_EVENTS, groupId = "${spring.kafka.consumer.group-id:campaign-service-group}")
    @Transactional
    @io.micrometer.observation.annotation.Observed(name = "analytics.consume", contextualName = "analytics-consumer")
    public void consumeTrackingEvent(String message, @org.springframework.messaging.handler.annotation.Headers java.util.Map<String, Object> headers) {
        meterRegistry.counter("kafka_consumer_records_consumed_total", "topic", KafkaConstants.TOPIC_AD_TRACKING_EVENTS).increment();
        String extractedEventId = com.fooddelivery.common.util.KafkaHeaderUtils.extractHeaderValue(headers, "eventId");
        final String resolvedEventId;
        if (extractedEventId == null) {
            resolvedEventId = UUID.nameUUIDFromBytes(message.getBytes(java.nio.charset.StandardCharsets.UTF_8)).toString();
        } else {
            resolvedEventId = extractedEventId;
        }

        String idempotencyKeyStr = "processed_event:" + resolvedEventId;

        if (idempotencyKeyRepository.tryClaim(idempotencyKeyStr) == 0) {
            log.info("Duplicate tracking event ignored: {}", idempotencyKeyStr);
            return;
        }

        Map<String, Object> payload;
        try {
            payload = objectMapper.readValue(message, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            // Malformed JSON is unrecoverable — log and discard to prevent infinite retry loop
            log.error("Dropping malformed tracking event (unparseable JSON): {}", message, e);
            meterRegistry.counter("campaign_event_dropped_total", "reason", "malformed").increment();
            return;
        }
        try {
            if (!payload.containsKey(EventPayloadConstants.EVENT_TYPE) || 
                !payload.containsKey(EventPayloadConstants.CAMPAIGN_ID) || 
                !payload.containsKey(EventPayloadConstants.ADVERTISER_ID) || 
                !payload.containsKey(EventPayloadConstants.AMOUNT) ||
                !payload.containsKey(EventPayloadConstants.TIMESTAMP)) {
                throw new InvalidTrackingEventException("Missing required fields in payload");
            }

            String eventType = (String) payload.get(EventPayloadConstants.EVENT_TYPE);
            UUID campaignId = UUID.fromString((String) payload.get(EventPayloadConstants.CAMPAIGN_ID));
            UUID advertiserId = UUID.fromString((String) payload.get(EventPayloadConstants.ADVERTISER_ID));
            BigDecimal amount = new BigDecimal(payload.get(EventPayloadConstants.AMOUNT).toString());
            
            // Business time bucketing
            long timestampMs = Long.parseLong(payload.get(EventPayloadConstants.TIMESTAMP).toString());
            LocalDate today = java.time.Instant.ofEpochMilli(timestampMs)
                                     .atZone(clock.getZone())
                                     .toLocalDate();

            int i = 0, c = 0, v = 0;
            BigDecimal spend = BigDecimal.ZERO;

            if ("IMPRESSION".equals(eventType)) {
                i = 1;
                spend = amount;
            } else if ("CLICK".equals(eventType)) {
                c = 1;
            } else if ("CONVERSION".equals(eventType)) {
                v = 1;
            }

            performanceRepository.upsertPerformance(UUID.randomUUID(), campaignId, advertiserId, today, i, c, v, spend);
            log.debug("Upserted campaign performance for campaign {}", campaignId);

        } catch (InvalidTrackingEventException | IllegalArgumentException e) {
            // Missing required fields or invalid UUID — unrecoverable, discard
            log.error("Dropping tracking event with invalid field values: {}", message, e);
            meterRegistry.counter("campaign_event_dropped_total", "reason", "malformed").increment();
        } catch (Exception e) {
            // Infrastructure/transient error (DB timeout, etc.) — rethrow so Kafka retries or sends to DLQ
            log.error("Transient error processing tracking event, will be retried: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process tracking event", e);
        }
    }

    @DltHandler
    public void handleDlt(Object message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("Tracking event failed all retries and sent to DLT: {} - {}", topic, message);
        meterRegistry.counter("kafka_dlt_depth_total", "topic", topic).increment();
    }
}
