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
    @jakarta.validation.constraints.NotNull
    private UUID id;
    @jakarta.validation.constraints.NotNull
    private UUID campaignId;
    @jakarta.validation.constraints.NotNull
    private String name;
    private GeoTargeting geoTargeting;
    private DaypartingConfig daypartingConfig;

    private ContextualKeywords contextualKeywords;
    private List<String> brandSafetyBlocklist;
    @jakarta.validation.constraints.NotNull
    private boolean active;
    @jakarta.validation.constraints.NotNull
    private Instant createdAt;
    @jakarta.validation.constraints.NotNull
    private Instant updatedAt;
}
