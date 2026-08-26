package com.fooddelivery.ad.campaign.contract;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(classes = BaseMessagingClass.TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration,org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration"})
@org.springframework.test.context.ActiveProfiles("contract-test")
@AutoConfigureMessageVerifier
@EmbeddedKafka(partitions = 1, topics = {"ad-events"})
public abstract class BaseMessagingClass {

    @org.springframework.boot.SpringBootConfiguration
    @org.springframework.boot.autoconfigure.EnableAutoConfiguration
    static class TestConfig {
        @Bean
        public KafkaMessageVerifier kafkaMessageVerifier() {
            return new KafkaMessageVerifier();
        }
    }

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers",
                () -> System.getProperty("spring.embedded.kafka.brokers", "localhost:9092"));
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    /**
     * Mirrors CampaignServiceImpl.publishOutboxEvent: the payload is the CampaignChangedEvent
     * published through the real OutboxProcessor (ADVERTISEMENT -> ad-events).
     */
    public void fireAdEvent() throws Exception {
        fireEvent(com.fooddelivery.common.constants.EventType.AD_CAMPAIGN_CREATED, com.fooddelivery.ad.campaign.enums.CampaignStatus.ACTIVE);
    }
    
    public void fireAdEventDeleted() throws Exception {
        fireEvent(com.fooddelivery.common.constants.EventType.AD_CAMPAIGN_DELETED, com.fooddelivery.ad.campaign.enums.CampaignStatus.DELETED);
    }
    
    public void fireAdEventPaused() throws Exception {
        fireEvent(com.fooddelivery.common.constants.EventType.AD_CAMPAIGN_PAUSED, com.fooddelivery.ad.campaign.enums.CampaignStatus.PAUSED);
    }
    
    public void fireAdEventResumed() throws Exception {
        fireEvent(com.fooddelivery.common.constants.EventType.AD_CAMPAIGN_RESUMED, com.fooddelivery.ad.campaign.enums.CampaignStatus.ACTIVE);
    }
    
    public void fireAdEventUpdated() throws Exception {
        fireEvent(com.fooddelivery.common.constants.EventType.AD_CAMPAIGN_UPDATED, com.fooddelivery.ad.campaign.enums.CampaignStatus.ACTIVE);
    }

    public void fireAdEventCompleted() throws Exception {
        fireEvent(com.fooddelivery.common.constants.EventType.AD_CAMPAIGN_COMPLETED, com.fooddelivery.ad.campaign.enums.CampaignStatus.COMPLETED);
    }

    public void fireAdEventBudgetExhausted() throws Exception {
        fireEvent(com.fooddelivery.common.constants.EventType.AD_CAMPAIGN_BUDGET_EXHAUSTED, com.fooddelivery.ad.campaign.enums.CampaignStatus.ACTIVE);
    }

    public void fireAdEventPacingUpdated() throws Exception {
        fireEvent(com.fooddelivery.common.constants.EventType.AD_CAMPAIGN_PACING_UPDATED, com.fooddelivery.ad.campaign.enums.CampaignStatus.ACTIVE);
    }

    public void fireAdBudgetAlert() throws Exception {
        fireEvent(com.fooddelivery.common.constants.EventType.AD_BUDGET_ALERT, com.fooddelivery.ad.campaign.enums.CampaignStatus.ACTIVE);
    }

    public void fireAdEventCreativeApproved() throws Exception {
        fireEvent(com.fooddelivery.common.constants.EventType.AD_CREATIVE_APPROVED, "ACTIVE");
    }

    public void fireAdEventCreativePending() throws Exception {
        fireEvent(com.fooddelivery.common.constants.EventType.AD_CREATIVE_PENDING, "PENDING");
    }

    public void fireAdEventCreativeRejected() throws Exception {
        fireEvent(com.fooddelivery.common.constants.EventType.AD_CREATIVE_REJECTED, "REJECTED");
    }

    private void fireEvent(com.fooddelivery.common.constants.EventType eventType, com.fooddelivery.ad.campaign.enums.CampaignStatus status) throws Exception {
        fireEvent(eventType, status.name());
    }

    /**
     * Creative review events carry the creative's review status (PENDING / REJECTED),
     * which is not a CampaignStatus value, so the status travels as a raw string.
     */
    private void fireEvent(com.fooddelivery.common.constants.EventType eventType, String status) throws Exception {
        // Built with the builder rather than a positional constructor so that adding a field to
        // CampaignChangedEvent does not silently shift these arguments.
        com.fooddelivery.common.event.CampaignChangedEvent event =
            com.fooddelivery.common.event.CampaignChangedEvent.builder()
                .campaignId(java.util.UUID.fromString("1d9c4f70-2a83-4b16-9e5d-7c0a3b8f6e41"))
                .advertiserId(java.util.UUID.fromString("3e14926d-0c98-5840-abcd-37ec439ddc25"))
                .status(status)
                .maxBid(new java.math.BigDecimal("12.50"))
                .budget(new java.math.BigDecimal("500.00"))
                .budgetExhausted(false)
                .pacingMultiplier(1.0)
                .schemaVersion(2)
                .build();

        com.fooddelivery.common.outbox.entity.OutboxEventEntity outboxEvent =
                com.fooddelivery.common.outbox.entity.OutboxEventEntity.builder()
                        .id(java.util.UUID.randomUUID())
                        .aggregateType(com.fooddelivery.common.constants.AggregateType.ADVERTISEMENT)
                        .aggregateId(event.getCampaignId().toString())
                        .eventType(eventType)
                        .payload(objectMapper.writeValueAsString(event))
                        .createdAt(java.time.LocalDateTime.now())
                        .build();
        com.fooddelivery.common.outbox.repository.OutboxEventRepository repo =
                org.mockito.Mockito.mock(com.fooddelivery.common.outbox.repository.OutboxEventRepository.class);
        org.mockito.Mockito.when(repo.findTop100ByStatusInOrderByCreatedAtAsc(org.mockito.ArgumentMatchers.anyList()))
                .thenReturn(new java.util.ArrayList<>(java.util.List.of(outboxEvent)));
        new com.fooddelivery.common.outbox.service.OutboxProcessor(repo, kafkaTemplate, new io.micrometer.core.instrument.simple.SimpleMeterRegistry()).processOutboxEvents();
    }
}
