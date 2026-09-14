package com.fooddelivery.ad.campaign.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.ad.campaign.service.CampaignService;
import com.fooddelivery.common.repository.IIdempotencyKeyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignAlertConsumerTest {

    @Mock
    private CampaignService campaignService;
    
    @Mock
    private IIdempotencyKeyRepository idempotencyKeyRepository;

    private CampaignAlertConsumer consumer;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        consumer = new CampaignAlertConsumer(campaignService, objectMapper, idempotencyKeyRepository,
                new com.fooddelivery.common.event.EventBinder(objectMapper,
                jakarta.validation.Validation.buildDefaultValidatorFactory().getValidator()));
    }

    @Test
    void pausesOnlyTheAlertedCampaignAndNoDirectKafkaSends() throws Exception {
        UUID advertiserId = UUID.randomUUID();
        UUID campaignA = UUID.randomUUID();
        UUID campaignB = UUID.randomUUID();
        UUID campaignC = UUID.randomUUID();

        // Simulate AD_BUDGET_ALERT for campaign B
        String payload = String.format("""
            {
                "eventType": "AD_BUDGET_ALERT",
                "advertiserId": "%s",
                "campaignId": "%s",
                "eventId": "test-event-123"
            }
        """, advertiserId.toString(), campaignB.toString());

        when(idempotencyKeyRepository.tryClaim(anyString())).thenReturn(1);

        // With the eventType header, the way OutboxProcessor publishes every event. The test
        // passed Map.of() before because the consumer resolved the type from the body; the
        // bound consumer reads the header, which is what production always sends.
        consumer.consumeAdEvent(payload, Map.of("eventType", "AD_BUDGET_ALERT"));

        // Assert B is PAUSED
        verify(campaignService).pauseCampaign(campaignB, advertiserId);

        // Assert A and C are still ACTIVE (i.e. not paused)
        verify(campaignService, never()).pauseCampaign(campaignA, advertiserId);
        verify(campaignService, never()).pauseCampaign(campaignC, advertiserId);
    }
}
