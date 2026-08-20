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
    private UUID id;
    private UUID adGroupId;
    private AdFormat format;
    private String assetUrl;
    private String vastXml;
    private CreativeAuditStatus auditStatus;
    private Instant createdAt;
    private Instant updatedAt;
}
