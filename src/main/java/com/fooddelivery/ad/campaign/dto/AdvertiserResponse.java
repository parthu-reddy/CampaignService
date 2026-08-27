package com.fooddelivery.ad.campaign.dto;

import lombok.Data;
import java.util.UUID;
import java.time.Instant;

@Data
public class AdvertiserResponse {
    @jakarta.validation.constraints.NotNull
    private UUID id;
    @jakarta.validation.constraints.NotNull
    private String userId;
    @jakarta.validation.constraints.NotNull
    private String companyName;
    private String externalRef;
    private UUID walletBalanceId;
    @jakarta.validation.constraints.NotNull
    private Instant createdAt;
    @jakarta.validation.constraints.NotNull
    private Instant updatedAt;
}
