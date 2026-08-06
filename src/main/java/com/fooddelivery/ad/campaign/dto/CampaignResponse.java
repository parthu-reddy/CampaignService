package com.fooddelivery.ad.campaign.dto;

import lombok.Data;
import java.util.UUID;
import java.time.Instant;
import java.math.BigDecimal;
import com.fooddelivery.ad.campaign.enums.CampaignStatus;

@Data
public class CampaignResponse {
    private UUID id;
    private UUID advertiserId;
    private String name;
    private CampaignStatus status;
    private BigDecimal dailyBudget;
    private BigDecimal lifetimeBudget;
    private BigDecimal maxBid;
    private Instant startDate;
    private Instant endDate;
    private Long version;
}
