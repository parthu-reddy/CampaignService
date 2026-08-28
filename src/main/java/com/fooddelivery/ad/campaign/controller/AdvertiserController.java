package com.fooddelivery.ad.campaign.controller;

import com.fooddelivery.ad.campaign.dto.AdvertiserRegistrationRequest;
import com.fooddelivery.ad.campaign.dto.AdvertiserResponse;
import com.fooddelivery.ad.campaign.service.AdvertiserService;
import com.fooddelivery.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/advertisers")
@lombok.extern.slf4j.Slf4j
public class AdvertiserController {

    private final AdvertiserService advertiserService;
    private final com.fooddelivery.ad.campaign.service.S3PresignedUrlService s3PresignedUrlService;
    private final com.fooddelivery.ad.campaign.service.CampaignSecurityHelper campaignSecurityHelper;

    public AdvertiserController(AdvertiserService advertiserService, com.fooddelivery.ad.campaign.service.S3PresignedUrlService s3PresignedUrlService,
                               com.fooddelivery.ad.campaign.service.CampaignSecurityHelper campaignSecurityHelper) {
        this.campaignSecurityHelper = campaignSecurityHelper;
        this.advertiserService = advertiserService;
        this.s3PresignedUrlService = s3PresignedUrlService;
    }

    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    @PostMapping("")
    public ResponseEntity<ApiResponse<AdvertiserResponse>> register(
            @RequestHeader(value = com.fooddelivery.common.constants.HeaderConstants.HEADER_USER_ID, required = true) String userId,
            @Validated @RequestBody AdvertiserRegistrationRequest request) {
        
        AdvertiserResponse response = advertiserService.registerAdvertiser(userId, request);
        return new ResponseEntity<>(ApiResponse.success(response, "Advertiser registered successfully"), HttpStatus.CREATED);
    }

    /** Reads one advertiser by id; previously any authenticated user could read any advertiser. */
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdvertiserResponse>> getAdvertiser(
            @PathVariable UUID id,
            @RequestHeader(value = com.fooddelivery.common.constants.HeaderConstants.HEADER_USER_ID, required = false) String userId) {
        campaignSecurityHelper.verifyAccess(userId, id);
        AdvertiserResponse response = advertiserService.getAdvertiser(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Advertiser details fetched successfully"));
    }

    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AdvertiserResponse>> getMyAdvertiser(
            @RequestHeader(value = com.fooddelivery.common.constants.HeaderConstants.HEADER_USER_ID, required = true) String userId) {
        AdvertiserResponse response = advertiserService.getAdvertiserByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Advertiser details fetched successfully"));
    }

    /**
     * Resolves an advertiser from an external reference. Ownership cannot be verified before the
     * lookup itself resolves an id, so this requires a principal but does not scope the result --
     * a known gap, recorded in FOLLOW_UPS item 13a rather than papered over.
     */
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    @GetMapping("")
    public ResponseEntity<ApiResponse<AdvertiserResponse>> getAdvertiserByExternalRef(
            @RequestParam(name = "externalRef") String externalRef) {
        AdvertiserResponse response = advertiserService.getAdvertiserByExternalRef(externalRef);
        return ResponseEntity.ok(ApiResponse.success(response, "Advertiser details fetched successfully"));
    }
    
    /** Issues an upload URL for one advertiser's assets, so ownership must be proven. */
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    @GetMapping("/{advertiserId}/presigned-url")
    public ResponseEntity<ApiResponse<String>> getPresignedUrlForUpload(
            @PathVariable UUID advertiserId, 
            @RequestParam String fileName,
            @RequestParam String contentType,
            @RequestHeader(value = com.fooddelivery.common.constants.HeaderConstants.HEADER_USER_ID, required = false) String userId) {
        campaignSecurityHelper.verifyAccess(userId, advertiserId);
        String url = s3PresignedUrlService.generatePresignedUrl(advertiserId, fileName, contentType);
        return ResponseEntity.ok(ApiResponse.success(url, "Presigned URL generated successfully"));
    }
}
