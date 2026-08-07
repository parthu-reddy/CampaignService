package com.fooddelivery.ad.campaign.entity;

import jakarta.persistence.*;
import com.fooddelivery.ad.campaign.enums.AdFormat;
import com.fooddelivery.ad.campaign.enums.CreativeAuditStatus;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import java.util.UUID;
import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "ad_creatives")
@EntityListeners(AuditingEntityListener.class)
public class AdCreative {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "ad_group_id", nullable = false)
    private UUID adGroupId;
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "format", columnDefinition = "ad_format", nullable = false)
    private AdFormat format;
    @Column(name = "asset_url", nullable = false, length = 1024)
    private String assetUrl;
    @Column(name = "vast_xml", columnDefinition = "TEXT")
    private String vastXml; // VAST XML for video ads
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "audit_status", columnDefinition = "creative_audit_status", nullable = false)
    private CreativeAuditStatus auditStatus = CreativeAuditStatus.PENDING;
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @java.lang.SuppressWarnings("all")
    public AdCreative() {
    }

    @java.lang.SuppressWarnings("all")
    public UUID getId() {
        return this.id;
    }

    @java.lang.SuppressWarnings("all")
    public UUID getAdGroupId() {
        return this.adGroupId;
    }

    @java.lang.SuppressWarnings("all")
    public AdFormat getFormat() {
        return this.format;
    }

    @java.lang.SuppressWarnings("all")
    public String getAssetUrl() {
        return this.assetUrl;
    }

    @java.lang.SuppressWarnings("all")
    public String getVastXml() {
        return this.vastXml;
    }

    @java.lang.SuppressWarnings("all")
    public CreativeAuditStatus getAuditStatus() {
        return this.auditStatus;
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
    public void setAdGroupId(final UUID adGroupId) {
        this.adGroupId = adGroupId;
    }

    @java.lang.SuppressWarnings("all")
    public void setFormat(final AdFormat format) {
        this.format = format;
    }

    @java.lang.SuppressWarnings("all")
    public void setAssetUrl(final String assetUrl) {
        this.assetUrl = assetUrl;
    }

    @java.lang.SuppressWarnings("all")
    public void setVastXml(final String vastXml) {
        this.vastXml = vastXml;
    }

    @java.lang.SuppressWarnings("all")
    public void setAuditStatus(final CreativeAuditStatus auditStatus) {
        this.auditStatus = auditStatus;
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
        if (!(o instanceof AdCreative)) return false;
        final AdCreative other = (AdCreative) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$adGroupId = this.getAdGroupId();
        final java.lang.Object other$adGroupId = other.getAdGroupId();
        if (this$adGroupId == null ? other$adGroupId != null : !this$adGroupId.equals(other$adGroupId)) return false;
        final java.lang.Object this$format = this.getFormat();
        final java.lang.Object other$format = other.getFormat();
        if (this$format == null ? other$format != null : !this$format.equals(other$format)) return false;
        final java.lang.Object this$assetUrl = this.getAssetUrl();
        final java.lang.Object other$assetUrl = other.getAssetUrl();
        if (this$assetUrl == null ? other$assetUrl != null : !this$assetUrl.equals(other$assetUrl)) return false;
        final java.lang.Object this$vastXml = this.getVastXml();
        final java.lang.Object other$vastXml = other.getVastXml();
        if (this$vastXml == null ? other$vastXml != null : !this$vastXml.equals(other$vastXml)) return false;
        final java.lang.Object this$auditStatus = this.getAuditStatus();
        final java.lang.Object other$auditStatus = other.getAuditStatus();
        if (this$auditStatus == null ? other$auditStatus != null : !this$auditStatus.equals(other$auditStatus)) return false;
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
        return other instanceof AdCreative;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $adGroupId = this.getAdGroupId();
        result = result * PRIME + ($adGroupId == null ? 43 : $adGroupId.hashCode());
        final java.lang.Object $format = this.getFormat();
        result = result * PRIME + ($format == null ? 43 : $format.hashCode());
        final java.lang.Object $assetUrl = this.getAssetUrl();
        result = result * PRIME + ($assetUrl == null ? 43 : $assetUrl.hashCode());
        final java.lang.Object $vastXml = this.getVastXml();
        result = result * PRIME + ($vastXml == null ? 43 : $vastXml.hashCode());
        final java.lang.Object $auditStatus = this.getAuditStatus();
        result = result * PRIME + ($auditStatus == null ? 43 : $auditStatus.hashCode());
        final java.lang.Object $createdAt = this.getCreatedAt();
        result = result * PRIME + ($createdAt == null ? 43 : $createdAt.hashCode());
        final java.lang.Object $updatedAt = this.getUpdatedAt();
        result = result * PRIME + ($updatedAt == null ? 43 : $updatedAt.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "AdCreative(id=" + this.getId() + ", adGroupId=" + this.getAdGroupId() + ", format=" + this.getFormat() + ", assetUrl=" + this.getAssetUrl() + ", vastXml=" + this.getVastXml() + ", auditStatus=" + this.getAuditStatus() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ")";
    }
}
