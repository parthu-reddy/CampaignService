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
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    public void approve() {
        if (this.auditStatus != CreativeAuditStatus.PENDING) {
            throw new IllegalStateException("Can only approve PENDING creatives");
        }
        this.auditStatus = CreativeAuditStatus.APPROVED;
    }

    public void reject() {
        if (this.auditStatus != CreativeAuditStatus.PENDING) {
            throw new IllegalStateException("Can only reject PENDING creatives");
        }
        this.auditStatus = CreativeAuditStatus.REJECTED;
    }
}
