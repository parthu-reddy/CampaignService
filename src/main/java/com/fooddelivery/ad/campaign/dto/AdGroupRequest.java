package com.fooddelivery.ad.campaign.dto;

import com.fooddelivery.common.dto.targeting.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdGroupRequest {
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Valid
    private GeoTargeting geoTargeting;

    @Valid
    private DaypartingConfig daypartingConfig;


    @Valid
    private ContextualKeywords contextualKeywords;

    private List<String> brandSafetyBlocklist;

    private boolean active = true;
}
