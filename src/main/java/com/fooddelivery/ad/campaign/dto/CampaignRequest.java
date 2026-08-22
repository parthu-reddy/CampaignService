package com.fooddelivery.ad.campaign.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@com.fooddelivery.ad.campaign.validation.ValidCampaignBudget
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

    @java.lang.SuppressWarnings("all")
    public CampaignRequest() {
    }

    @java.lang.SuppressWarnings("all")
    public UUID getAdvertiserId() {
        return this.advertiserId;
    }

    @java.lang.SuppressWarnings("all")
    public String getName() {
        return this.name;
    }

    @java.lang.SuppressWarnings("all")
    public BigDecimal getDailyBudget() {
        return this.dailyBudget;
    }

    @java.lang.SuppressWarnings("all")
    public BigDecimal getLifetimeBudget() {
        return this.lifetimeBudget;
    }

    @java.lang.SuppressWarnings("all")
    public BigDecimal getMaxBid() {
        return this.maxBid;
    }

    @java.lang.SuppressWarnings("all")
    public Instant getStartDate() {
        return this.startDate;
    }

    @java.lang.SuppressWarnings("all")
    public Instant getEndDate() {
        return this.endDate;
    }

    @java.lang.SuppressWarnings("all")
    public Integer getFrequencyCap() {
        return this.frequencyCap;
    }

    @java.lang.SuppressWarnings("all")
    public void setAdvertiserId(final UUID advertiserId) {
        this.advertiserId = advertiserId;
    }

    @java.lang.SuppressWarnings("all")
    public void setName(final String name) {
        this.name = name;
    }

    @java.lang.SuppressWarnings("all")
    public void setDailyBudget(final BigDecimal dailyBudget) {
        this.dailyBudget = dailyBudget;
    }

    @java.lang.SuppressWarnings("all")
    public void setLifetimeBudget(final BigDecimal lifetimeBudget) {
        this.lifetimeBudget = lifetimeBudget;
    }

    @java.lang.SuppressWarnings("all")
    public void setMaxBid(final BigDecimal maxBid) {
        this.maxBid = maxBid;
    }

    @java.lang.SuppressWarnings("all")
    public void setStartDate(final Instant startDate) {
        this.startDate = startDate;
    }

    @java.lang.SuppressWarnings("all")
    public void setEndDate(final Instant endDate) {
        this.endDate = endDate;
    }

    @java.lang.SuppressWarnings("all")
    public void setFrequencyCap(final Integer frequencyCap) {
        this.frequencyCap = frequencyCap;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof CampaignRequest)) return false;
        final CampaignRequest other = (CampaignRequest) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$advertiserId = this.getAdvertiserId();
        final java.lang.Object other$advertiserId = other.getAdvertiserId();
        if (this$advertiserId == null ? other$advertiserId != null : !this$advertiserId.equals(other$advertiserId)) return false;
        final java.lang.Object this$name = this.getName();
        final java.lang.Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final java.lang.Object this$dailyBudget = this.getDailyBudget();
        final java.lang.Object other$dailyBudget = other.getDailyBudget();
        if (this$dailyBudget == null ? other$dailyBudget != null : !this$dailyBudget.equals(other$dailyBudget)) return false;
        final java.lang.Object this$lifetimeBudget = this.getLifetimeBudget();
        final java.lang.Object other$lifetimeBudget = other.getLifetimeBudget();
        if (this$lifetimeBudget == null ? other$lifetimeBudget != null : !this$lifetimeBudget.equals(other$lifetimeBudget)) return false;
        final java.lang.Object this$maxBid = this.getMaxBid();
        final java.lang.Object other$maxBid = other.getMaxBid();
        if (this$maxBid == null ? other$maxBid != null : !this$maxBid.equals(other$maxBid)) return false;
        final java.lang.Object this$startDate = this.getStartDate();
        final java.lang.Object other$startDate = other.getStartDate();
        if (this$startDate == null ? other$startDate != null : !this$startDate.equals(other$startDate)) return false;
        final java.lang.Object this$endDate = this.getEndDate();
        final java.lang.Object other$endDate = other.getEndDate();
        if (this$endDate == null ? other$endDate != null : !this$endDate.equals(other$endDate)) return false;
        final java.lang.Object this$frequencyCap = this.getFrequencyCap();
        final java.lang.Object other$frequencyCap = other.getFrequencyCap();
        if (this$frequencyCap == null ? other$frequencyCap != null : !this$frequencyCap.equals(other$frequencyCap)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof CampaignRequest;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $advertiserId = this.getAdvertiserId();
        result = result * PRIME + ($advertiserId == null ? 43 : $advertiserId.hashCode());
        final java.lang.Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final java.lang.Object $dailyBudget = this.getDailyBudget();
        result = result * PRIME + ($dailyBudget == null ? 43 : $dailyBudget.hashCode());
        final java.lang.Object $lifetimeBudget = this.getLifetimeBudget();
        result = result * PRIME + ($lifetimeBudget == null ? 43 : $lifetimeBudget.hashCode());
        final java.lang.Object $maxBid = this.getMaxBid();
        result = result * PRIME + ($maxBid == null ? 43 : $maxBid.hashCode());
        final java.lang.Object $startDate = this.getStartDate();
        result = result * PRIME + ($startDate == null ? 43 : $startDate.hashCode());
        final java.lang.Object $endDate = this.getEndDate();
        result = result * PRIME + ($endDate == null ? 43 : $endDate.hashCode());
        final java.lang.Object $frequencyCap = this.getFrequencyCap();
        result = result * PRIME + ($frequencyCap == null ? 43 : $frequencyCap.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "CampaignRequest(advertiserId=" + this.getAdvertiserId() + ", name=" + this.getName() + ", dailyBudget=" + this.getDailyBudget() + ", lifetimeBudget=" + this.getLifetimeBudget() + ", maxBid=" + this.getMaxBid() + ", startDate=" + this.getStartDate() + ", endDate=" + this.getEndDate() + ", frequencyCap=" + this.getFrequencyCap() + ")";
    }
}
