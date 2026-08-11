package com.fooddelivery.ad.campaign.kafka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.ad.campaign.entity.CampaignPerformance;
import com.fooddelivery.ad.campaign.repository.CampaignPerformanceRepository;
import com.fooddelivery.common.constants.EventPayloadConstants;
import com.fooddelivery.common.constants.KafkaConstants;
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
public class KafkaAnalyticsConsumer {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(KafkaAnalyticsConsumer.class);
    private final CampaignPerformanceRepository performanceRepository;
    private final ObjectMapper objectMapper;

    public KafkaAnalyticsConsumer(CampaignPerformanceRepository performanceRepository, ObjectMapper objectMapper) {
        this.performanceRepository = performanceRepository;
        this.objectMapper = objectMapper;
    }

    @RetryableTopic(attempts = "5", backoff = @Backoff(delay = 1000, multiplier = 2.0), autoCreateTopics = "true", dltStrategy = DltStrategy.FAIL_ON_ERROR)
    @KafkaListener(topics = KafkaConstants.TOPIC_AD_TRACKING_EVENTS, groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    @io.micrometer.observation.annotation.Observed(name = "analytics.consume", contextualName = "analytics-consumer")
    public void consumeTrackingEvent(String message) {
        Map<String, Object> payload;
        try {
            payload = objectMapper.readValue(message, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            // Malformed JSON is unrecoverable — log and discard to prevent infinite retry loop
            log.error("Dropping malformed tracking event (unparseable JSON): {}", message, e);
            return;
        }
        try {
            String eventType = (String) payload.get(EventPayloadConstants.EVENT_TYPE);
            UUID campaignId = UUID.fromString((String) payload.get(EventPayloadConstants.CAMPAIGN_ID));
            UUID advertiserId = UUID.fromString((String) payload.get(EventPayloadConstants.ADVERTISER_ID));
            BigDecimal amount = new BigDecimal((String) payload.get(EventPayloadConstants.AMOUNT));
            LocalDate today = LocalDate.now();
            CampaignPerformance performance = performanceRepository.findByCampaignIdAndDate(campaignId, today).orElseGet(() -> {
                CampaignPerformance p = new CampaignPerformance();
                p.setCampaignId(campaignId);
                p.setAdvertiserId(advertiserId);
                p.setDate(today);
                p.setImpressions(0);
                p.setClicks(0);
                p.setConversions(0);
                p.setSpend(BigDecimal.ZERO);
                return p;
            });
            if ("IMPRESSION".equals(eventType)) {
                performance.setImpressions(performance.getImpressions() + 1);
            } else if ("CLICK".equals(eventType)) {
                performance.setClicks(performance.getClicks() + 1);
            } else if ("CONVERSION".equals(eventType)) {
                performance.setConversions(performance.getConversions() + 1);
            }
            performance.setSpend(performance.getSpend().add(amount));
            performanceRepository.save(performance);
            log.debug("Updated campaign performance for campaign {}", campaignId);
        } catch (IllegalArgumentException e) {
            // Missing required fields or invalid UUID — unrecoverable, discard
            log.error("Dropping tracking event with invalid field values: {}", message, e);
        } catch (Exception e) {
            // Infrastructure/transient error (DB timeout, etc.) — rethrow so Kafka retries or sends to DLQ
            log.error("Transient error processing tracking event, will be retried: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process tracking event", e);
        }
    }

    @DltHandler
    public void handleDlt(Object message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("Tracking event failed all retries and sent to DLT: {} - {}", topic, message);
    }
}
