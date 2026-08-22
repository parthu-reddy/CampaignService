package com.fooddelivery.ad.campaign.controller;

import com.fooddelivery.ad.campaign.dto.CampaignRequest;
import com.fooddelivery.ad.campaign.dto.CampaignResponse;
import com.fooddelivery.ad.campaign.dto.TopupWalletRequest;
import com.fooddelivery.ad.campaign.service.CampaignService;
import com.fooddelivery.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import com.fooddelivery.common.client.WalletServiceClient;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/advertisers/{advertiserId}/campaigns")
@lombok.extern.slf4j.Slf4j
public class CampaignController {
    @java.lang.SuppressWarnings("all")

    private final CampaignService campaignService;
    private final WalletServiceClient walletClient;
    private final com.fooddelivery.ad.campaign.service.CampaignSecurityHelper campaignSecurityHelper;

    public CampaignController(final CampaignService campaignService, final WalletServiceClient walletClient, final com.fooddelivery.ad.campaign.service.CampaignSecurityHelper campaignSecurityHelper) {
        this.campaignService = campaignService;
        this.walletClient = walletClient;
        this.campaignSecurityHelper = campaignSecurityHelper;
    }

    private Long parseIfMatch(String ifMatch) {
        if (ifMatch == null || ifMatch.isBlank()) return null;
        return Long.parseLong(ifMatch.replace("\"", ""));
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<CampaignResponse>> createCampaign(@PathVariable UUID advertiserId, @RequestHeader(value = "X-User-Id", required = false) String userId, @Validated @RequestBody CampaignRequest request) {
        campaignSecurityHelper.verifyAccess(userId, advertiserId);
        if (!advertiserId.equals(request.getAdvertiserId())) {
            throw new IllegalArgumentException("Path advertiserId must match request payload");
        }
        CampaignResponse response = campaignService.createCampaign(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .eTag("\"" + response.getVersion() + "\"")
                .body(ApiResponse.success(response, "Campaign created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CampaignResponse>> getCampaign(@PathVariable UUID advertiserId, @RequestHeader(value = "X-User-Id", required = false) String userId, @PathVariable UUID id) {
        campaignSecurityHelper.verifyAccess(userId, advertiserId);
        CampaignResponse response = campaignService.getCampaign(id, advertiserId);
        return ResponseEntity.ok()
                .eTag("\"" + response.getVersion() + "\"")
                .body(ApiResponse.success(response, "Campaign retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CampaignResponse>> updateCampaign(@PathVariable UUID advertiserId, @PathVariable UUID id, @RequestHeader(value = "X-User-Id", required = false) String userId, @RequestHeader(value = "If-Match", required = false) String ifMatchHeader, @Validated @RequestBody CampaignRequest request) {
        campaignSecurityHelper.verifyAccess(userId, advertiserId);
        if (ifMatchHeader == null || ifMatchHeader.isBlank()) {
            throw new ResponseStatusException(HttpStatus.PRECONDITION_REQUIRED, "If-Match header is required for updates");
        }
        Long version = parseIfMatch(ifMatchHeader);
        CampaignResponse response = campaignService.updateCampaign(id, advertiserId, request, version);
        return ResponseEntity.ok()
                .eTag("\"" + response.getVersion() + "\"")
                .body(ApiResponse.success(response, "Campaign updated successfully"));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<CampaignResponse>> activateCampaign(@PathVariable UUID advertiserId, @RequestHeader(value = "X-User-Id", required = false) String userId, @PathVariable UUID id) {
        campaignSecurityHelper.verifyAccess(userId, advertiserId);
        CampaignResponse response = campaignService.activateCampaign(id, advertiserId);
        return ResponseEntity.ok()
                .eTag("\"" + response.getVersion() + "\"")
                .body(ApiResponse.success(response, "Campaign activated successfully"));
    }

    @PostMapping("/{id}/pause")
    public ResponseEntity<ApiResponse<Void>> pauseCampaign(@PathVariable UUID advertiserId, @RequestHeader(value = "X-User-Id", required = false) String userId, @PathVariable UUID id) {
        campaignSecurityHelper.verifyAccess(userId, advertiserId);
        campaignService.pauseCampaign(id, advertiserId);
        return ResponseEntity.ok(ApiResponse.success(null, "Campaign paused successfully"));
    }

    @PostMapping("/{id}/resume")
    public ResponseEntity<ApiResponse<Void>> resumeCampaign(@PathVariable UUID advertiserId, @RequestHeader(value = "X-User-Id", required = false) String userId, @PathVariable UUID id) {
        campaignSecurityHelper.verifyAccess(userId, advertiserId);
        campaignService.resumeCampaign(id, advertiserId);
        return ResponseEntity.ok(ApiResponse.success(null, "Campaign resumed successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCampaign(@PathVariable UUID advertiserId, @RequestHeader(value = "X-User-Id", required = false) String userId, @PathVariable UUID id) {
        campaignSecurityHelper.verifyAccess(userId, advertiserId);
        campaignService.deleteCampaign(id, advertiserId);
        return ResponseEntity.ok(ApiResponse.success(null, "Campaign deleted successfully"));
    }

    @PostMapping("/wallet/topup")
    public ResponseEntity<ApiResponse<Map<String, String>>> topupWallet(@PathVariable UUID advertiserId, @RequestHeader(value = "X-User-Id", required = false) String userId, @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey, @Validated @RequestBody TopupWalletRequest request) {
        campaignSecurityHelper.verifyAccess(userId, advertiserId);
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            idempotencyKey = UUID.randomUUID().toString();
        }
        
        // Let WalletServiceClient handle the request. Use Object because TopupWalletRequest is in campaign service dto package, but compatible format.
        ApiResponse<Map<String, String>> gatewayResponse = walletClient.topupWallet(advertiserId, request, idempotencyKey);
        
        return ResponseEntity.ok(gatewayResponse);
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<CampaignResponse>>> getCampaigns(@PathVariable UUID advertiserId, @RequestHeader(value = "X-User-Id", required = false) String userId, @org.springframework.data.web.PageableDefault(size = 20) org.springframework.data.domain.Pageable pageable) {
        campaignSecurityHelper.verifyAccess(userId, advertiserId);
        return ResponseEntity.ok(ApiResponse.success(campaignService.getCampaignsByAdvertiser(advertiserId, pageable), "Campaigns retrieved successfully"));
    }

    @GetMapping("/{id}/performance")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<com.fooddelivery.ad.campaign.dto.CampaignPerformanceResponse>>> getCampaignPerformance(
            @PathVariable UUID advertiserId, 
            @RequestHeader(value = "X-User-Id", required = false) String userId, 
            @PathVariable UUID id,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @org.springframework.data.web.PageableDefault(size = 20) org.springframework.data.domain.Pageable pageable) {
        campaignSecurityHelper.verifyAccess(userId, advertiserId);
        if (to == null) to = LocalDate.now(java.time.ZoneOffset.UTC);
        if (from == null) from = to.minusDays(30);
        if (from.isBefore(to.minusDays(90))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date range cannot exceed 90 days");
        }
        return ResponseEntity.ok(ApiResponse.success(campaignService.getCampaignPerformance(id, advertiserId, from, to, pageable), "Performance retrieved successfully"));
    }

    @GetMapping("/performance")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<com.fooddelivery.ad.campaign.dto.CampaignPerformanceResponse>>> getAllCampaignPerformance(
            @PathVariable UUID advertiserId, 
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @org.springframework.data.web.PageableDefault(size = 20) org.springframework.data.domain.Pageable pageable) {
        campaignSecurityHelper.verifyAccess(userId, advertiserId);
        if (to == null) to = LocalDate.now(java.time.ZoneOffset.UTC);
        if (from == null) from = to.minusDays(30);
        if (from.isBefore(to.minusDays(90))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date range cannot exceed 90 days");
        }
        return ResponseEntity.ok(ApiResponse.success(campaignService.getAllCampaignPerformance(advertiserId, from, to, pageable), "Performance retrieved successfully"));
    }
}
