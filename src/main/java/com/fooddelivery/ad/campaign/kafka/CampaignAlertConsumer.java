package com.fooddelivery.ad.campaign.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.ad.campaign.service.CampaignService;
import com.fooddelivery.common.constants.EventType;
import com.fooddelivery.common.constants.KafkaConstants;
import com.fooddelivery.common.util.EventPayloadUtils;
import com.fooddelivery.common.repository.IIdempotencyKeyRepository;
import com.fooddelivery.common.entity.IdempotencyKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class CampaignAlertConsumer {

    private final CampaignService campaignService;
    private final ObjectMapper objectMapper;
    private final IIdempotencyKeyRepository idempotencyKeyRepository;

    public CampaignAlertConsumer(CampaignService campaignService, ObjectMapper objectMapper, IIdempotencyKeyRepository idempotencyKeyRepository) {
        this.campaignService = campaignService;
        this.objectMapper = objectMapper;
        this.idempotencyKeyRepository = idempotencyKeyRepository;
    }

    @Transactional
    @RetryableTopic(attempts = "5")
    @KafkaListener(topics = KafkaConstants.TOPIC_AD_EVENTS, groupId = "campaign-alert-consumer-group")
    public void consumeAdEvent(String message, @Headers Map<String, Object> headers) throws Exception {
        JsonNode root = objectMapper.readTree(message);
        String eventTypeStr = EventPayloadUtils.resolveEventType(root, headers);
        
        if (EventType.AD_BUDGET_ALERT.name().equals(eventTypeStr)) {
            JsonNode payload = root;
            
            String eventId = root.hasNonNull("eventId") ? root.get("eventId").asText() : UUID.randomUUID().toString();
            String idempotencyKeyStr = "processed_event:budget_alert:" + eventId;
            if (idempotencyKeyRepository.tryClaim(idempotencyKeyStr) == 0) {
                log.info("Duplicate budget alert event ignored: {}", idempotencyKeyStr);
                return;
            }

            String advertiserIdStr = payload.path("advertiserId").asText(null);
            String campaignIdStr = EventPayloadUtils.campaignId(payload);
            
            if (advertiserIdStr != null && campaignIdStr != null) {
                UUID campaignId = UUID.fromString(campaignIdStr);
                UUID advertiserId = UUID.fromString(advertiserIdStr);
                log.warn("Received AD_BUDGET_ALERT for advertiser {}, pausing campaign {}", advertiserIdStr, campaignIdStr);
                campaignService.pauseCampaign(campaignId, advertiserId);
            } else {
                log.warn("Received AD_BUDGET_ALERT but missing advertiserId or campaignId. payload={}", payload);
            }
        }
    }

    @DltHandler
    public void handleDlt(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, Exception e) {
        log.error("DLQ: Failed to process alert event on topic {} after retries: {}. Error: {}", topic, message, e.getMessage());
    }
}
