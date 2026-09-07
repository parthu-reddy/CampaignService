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
@EntityListeners(AuditingEntityListener.class)@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.Data

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

}
