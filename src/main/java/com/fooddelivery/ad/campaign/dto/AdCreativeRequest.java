package com.fooddelivery.ad.campaign.dto;

import com.fooddelivery.ad.campaign.enums.AdFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdCreativeRequest {
    @NotNull(message = "Format cannot be null")
    private AdFormat format;
    private String assetUrl;
    private String vastXml;
}
