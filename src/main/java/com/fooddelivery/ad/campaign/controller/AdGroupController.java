package com.fooddelivery.ad.campaign.controller;

import com.fooddelivery.ad.campaign.dto.AdGroupRequest;
import com.fooddelivery.ad.campaign.dto.AdGroupResponse;
import com.fooddelivery.ad.campaign.service.AdGroupService;
import com.fooddelivery.common.dto.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/advertisers/{advertiserId}/campaigns/{campaignId}/ad-groups")
public class AdGroupController {

    private final AdGroupService adGroupService;
    private final com.fooddelivery.ad.campaign.service.CampaignSecurityHelper campaignSecurityHelper;

    public AdGroupController(AdGroupService adGroupService,
                             com.fooddelivery.ad.campaign.service.CampaignSecurityHelper campaignSecurityHelper) {
        this.adGroupService = adGroupService;
        this.campaignSecurityHelper = campaignSecurityHelper;
    }

    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ApiResponse<AdGroupResponse>> createAdGroup(
            @PathVariable UUID advertiserId,
            @PathVariable UUID campaignId,
            @Valid @RequestBody AdGroupRequest request,
            @RequestHeader(value = com.fooddelivery.common.constants.HeaderConstants.HEADER_USER_ID, required = false) String userId) {
        campaignSecurityHelper.verifyAccess(userId, advertiserId);
        AdGroupResponse response = adGroupService.createAdGroup(advertiserId, campaignId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Ad group created successfully"));
    }

    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdGroupResponse>>> listAdGroups(
            @PathVariable UUID advertiserId,
            @PathVariable UUID campaignId,
            Pageable pageable,
            @RequestHeader(value = com.fooddelivery.common.constants.HeaderConstants.HEADER_USER_ID, required = false) String userId) {
        campaignSecurityHelper.verifyAccess(userId, advertiserId);
        Page<AdGroupResponse> response = adGroupService.listAdGroups(advertiserId, campaignId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Ad groups fetched successfully"));
    }

    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    @GetMapping("/{adGroupId}")
    public ResponseEntity<ApiResponse<AdGroupResponse>> getAdGroup(
            @PathVariable UUID advertiserId,
            @PathVariable UUID campaignId,
            @PathVariable UUID adGroupId,
            @RequestHeader(value = com.fooddelivery.common.constants.HeaderConstants.HEADER_USER_ID, required = false) String userId) {
        campaignSecurityHelper.verifyAccess(userId, advertiserId);
        AdGroupResponse response = adGroupService.getAdGroup(advertiserId, campaignId, adGroupId);
        return ResponseEntity.ok(ApiResponse.success(response, "Ad group fetched successfully"));
    }

    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    @PutMapping("/{adGroupId}")
    public ResponseEntity<ApiResponse<AdGroupResponse>> updateAdGroup(
            @PathVariable UUID advertiserId,
            @PathVariable UUID campaignId,
            @PathVariable UUID adGroupId,
            @Valid @RequestBody AdGroupRequest request,
            @RequestHeader(value = com.fooddelivery.common.constants.HeaderConstants.HEADER_USER_ID, required = false) String userId) {
        campaignSecurityHelper.verifyAccess(userId, advertiserId);
        AdGroupResponse response = adGroupService.updateAdGroup(advertiserId, campaignId, adGroupId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Ad group updated successfully"));
    }

    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{adGroupId}")
    public ResponseEntity<ApiResponse<Void>> deleteAdGroup(
            @PathVariable UUID advertiserId,
            @PathVariable UUID campaignId,
            @PathVariable UUID adGroupId,
            @RequestHeader(value = com.fooddelivery.common.constants.HeaderConstants.HEADER_USER_ID, required = false) String userId) {
        campaignSecurityHelper.verifyAccess(userId, advertiserId);
        adGroupService.deleteAdGroup(advertiserId, campaignId, adGroupId);
        return ResponseEntity.ok(ApiResponse.success(null, "Ad group deleted successfully"));
    }
}
