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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdvertiserProfile)) return false;
        AdvertiserProfile other = (AdvertiserProfile) o;
        return id != null && id.equals(other.getId());
    }

    @java.lang.Override
    public int hashCode() {
        return getClass().hashCode();
    }


}
