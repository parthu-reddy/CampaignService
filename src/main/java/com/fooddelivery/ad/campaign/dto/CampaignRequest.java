package com.fooddelivery.ad.campaign.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class CampaignRequest {
    @NotNull
    private UUID advertiserId;
    @NotNull
    private String name;
    @NotNull
    @PositiveOrZero
    private BigDecimal dailyBudget;
    @NotNull
    @PositiveOrZero
    private BigDecimal lifetimeBudget;
    @NotNull
    @PositiveOrZero
    private BigDecimal maxBid;
    @NotNull
    private Instant startDate;
    private Instant endDate;
}
