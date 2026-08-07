package com.fooddelivery.ad.campaign.entity;

import jakarta.persistence.*;
import java.util.UUID;
import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "ad_groups")
@EntityListeners(AuditingEntityListener.class)
public class AdGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "campaign_id", nullable = false)
    private UUID campaignId;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "geo_targeting", columnDefinition = "TEXT")
    private String geoTargeting; // JSON representation of boundaries
    @Column(name = "dayparting_config", columnDefinition = "TEXT")
    private String daypartingConfig; // JSON representation of time slots
    @Column(name = "demographic_targeting", columnDefinition = "TEXT")
    private String demographicTargeting; // JSON representation of demographics
    @Column(name = "behavioral_targeting", columnDefinition = "TEXT")
    private String behavioralTargeting; // JSON representation of behaviors
    @Column(name = "contextual_keywords", columnDefinition = "TEXT")
    private String contextualKeywords; // JSON array of keywords
    @Column(name = "active", nullable = false)
    private boolean active = true;
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @java.lang.SuppressWarnings("all")
    public AdGroup() {
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
    public String getName() {
        return this.name;
    }

    @java.lang.SuppressWarnings("all")
    public String getGeoTargeting() {
        return this.geoTargeting;
    }

    @java.lang.SuppressWarnings("all")
    public String getDaypartingConfig() {
        return this.daypartingConfig;
    }

    @java.lang.SuppressWarnings("all")
    public String getDemographicTargeting() {
        return this.demographicTargeting;
    }

    @java.lang.SuppressWarnings("all")
    public String getBehavioralTargeting() {
        return this.behavioralTargeting;
    }

    @java.lang.SuppressWarnings("all")
    public String getContextualKeywords() {
        return this.contextualKeywords;
    }

    @java.lang.SuppressWarnings("all")
    public boolean isActive() {
        return this.active;
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
    public void setName(final String name) {
        this.name = name;
    }

    @java.lang.SuppressWarnings("all")
    public void setGeoTargeting(final String geoTargeting) {
        this.geoTargeting = geoTargeting;
    }

    @java.lang.SuppressWarnings("all")
    public void setDaypartingConfig(final String daypartingConfig) {
        this.daypartingConfig = daypartingConfig;
    }

    @java.lang.SuppressWarnings("all")
    public void setDemographicTargeting(final String demographicTargeting) {
        this.demographicTargeting = demographicTargeting;
    }

    @java.lang.SuppressWarnings("all")
    public void setBehavioralTargeting(final String behavioralTargeting) {
        this.behavioralTargeting = behavioralTargeting;
    }

    @java.lang.SuppressWarnings("all")
    public void setContextualKeywords(final String contextualKeywords) {
        this.contextualKeywords = contextualKeywords;
    }

    @java.lang.SuppressWarnings("all")
    public void setActive(final boolean active) {
        this.active = active;
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
        if (!(o instanceof AdGroup)) return false;
        final AdGroup other = (AdGroup) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        if (this.isActive() != other.isActive()) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$campaignId = this.getCampaignId();
        final java.lang.Object other$campaignId = other.getCampaignId();
        if (this$campaignId == null ? other$campaignId != null : !this$campaignId.equals(other$campaignId)) return false;
        final java.lang.Object this$name = this.getName();
        final java.lang.Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final java.lang.Object this$geoTargeting = this.getGeoTargeting();
        final java.lang.Object other$geoTargeting = other.getGeoTargeting();
        if (this$geoTargeting == null ? other$geoTargeting != null : !this$geoTargeting.equals(other$geoTargeting)) return false;
        final java.lang.Object this$daypartingConfig = this.getDaypartingConfig();
        final java.lang.Object other$daypartingConfig = other.getDaypartingConfig();
        if (this$daypartingConfig == null ? other$daypartingConfig != null : !this$daypartingConfig.equals(other$daypartingConfig)) return false;
        final java.lang.Object this$demographicTargeting = this.getDemographicTargeting();
        final java.lang.Object other$demographicTargeting = other.getDemographicTargeting();
        if (this$demographicTargeting == null ? other$demographicTargeting != null : !this$demographicTargeting.equals(other$demographicTargeting)) return false;
        final java.lang.Object this$behavioralTargeting = this.getBehavioralTargeting();
        final java.lang.Object other$behavioralTargeting = other.getBehavioralTargeting();
        if (this$behavioralTargeting == null ? other$behavioralTargeting != null : !this$behavioralTargeting.equals(other$behavioralTargeting)) return false;
        final java.lang.Object this$contextualKeywords = this.getContextualKeywords();
        final java.lang.Object other$contextualKeywords = other.getContextualKeywords();
        if (this$contextualKeywords == null ? other$contextualKeywords != null : !this$contextualKeywords.equals(other$contextualKeywords)) return false;
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
        return other instanceof AdGroup;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (this.isActive() ? 79 : 97);
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $campaignId = this.getCampaignId();
        result = result * PRIME + ($campaignId == null ? 43 : $campaignId.hashCode());
        final java.lang.Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final java.lang.Object $geoTargeting = this.getGeoTargeting();
        result = result * PRIME + ($geoTargeting == null ? 43 : $geoTargeting.hashCode());
        final java.lang.Object $daypartingConfig = this.getDaypartingConfig();
        result = result * PRIME + ($daypartingConfig == null ? 43 : $daypartingConfig.hashCode());
        final java.lang.Object $demographicTargeting = this.getDemographicTargeting();
        result = result * PRIME + ($demographicTargeting == null ? 43 : $demographicTargeting.hashCode());
        final java.lang.Object $behavioralTargeting = this.getBehavioralTargeting();
        result = result * PRIME + ($behavioralTargeting == null ? 43 : $behavioralTargeting.hashCode());
        final java.lang.Object $contextualKeywords = this.getContextualKeywords();
        result = result * PRIME + ($contextualKeywords == null ? 43 : $contextualKeywords.hashCode());
        final java.lang.Object $createdAt = this.getCreatedAt();
        result = result * PRIME + ($createdAt == null ? 43 : $createdAt.hashCode());
        final java.lang.Object $updatedAt = this.getUpdatedAt();
        result = result * PRIME + ($updatedAt == null ? 43 : $updatedAt.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "AdGroup(id=" + this.getId() + ", campaignId=" + this.getCampaignId() + ", name=" + this.getName() + ", geoTargeting=" + this.getGeoTargeting() + ", daypartingConfig=" + this.getDaypartingConfig() + ", demographicTargeting=" + this.getDemographicTargeting() + ", behavioralTargeting=" + this.getBehavioralTargeting() + ", contextualKeywords=" + this.getContextualKeywords() + ", active=" + this.isActive() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ")";
    }
}
