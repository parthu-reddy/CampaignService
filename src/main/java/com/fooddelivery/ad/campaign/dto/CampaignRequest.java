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
    /**
     * The first and last days of the campaign, as calendar dates on the advertiser's calendar. The
     * server turns them into instants in the advertiser's zone (AdvertiserCalendar), so a campaign runs
     * all of its last day wherever the advertiser is. They used to be instants the browser made from
     * UTC midnight, which ended an Indian campaign at 05:30 and started an American one the day before.
     */
    @NotNull
    private java.time.LocalDate startDate;
    private java.time.LocalDate endDate;
    @PositiveOrZero
    private Integer frequencyCap;

}
