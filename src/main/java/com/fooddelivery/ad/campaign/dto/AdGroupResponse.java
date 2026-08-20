package com.fooddelivery.ad.campaign.dto;

import com.fooddelivery.common.dto.targeting.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.UUID;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdGroupResponse {
    private UUID id;
    private UUID campaignId;
    private String name;
    private GeoTargeting geoTargeting;
    private DaypartingConfig daypartingConfig;
    private DemographicTargeting demographicTargeting;
    private BehavioralTargeting behavioralTargeting;
    private ContextualKeywords contextualKeywords;
    private List<String> brandSafetyBlocklist;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
