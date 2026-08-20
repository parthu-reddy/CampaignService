package com.fooddelivery.ad.campaign.entity;

import jakarta.persistence.*;
import java.util.UUID;
import java.util.List;
import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.fooddelivery.common.dto.targeting.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "geo_targeting", columnDefinition = "jsonb")
    private GeoTargeting geoTargeting;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dayparting_config", columnDefinition = "jsonb")
    private DaypartingConfig daypartingConfig;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "demographic_targeting", columnDefinition = "jsonb")
    private DemographicTargeting demographicTargeting;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "behavioral_targeting", columnDefinition = "jsonb")
    private BehavioralTargeting behavioralTargeting;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "contextual_keywords", columnDefinition = "jsonb")
    private ContextualKeywords contextualKeywords;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "brand_safety_blocklist", columnDefinition = "jsonb")
    private List<String> brandSafetyBlocklist;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
