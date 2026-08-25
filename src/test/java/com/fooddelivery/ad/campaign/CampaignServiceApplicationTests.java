package com.fooddelivery.ad.campaign;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.test.context.ActiveProfiles;


@org.springframework.test.context.ActiveProfiles("contract-test")
@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "spring.config.import=",
        "spring.redis.enabled=false",
        "spring.main.allow-bean-definition-overriding=true",
        "eureka.client.enabled=false"
})
class CampaignServiceApplicationTests {
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.fooddelivery.common.outbox.service.OutboxProcessor outboxProcessor;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.fooddelivery.common.outbox.repository.OutboxEventRepository outboxEventRepository;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.fooddelivery.common.service.NotificationRouterService notificationRouterService;

    @Test
    void contextLoads() {
        org.junit.jupiter.api.Assertions.assertNotNull(outboxProcessor, "OutboxProcessor should be wired when outbox.enabled is true");
        org.junit.jupiter.api.Assertions.assertNotNull(outboxEventRepository, "OutboxEventRepository should be wired when outbox.enabled is true");
        org.junit.jupiter.api.Assertions.assertNotNull(notificationRouterService, "NotificationRouterService should be wired when outbox.enabled is true");
    }

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void outboxProcessorBeanExists() {
        assertTrue(applicationContext.containsBean("outboxProcessor"), "OutboxProcessor bean should be present");
    }
}
