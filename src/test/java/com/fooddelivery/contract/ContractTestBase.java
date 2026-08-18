package com.fooddelivery.contract;

import com.fooddelivery.ad.campaign.controller.InternalCampaignController;
import com.fooddelivery.ad.campaign.entity.Campaign;
import com.fooddelivery.ad.campaign.repository.CampaignRepository;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@org.springframework.test.context.ActiveProfiles("contract-test")
public class ContractTestBase {

    @BeforeEach
    public void setup() {
        CampaignRepository campaignRepository = Mockito.mock(CampaignRepository.class);

        Campaign cmp1 = new Campaign();
        cmp1.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        cmp1.setDailyBudget(new BigDecimal("500.00"));
        cmp1.setAdvertiserId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));

        Campaign cmp2 = new Campaign();
        cmp2.setId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        cmp2.setDailyBudget(new BigDecimal("1500.50"));
        cmp2.setAdvertiserId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"));

        Mockito.when(campaignRepository.findById(UUID.fromString("11111111-1111-1111-1111-111111111111")))
               .thenReturn(Optional.of(cmp1));
        Mockito.when(campaignRepository.findById(UUID.fromString("22222222-2222-2222-2222-222222222222")))
               .thenReturn(Optional.of(cmp2));

        RestAssuredMockMvc.standaloneSetup(new InternalCampaignController(campaignRepository));
    }
}
