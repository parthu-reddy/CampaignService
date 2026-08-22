package com.fooddelivery.ad.campaign.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import com.fooddelivery.ad.campaign.enums.CampaignStatus;
import java.util.UUID;
import java.time.Instant;
import java.math.BigDecimal;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "campaigns")
@EntityListeners(AuditingEntityListener.class)
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "advertiser_id", nullable = false)
    private UUID advertiserId;
    @Column(name = "name", nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status", columnDefinition = "campaign_status", nullable = false)
    private CampaignStatus status = CampaignStatus.DRAFT;
    @Column(name = "daily_budget", nullable = false, precision = 19, scale = 4)
    private BigDecimal dailyBudget;
    @Column(name = "lifetime_budget", nullable = false, precision = 19, scale = 4)
    private BigDecimal lifetimeBudget;
    @Column(name = "max_bid", nullable = false, precision = 19, scale = 4)
    private BigDecimal maxBid;
    @Column(name = "start_date", nullable = false)
    private Instant startDate;
    @Column(name = "end_date")
    private Instant endDate;
    @Column(name = "frequency_cap", nullable = false)
    private Integer frequencyCap = 5;
    @Version
    @Column(name = "version", nullable = false)
    private Long version;
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @java.lang.SuppressWarnings("all")
    public Campaign() {
    }

    @java.lang.SuppressWarnings("all")
    public UUID getId() {
        return this.id;
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
    public CampaignStatus getStatus() {
        return this.status;
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
    public Long getVersion() {
        return this.version;
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
    public void setAdvertiserId(final UUID advertiserId) {
        this.advertiserId = advertiserId;
    }

    @java.lang.SuppressWarnings("all")
    public void setName(final String name) {
        this.name = name;
    }

    @java.lang.SuppressWarnings("all")
    public void setStatus(final CampaignStatus status) {
        this.status = status;
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

    @java.lang.SuppressWarnings("all")
    public void setVersion(final Long version) {
        this.version = version;
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
        if (!(o instanceof Campaign)) return false;
        Campaign other = (Campaign) o;
        return id != null && id.equals(other.getId());
    }

    @java.lang.Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
