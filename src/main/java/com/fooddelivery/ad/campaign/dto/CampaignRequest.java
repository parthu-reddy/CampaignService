package com.fooddelivery.ad.campaign.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@com.fooddelivery.ad.campaign.validation.ValidCampaignBudget@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.Data

public class CampaignRequest {
    @NotNull
    private UUID advertiserId;
    @NotNull
    @jakarta.validation.constraints.NotBlank
    @jakarta.validation.constraints.Size(max = 255)
    private String name;
    @NotNull
    @jakarta.validation.constraints.Positive
    private BigDecimal dailyBudget;
    @jakarta.validation.constraints.Positive
    private BigDecimal lifetimeBudget;
    @NotNull
    @jakarta.validation.constraints.Positive
    private BigDecimal maxBid;
    @NotNull
    private Instant startDate;
    private Instant endDate;
    @PositiveOrZero
    private Integer frequencyCap;

}
