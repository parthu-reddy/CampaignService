package com.fooddelivery.ad.campaign.dto;

import java.util.UUID;
import java.time.LocalDate;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CampaignPerformanceResponse {
    @jakarta.validation.constraints.NotNull
    private UUID id;
    @jakarta.validation.constraints.NotNull
    private UUID advertiserId;
    @jakarta.validation.constraints.NotNull
    private UUID campaignId;
    @jakarta.validation.constraints.NotNull
    private LocalDate date;
    private long impressions;
    private long clicks;
    private long conversions;
    @jakarta.validation.constraints.NotNull
    private BigDecimal spend;
}
