package com.fooddelivery.ad.campaign;

import com.fooddelivery.ad.campaign.entity.Campaign;
import com.fooddelivery.ad.campaign.enums.CampaignStatus;
import com.fooddelivery.ad.campaign.repository.CampaignRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.springframework.boot.test.mock.mockito.MockBean;
import io.lettuce.core.RedisClient;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;

@SpringBootTest
public class CampaignServiceIntegrationTest {

    @MockBean
    private RedisClient redisClient;

    @MockBean
    private LettuceBasedProxyManager lettuceProxyManager;

    @Autowired
    private CampaignRepository campaignRepository;

    @Test
    public void testOptimisticLockingOnCampaign() {
        Campaign campaign = new Campaign();
        campaign.setAdvertiserId(UUID.randomUUID());
        campaign.setName("Test Campaign");
        campaign.setDailyBudget(new BigDecimal("100.00"));
        campaign.setLifetimeBudget(new BigDecimal("1000.00"));
        campaign.setStartDate(Instant.now());
        campaign.setStatus(CampaignStatus.ACTIVE);

        Campaign savedCampaign = campaignRepository.save(campaign);

        // Fetch campaign in two separate sessions (simulated)
        Campaign tx1 = campaignRepository.findById(savedCampaign.getId()).get();
        Campaign tx2 = campaignRepository.findById(savedCampaign.getId()).get();

        tx1.setDailyBudget(new BigDecimal("150.00"));
        campaignRepository.save(tx1); // Version becomes 1

        tx2.setDailyBudget(new BigDecimal("200.00"));
        
        assertThrows(ObjectOptimisticLockingFailureException.class, () -> {
            campaignRepository.save(tx2); // Should throw because version is still 0 in tx2
        });
    }
}
