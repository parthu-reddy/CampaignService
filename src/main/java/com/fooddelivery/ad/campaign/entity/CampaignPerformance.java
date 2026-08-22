package com.fooddelivery.ad.campaign.entity;

import jakarta.persistence.*;
import java.util.UUID;
import java.time.LocalDate;
import java.math.BigDecimal;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.Instant;

@Entity
@Table(name = "campaign_performance")
@EntityListeners(AuditingEntityListener.class)
public class CampaignPerformance {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "campaign_id", nullable = false)
    private UUID campaignId;
    @Column(name = "advertiser_id", nullable = false)
    private UUID advertiserId;
    @Column(name = "date", nullable = false)
    private LocalDate date;
    @Column(name = "impressions", nullable = false)
    private long impressions = 0;
    @Column(name = "clicks", nullable = false)
    private long clicks = 0;
    @Column(name = "conversions", nullable = false)
    private long conversions = 0;
    @Column(name = "spend", nullable = false, precision = 19, scale = 4)
    private BigDecimal spend = BigDecimal.ZERO;
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @java.lang.SuppressWarnings("all")
    public CampaignPerformance() {
    }

    @java.lang.SuppressWarnings("all")
    public UUID getId() {
        return this.id;
    }

    @java.lang.SuppressWarnings("all")
    public UUID getCampaignId() {
        return this.campaignId;
    }

    @java.lang.SuppressWarnings("all")
    public UUID getAdvertiserId() {
        return this.advertiserId;
    }

    @java.lang.SuppressWarnings("all")
    public LocalDate getDate() {
        return this.date;
    }

    @java.lang.SuppressWarnings("all")
    public long getImpressions() {
        return this.impressions;
    }

    @java.lang.SuppressWarnings("all")
    public long getClicks() {
        return this.clicks;
    }

    @java.lang.SuppressWarnings("all")
    public long getConversions() {
        return this.conversions;
    }

    @java.lang.SuppressWarnings("all")
    public BigDecimal getSpend() {
        return this.spend;
    }

    @java.lang.SuppressWarnings("all")
    public Instant getCreatedAt() {
        return this.createdAt;
    }

    @java.lang.SuppressWarnings("all")
    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    @java.lang.SuppressWarnings("all")
    public void setId(final UUID id) {
        this.id = id;
    }

    @java.lang.SuppressWarnings("all")
    public void setCampaignId(final UUID campaignId) {
        this.campaignId = campaignId;
    }

    @java.lang.SuppressWarnings("all")
    public void setAdvertiserId(final UUID advertiserId) {
        this.advertiserId = advertiserId;
    }

    @java.lang.SuppressWarnings("all")
    public void setDate(final LocalDate date) {
        this.date = date;
    }

    @java.lang.SuppressWarnings("all")
    public void setImpressions(final long impressions) {
        this.impressions = impressions;
    }

    @java.lang.SuppressWarnings("all")
    public void setClicks(final long clicks) {
        this.clicks = clicks;
    }

    @java.lang.SuppressWarnings("all")
    public void setConversions(final long conversions) {
        this.conversions = conversions;
    }

    @java.lang.SuppressWarnings("all")
    public void setSpend(final BigDecimal spend) {
        this.spend = spend;
    }

    @java.lang.SuppressWarnings("all")
    public void setCreatedAt(final Instant createdAt) {
        this.createdAt = createdAt;
    }

    @java.lang.SuppressWarnings("all")
    public void setUpdatedAt(final Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @java.lang.Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CampaignPerformance)) return false;
        CampaignPerformance other = (CampaignPerformance) o;
        return id != null && id.equals(other.getId());
    }

    @java.lang.Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
