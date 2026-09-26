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

        private final com.fooddelivery.common.event.EventBinder eventBinder;

public CampaignAlertConsumer(CampaignService campaignService, ObjectMapper objectMapper, IIdempotencyKeyRepository idempotencyKeyRepository, com.fooddelivery.common.event.EventBinder eventBinder) {
        this.eventBinder = eventBinder;
        this.campaignService = campaignService;
        this.objectMapper = objectMapper;
        this.idempotencyKeyRepository = idempotencyKeyRepository;
    }

    @Transactional
    @RetryableTopic(attempts = "5", exclude = {com.fooddelivery.common.event.EventBindingException.class}, traversingCauses = "true")
    @KafkaListener(topics = KafkaConstants.TOPIC_AD_EVENTS, groupId = "campaign-alert-consumer-group-campaignalertconsumer")
    public void consumeAdEvent(String message, @Headers Map<String, Object> headers) throws Exception {
        String eventTypeStr = com.fooddelivery.common.util.KafkaHeaderUtils.extractEventType(headers, null);
        if (!EventType.AD_BUDGET_ALERT.name().equals(eventTypeStr)) {
            return;
        }
        // WalletService.publishBudgetAlert serialises a BudgetAlertEvent -- {eventId, advertiserId,
        // campaignId} -- which is a different shape from the CampaignChangedEvent the rest of
        // ad-events carries. Producer and consumer were changed together.
        com.fooddelivery.common.event.BudgetAlertEvent event = eventBinder.bindIf(
                EventType.AD_BUDGET_ALERT, eventTypeStr, message,
                com.fooddelivery.common.event.BudgetAlertEvent.class)
                .orElseThrow(() -> new IllegalStateException(
                        "bindIf returned empty for " + eventTypeStr
                                + " despite an exact event-type match"));

        String eventId = event.getEventId() != null ? event.getEventId() : UUID.randomUUID().toString();
        String idempotencyKeyStr = "processed_event:budget_alert:" + eventId;
        if (idempotencyKeyRepository.tryClaim(idempotencyKeyStr) == 0) {
            log.info("Duplicate budget alert event ignored: {}", idempotencyKeyStr);
            return;
        }

        if (event.getAdvertiserId() != null && event.getCampaignId() != null) {
            log.warn("Received AD_BUDGET_ALERT for advertiser {}, pausing campaign {}",
                    event.getAdvertiserId(), event.getCampaignId());
            campaignService.pauseCampaign(event.getCampaignId(), event.getAdvertiserId());
        } else {
            log.warn("Received AD_BUDGET_ALERT but missing advertiserId or campaignId. event={}", event);
        }
    }

    @DltHandler
    public void handleDlt(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                          @Header(KafkaHeaders.OFFSET) long offset, Exception e) {
        log.error("DLQ: Failed to process alert event on topic {} after retries: {}. Error: {} replay={}", topic, message,
                e.getMessage(), com.fooddelivery.common.util.KafkaHeaderUtils.deadLetterPosition(topic, partition, offset));
    }
}
