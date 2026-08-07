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
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof CampaignPerformance)) return false;
        final CampaignPerformance other = (CampaignPerformance) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        if (this.getImpressions() != other.getImpressions()) return false;
        if (this.getClicks() != other.getClicks()) return false;
        if (this.getConversions() != other.getConversions()) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$campaignId = this.getCampaignId();
        final java.lang.Object other$campaignId = other.getCampaignId();
        if (this$campaignId == null ? other$campaignId != null : !this$campaignId.equals(other$campaignId)) return false;
        final java.lang.Object this$advertiserId = this.getAdvertiserId();
        final java.lang.Object other$advertiserId = other.getAdvertiserId();
        if (this$advertiserId == null ? other$advertiserId != null : !this$advertiserId.equals(other$advertiserId)) return false;
        final java.lang.Object this$date = this.getDate();
        final java.lang.Object other$date = other.getDate();
        if (this$date == null ? other$date != null : !this$date.equals(other$date)) return false;
        final java.lang.Object this$spend = this.getSpend();
        final java.lang.Object other$spend = other.getSpend();
        if (this$spend == null ? other$spend != null : !this$spend.equals(other$spend)) return false;
        final java.lang.Object this$createdAt = this.getCreatedAt();
        final java.lang.Object other$createdAt = other.getCreatedAt();
        if (this$createdAt == null ? other$createdAt != null : !this$createdAt.equals(other$createdAt)) return false;
        final java.lang.Object this$updatedAt = this.getUpdatedAt();
        final java.lang.Object other$updatedAt = other.getUpdatedAt();
        if (this$updatedAt == null ? other$updatedAt != null : !this$updatedAt.equals(other$updatedAt)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof CampaignPerformance;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final long $impressions = this.getImpressions();
        result = result * PRIME + (int) ($impressions >>> 32 ^ $impressions);
        final long $clicks = this.getClicks();
        result = result * PRIME + (int) ($clicks >>> 32 ^ $clicks);
        final long $conversions = this.getConversions();
        result = result * PRIME + (int) ($conversions >>> 32 ^ $conversions);
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $campaignId = this.getCampaignId();
        result = result * PRIME + ($campaignId == null ? 43 : $campaignId.hashCode());
        final java.lang.Object $advertiserId = this.getAdvertiserId();
        result = result * PRIME + ($advertiserId == null ? 43 : $advertiserId.hashCode());
        final java.lang.Object $date = this.getDate();
        result = result * PRIME + ($date == null ? 43 : $date.hashCode());
        final java.lang.Object $spend = this.getSpend();
        result = result * PRIME + ($spend == null ? 43 : $spend.hashCode());
        final java.lang.Object $createdAt = this.getCreatedAt();
        result = result * PRIME + ($createdAt == null ? 43 : $createdAt.hashCode());
        final java.lang.Object $updatedAt = this.getUpdatedAt();
        result = result * PRIME + ($updatedAt == null ? 43 : $updatedAt.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "CampaignPerformance(id=" + this.getId() + ", campaignId=" + this.getCampaignId() + ", advertiserId=" + this.getAdvertiserId() + ", date=" + this.getDate() + ", impressions=" + this.getImpressions() + ", clicks=" + this.getClicks() + ", conversions=" + this.getConversions() + ", spend=" + this.getSpend() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ")";
    }
}
