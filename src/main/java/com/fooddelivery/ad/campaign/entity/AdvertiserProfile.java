package com.fooddelivery.ad.campaign.entity;

import jakarta.persistence.*;
import java.util.UUID;
import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "advertiser_profiles")
@EntityListeners(AuditingEntityListener.class)
public class AdvertiserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId; // Links to IdentityService
    
    @Column(name = "external_ref", unique = true)
    private String externalRef; // Links to external systems (e.g. brandId)
    
    @Column(name = "company_name", nullable = false)
    private String companyName;
    @Column(name = "wallet_balance_id")
    private UUID walletBalanceId; // Links to BillingWalletService
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @java.lang.SuppressWarnings("all")
    public AdvertiserProfile() {
    }

    @java.lang.SuppressWarnings("all")
    public UUID getId() {
        return this.id;
    }

    @java.lang.SuppressWarnings("all")
    public String getUserId() {
        return this.userId;
    }

    @java.lang.SuppressWarnings("all")
    public String getExternalRef() {
        return this.externalRef;
    }

    @java.lang.SuppressWarnings("all")
    public String getCompanyName() {
        return this.companyName;
    }

    @java.lang.SuppressWarnings("all")
    public UUID getWalletBalanceId() {
        return this.walletBalanceId;
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
    public void setUserId(final String userId) {
        this.userId = userId;
    }

    @java.lang.SuppressWarnings("all")
    public void setExternalRef(final String externalRef) {
        this.externalRef = externalRef;
    }

    @java.lang.SuppressWarnings("all")
    public void setCompanyName(final String companyName) {
        this.companyName = companyName;
    }

    @java.lang.SuppressWarnings("all")
    public void setWalletBalanceId(final UUID walletBalanceId) {
        this.walletBalanceId = walletBalanceId;
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
        if (!(o instanceof AdvertiserProfile)) return false;
        final AdvertiserProfile other = (AdvertiserProfile) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$userId = this.getUserId();
        final java.lang.Object other$userId = other.getUserId();
        if (this$userId == null ? other$userId != null : !this$userId.equals(other$userId)) return false;
        final java.lang.Object this$companyName = this.getCompanyName();
        final java.lang.Object other$companyName = other.getCompanyName();
        if (this$companyName == null ? other$companyName != null : !this$companyName.equals(other$companyName)) return false;
        final java.lang.Object this$walletBalanceId = this.getWalletBalanceId();
        final java.lang.Object other$walletBalanceId = other.getWalletBalanceId();
        if (this$walletBalanceId == null ? other$walletBalanceId != null : !this$walletBalanceId.equals(other$walletBalanceId)) return false;
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
        return other instanceof AdvertiserProfile;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $userId = this.getUserId();
        result = result * PRIME + ($userId == null ? 43 : $userId.hashCode());
        final java.lang.Object $companyName = this.getCompanyName();
        result = result * PRIME + ($companyName == null ? 43 : $companyName.hashCode());
        final java.lang.Object $walletBalanceId = this.getWalletBalanceId();
        result = result * PRIME + ($walletBalanceId == null ? 43 : $walletBalanceId.hashCode());
        final java.lang.Object $createdAt = this.getCreatedAt();
        result = result * PRIME + ($createdAt == null ? 43 : $createdAt.hashCode());
        final java.lang.Object $updatedAt = this.getUpdatedAt();
        result = result * PRIME + ($updatedAt == null ? 43 : $updatedAt.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "AdvertiserProfile(id=" + this.getId() + ", userId=" + this.getUserId() + ", companyName=" + this.getCompanyName() + ", walletBalanceId=" + this.getWalletBalanceId() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ")";
    }
}
