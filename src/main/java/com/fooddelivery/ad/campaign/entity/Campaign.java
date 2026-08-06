package com.fooddelivery.ad.campaign.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import com.fooddelivery.ad.campaign.enums.CampaignStatus;
import java.util.UUID;
import java.time.Instant;
import java.math.BigDecimal;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
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
    
    @Column(name = "brand_safety_blocklist", columnDefinition = "TEXT")
    private String brandSafetyBlocklist; // JSON array of blocked domains
    
    @Column(name = "end_date")
    private Instant endDate;
    
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
