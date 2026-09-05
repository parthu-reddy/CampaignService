package com.fooddelivery.contract;

import com.fooddelivery.ad.campaign.controller.InternalCampaignController;
import com.fooddelivery.ad.campaign.entity.Campaign;
import com.fooddelivery.ad.campaign.repository.CampaignRepository;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Base class for the generated HTTP contract tests under {@code contracts/http}.
 */
@org.springframework.test.context.ActiveProfiles("contract-test")
public class ContractTestBase {

    private static final UUID CAMPAIGN_ONE = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID CAMPAIGN_TWO = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @BeforeEach
    public void setup() {
        CampaignRepository campaignRepository = Mockito.mock(CampaignRepository.class);
        // getActiveCampaignsForBidding builds a TargetingSummary from this; the batch/budgets
        // contract never reaches that path, but the controller requires the collaborator.
        com.fooddelivery.ad.campaign.repository.AdGroupRepository adGroupRepository =
                Mockito.mock(com.fooddelivery.ad.campaign.repository.AdGroupRepository.class);
        Mockito.when(adGroupRepository.findByCampaignIdAndActiveTrue(Mockito.any()))
               .thenReturn(java.util.List.of());

        Campaign cmp1 = new Campaign();
        cmp1.setId(CAMPAIGN_ONE);
        cmp1.setDailyBudget(new BigDecimal("500.00"));
        cmp1.setLifetimeBudget(new BigDecimal("5000.00"));
        cmp1.setAdvertiserId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));

        Campaign cmp2 = new Campaign();
        cmp2.setId(CAMPAIGN_TWO);
        cmp2.setDailyBudget(new BigDecimal("1500.50"));
        cmp2.setLifetimeBudget(new BigDecimal("15000.50"));
        cmp2.setAdvertiserId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"));

        Mockito.when(campaignRepository.findAllById(Mockito.anyIterable()))
               .thenReturn(List.of(cmp1, cmp2));

        com.fooddelivery.ad.campaign.repository.AdvertiserProfileRepository advertiserProfileRepository = Mockito.mock(com.fooddelivery.ad.campaign.repository.AdvertiserProfileRepository.class);
        com.fooddelivery.ad.campaign.entity.AdvertiserProfile profile = new com.fooddelivery.ad.campaign.entity.AdvertiserProfile();
        profile.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        profile.setUserId("user-789");
        Mockito.when(advertiserProfileRepository.findById(UUID.fromString("123e4567-e89b-12d3-a456-426614174000")))
                .thenReturn(java.util.Optional.of(profile));

        RestAssuredMockMvc.standaloneSetup(
                new InternalCampaignController(campaignRepository, adGroupRepository),
                new com.fooddelivery.ad.campaign.controller.InternalAdvertiserController(advertiserProfileRepository)
        );
    }
}
