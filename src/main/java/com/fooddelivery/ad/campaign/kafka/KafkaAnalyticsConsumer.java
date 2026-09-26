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
    private final io.micrometer.core.instrument.MeterRegistry meterRegistry;

        private final com.fooddelivery.common.event.EventBinder eventBinder;

public KafkaAnalyticsConsumer(CampaignPerformanceRepository performanceRepository, ObjectMapper objectMapper, IIdempotencyKeyRepository idempotencyKeyRepository, io.micrometer.core.instrument.MeterRegistry meterRegistry, com.fooddelivery.common.event.EventBinder eventBinder) {
        this.eventBinder = eventBinder;
        this.performanceRepository = performanceRepository;
        this.objectMapper = objectMapper;
        this.idempotencyKeyRepository = idempotencyKeyRepository;
        this.meterRegistry = meterRegistry;
    }

    @RetryableTopic(attempts = "5", backoff = @Backoff(delay = 1000, multiplier = 2.0), autoCreateTopics = "true", dltStrategy = DltStrategy.FAIL_ON_ERROR, exclude = {com.fooddelivery.common.event.EventBindingException.class}, traversingCauses = "true")
    @KafkaListener(topics = KafkaConstants.TOPIC_AD_TRACKING_EVENTS, groupId = "${spring.kafka.consumer.group-id:campaign-service-group}-kafkaanalyticsconsumer")
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

        com.fooddelivery.common.event.AdTrackingEvent tracking;
        try {
            // All five fields are @NotNull on AdTrackingEvent, so the containsKey block that used to
            // stand here is the binding's job now. Both a malformed body and a missing field arrive
            // as an exception from bind, and both keep the existing behaviour: discard, do not retry
            // -- retrying cannot repair a payload.
            tracking = eventBinder.bind(message, com.fooddelivery.common.event.AdTrackingEvent.class);
        } catch (Exception e) {
            log.error("Dropping malformed tracking event (unparseable or incomplete): {}", message, e);
            meterRegistry.counter("campaign_event_dropped_total", "reason", "malformed").increment();
            return;
        }
        try {
            String eventType = tracking.getEventType();
            UUID campaignId = UUID.fromString(tracking.getCampaignId());
            UUID advertiserId = UUID.fromString(tracking.getAdvertiserId());
            BigDecimal amount = tracking.getAmount();
            
            // The day this counts against was decided once, by the tracker, on the advertiser's
            // calendar -- the same day it charged to the daily budget. Recomputing it here from the
            // timestamp would need the advertiser's zone and could disagree at midnight.
            LocalDate today = tracking.getSpendDay();

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
    public void handleDlt(Object message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                          @Header(KafkaHeaders.OFFSET) long offset) {
        log.error("Tracking event failed all retries and sent to DLT: {} - {} replay={}", topic, message, com.fooddelivery.common.util.KafkaHeaderUtils.deadLetterPosition(topic, partition, offset));
        meterRegistry.counter("kafka_dlt_depth_total", "topic", topic).increment();
    }
}
