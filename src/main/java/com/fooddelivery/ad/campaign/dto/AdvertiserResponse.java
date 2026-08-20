package com.fooddelivery.ad.campaign.dto;

import lombok.Data;
import java.util.UUID;
import java.time.Instant;

@Data
public class AdvertiserResponse {
    private UUID id;
    private String userId;
    private String companyName;
    private String externalRef;
    private UUID walletBalanceId;
    private Instant createdAt;
    private Instant updatedAt;
}
