package com.fooddelivery.ad.campaign.dto;

import java.util.UUID;
import java.time.LocalDate;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CampaignPerformanceResponse {
    private UUID id;
    private UUID advertiserId;
    private UUID campaignId;
    private LocalDate date;
    private long impressions;
    private long clicks;
    private long conversions;
    private BigDecimal spend;
}
