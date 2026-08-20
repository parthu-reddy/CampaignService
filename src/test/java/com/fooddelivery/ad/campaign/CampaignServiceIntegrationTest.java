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


@ActiveProfiles("contract-test")
@SpringBootTest(properties = {"spring.cloud.config.enabled=false", "spring.config.import=", "spring.redis.enabled=false", "spring.main.allow-bean-definition-overriding=true", "eureka.client.enabled=false", "spring.kafka.consumer.group-id=campaign-integration-test"})
public class CampaignServiceIntegrationTest {

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
        // Campaign.maxBid is @Column(nullable = false); this fixture predates that constraint.
        campaign.setMaxBid(new BigDecimal("5.00"));

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
