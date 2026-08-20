package com.fooddelivery.ad.campaign;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
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

    @Test
    void contextLoads() {
    }
}
