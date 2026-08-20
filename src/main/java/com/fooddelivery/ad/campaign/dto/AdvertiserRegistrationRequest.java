package com.fooddelivery.ad.campaign.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdvertiserRegistrationRequest {
    @NotBlank(message = "Company name is required")
    private String companyName;

    private String externalRef;
}
