package com.fooddelivery.ad.campaign.contract;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(classes = BaseMessagingClass.TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@org.springframework.test.context.ActiveProfiles("contract-test")
@AutoConfigureMessageVerifier
@EmbeddedKafka(partitions = 1, topics = {"ad-events"})
public abstract class BaseMessagingClass {

    @org.springframework.boot.SpringBootConfiguration
    @org.springframework.boot.autoconfigure.EnableAutoConfiguration(exclude = {
            org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
            org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
            org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class,
            org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration.class,
            org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration.class
    })
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
     * Mirrors CampaignServiceImpl.publishOutboxEvent: the payload is the saved Campaign entity
     * itself, published through the real OutboxProcessor (ADVERTISEMENT -> ad-events, key = id).
     */
    public void fireAdEvent() throws Exception {
        com.fooddelivery.ad.campaign.entity.Campaign campaign =
                new com.fooddelivery.ad.campaign.entity.Campaign();
        campaign.setId(java.util.UUID.fromString("1d9c4f70-2a83-4b16-9e5d-7c0a3b8f6e41"));
        campaign.setAdvertiserId(java.util.UUID.fromString("3e14926d-0c98-5840-abcd-37ec439ddc25"));
        campaign.setName("Summer Pizza Push");
        campaign.setStatus(com.fooddelivery.ad.campaign.enums.CampaignStatus.ACTIVE);
        campaign.setDailyBudget(new java.math.BigDecimal("500.00"));
        campaign.setMaxBid(new java.math.BigDecimal("12.50"));

        com.fooddelivery.common.outbox.entity.OutboxEventEntity outboxEvent =
                com.fooddelivery.common.outbox.entity.OutboxEventEntity.builder()
                        .id(java.util.UUID.randomUUID())
                        .aggregateType(com.fooddelivery.common.constants.AggregateType.ADVERTISEMENT)
                        .aggregateId(campaign.getId().toString())
                        .eventType(com.fooddelivery.common.constants.EventType.AD_CAMPAIGN_CREATED)
                        .payload(objectMapper.writeValueAsString(campaign))
                        .createdAt(java.time.LocalDateTime.now())
                        .build();

        com.fooddelivery.common.outbox.repository.OutboxEventRepository repo =
                org.mockito.Mockito.mock(com.fooddelivery.common.outbox.repository.OutboxEventRepository.class);
        org.mockito.Mockito.when(repo.findTop100ByStatusInOrderByCreatedAtAsc(org.mockito.ArgumentMatchers.anyList()))
                .thenReturn(new java.util.ArrayList<>(java.util.List.of(outboxEvent)));
        new com.fooddelivery.common.outbox.service.OutboxProcessor(repo, kafkaTemplate).processOutboxEvents();
    }
}
