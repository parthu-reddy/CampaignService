package com.fooddelivery.ad.campaign.controller;

import com.fooddelivery.ad.campaign.dto.AdCreativeRequest;
import com.fooddelivery.ad.campaign.dto.AdCreativeResponse;
import com.fooddelivery.ad.campaign.service.AdCreativeService;
import com.fooddelivery.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.UUID;
import java.util.List;
import java.util.Map;

@RestController
public class AdCreativeController {

    private final AdCreativeService adCreativeService;
    private final com.fooddelivery.ad.campaign.service.CampaignSecurityHelper campaignSecurityHelper;

    public AdCreativeController(AdCreativeService adCreativeService,
                                com.fooddelivery.ad.campaign.service.CampaignSecurityHelper campaignSecurityHelper) {
        this.adCreativeService = adCreativeService;
        this.campaignSecurityHelper = campaignSecurityHelper;
    }

    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    @PostMapping("/api/v1/advertisers/{advertiserId}/campaigns/{campaignId}/ad-groups/{adGroupId}/creatives/upload-url")
    public ResponseEntity<ApiResponse<String>> generateUploadUrl(
            @PathVariable UUID advertiserId,
            @PathVariable UUID campaignId,
            @PathVariable UUID adGroupId,
            @RequestParam String fileName,
            @RequestParam String contentType,
            @RequestHeader(value = com.fooddelivery.common.constants.HeaderConstants.HEADER_USER_ID, required = false) String userId) {
        campaignSecurityHelper.verifyAccess(userId, advertiserId);
        String url = adCreativeService.generateUploadUrl(advertiserId, campaignId, adGroupId, fileName, contentType);
        return ResponseEntity.ok(ApiResponse.success(url, "Presigned URL generated successfully"));
    }

    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    @PostMapping("/api/v1/advertisers/{advertiserId}/campaigns/{campaignId}/ad-groups/{adGroupId}/creatives")
    public ResponseEntity<ApiResponse<AdCreativeResponse>> createCreative(
            @PathVariable UUID advertiserId,
            @PathVariable UUID campaignId,
            @PathVariable UUID adGroupId,
            @Valid @RequestBody AdCreativeRequest request,
            @RequestHeader(value = com.fooddelivery.common.constants.HeaderConstants.HEADER_USER_ID, required = false) String userId) {
        campaignSecurityHelper.verifyAccess(userId, advertiserId);
        AdCreativeResponse response = adCreativeService.createCreative(advertiserId, campaignId, adGroupId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Ad creative created successfully"));
    }

    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    @GetMapping("/api/v1/advertisers/{advertiserId}/campaigns/{campaignId}/ad-groups/{adGroupId}/creatives")
    public ResponseEntity<ApiResponse<List<AdCreativeResponse>>> listCreatives(
            @PathVariable UUID advertiserId,
            @PathVariable UUID campaignId,
            @PathVariable UUID adGroupId,
            @RequestHeader(value = com.fooddelivery.common.constants.HeaderConstants.HEADER_USER_ID, required = false) String userId) {
        campaignSecurityHelper.verifyAccess(userId, advertiserId);
        List<AdCreativeResponse> response = adCreativeService.listCreatives(advertiserId, campaignId, adGroupId);
        return ResponseEntity.ok(ApiResponse.success(response, "Ad creatives fetched successfully"));
    }

    /** Creative moderation: an internal action, not something an advertiser performs. */
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('SERVICE', 'ADMIN')")
    @PostMapping("/api/v1/internal/creatives/{creativeId}/audit")
    public ResponseEntity<ApiResponse<Void>> auditCreative(
            @PathVariable UUID creativeId,
            @RequestBody Map<String, Object> payload) {
        boolean approve = (Boolean) payload.getOrDefault("approve", false);
        String reason = (String) payload.getOrDefault("reason", "");
        adCreativeService.auditCreative(creativeId, approve, reason);
        return ResponseEntity.ok(ApiResponse.success(null, "Ad creative audit completed successfully"));
    }
}
