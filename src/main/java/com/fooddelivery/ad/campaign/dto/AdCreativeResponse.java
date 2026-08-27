package com.fooddelivery.ad.campaign.dto;

import com.fooddelivery.ad.campaign.enums.AdFormat;
import com.fooddelivery.ad.campaign.enums.CreativeAuditStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdCreativeResponse {
    @jakarta.validation.constraints.NotNull
    private UUID id;
    @jakarta.validation.constraints.NotNull
    private UUID adGroupId;
    @jakarta.validation.constraints.NotNull
    private AdFormat format;
    @jakarta.validation.constraints.NotNull
    private String assetUrl;
    private String vastXml;
    @jakarta.validation.constraints.NotNull
    private CreativeAuditStatus auditStatus;
    @jakarta.validation.constraints.NotNull
    private Instant createdAt;
    @jakarta.validation.constraints.NotNull
    private Instant updatedAt;
}
