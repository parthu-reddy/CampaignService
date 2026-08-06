package com.fooddelivery.ad.campaign;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.boot.test.mock.mockito.MockBean;
import io.lettuce.core.RedisClient;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;

@SpringBootTest
class CampaignServiceApplicationTests {

    @MockBean
    private RedisClient redisClient;

    @MockBean
    private LettuceBasedProxyManager lettuceProxyManager;

    @Test
    void contextLoads() {
    }
}
