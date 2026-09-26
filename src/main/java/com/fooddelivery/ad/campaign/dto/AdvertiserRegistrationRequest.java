package com.fooddelivery.ad.campaign.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdvertiserRegistrationRequest {
    @NotBlank(message = "Company name is required")
    private String companyName;

    private String externalRef;

    /** IANA zone the advertiser's campaigns run in, e.g. Asia/Kolkata. Offsets such as +05:30 are refused: they have no DST rules. */
    @jakarta.validation.constraints.NotBlank(message = "Time zone is required")
    @com.fooddelivery.common.time.IanaTimeZone
    private String timeZone;
}
