package com.fooddelivery.ad.campaign.dto;

import java.util.UUID;
import java.time.Instant;
import java.math.BigDecimal;
import com.fooddelivery.ad.campaign.enums.CampaignStatus;@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.Data


public class CampaignResponse {
    @jakarta.validation.constraints.NotNull
    private UUID id;
    @jakarta.validation.constraints.NotNull
    private UUID advertiserId;
    @jakarta.validation.constraints.NotNull
    private String name;
    @jakarta.validation.constraints.NotNull
    private CampaignStatus status;
    @jakarta.validation.constraints.NotNull
    private BigDecimal dailyBudget;
    @jakarta.validation.constraints.NotNull
    private BigDecimal lifetimeBudget;
    @jakarta.validation.constraints.NotNull
    private BigDecimal maxBid;
    @jakarta.validation.constraints.NotNull
    private Instant startDate;
    private Instant endDate;
    @jakarta.validation.constraints.NotNull
    private Integer frequencyCap;
    @jakarta.validation.constraints.NotNull
    private Long version;

}
