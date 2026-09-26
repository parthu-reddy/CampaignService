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
    /** First day, on the advertiser's calendar. */
    @jakarta.validation.constraints.NotNull
    private java.time.LocalDate startDate;
    /** Last day (inclusive), on the advertiser's calendar. */
    private java.time.LocalDate endDate;
    /** The advertiser's IANA zone, which the dates are in. */
    @jakarta.validation.constraints.NotNull
    private String timeZone;
    @jakarta.validation.constraints.NotNull
    private Integer frequencyCap;
    @jakarta.validation.constraints.NotNull
    private Long version;

}
