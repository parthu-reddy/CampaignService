package com.fooddelivery.ad.campaign.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
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
}
