package com.fooddelivery.ad.campaign.controller;

import com.fooddelivery.ad.campaign.dto.CampaignRequest;
import com.fooddelivery.ad.campaign.dto.CampaignResponse;
import com.fooddelivery.ad.campaign.service.CampaignService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import com.fooddelivery.ad.campaign.client.PaymentClient;

@RestController
@RequestMapping("/api/v1/advertisers/{advertiserId}/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;
    private final PaymentClient paymentClient;

    @PostMapping
    public ResponseEntity<CampaignResponse> createCampaign(
            @PathVariable UUID advertiserId,
            @Validated @RequestBody CampaignRequest request) {
        
        if (!advertiserId.equals(request.getAdvertiserId())) {
            throw new IllegalArgumentException("Path advertiserId must match request payload");
        }
        
        return new ResponseEntity<>(campaignService.createCampaign(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CampaignResponse> updateCampaign(
            @PathVariable UUID advertiserId,
            @PathVariable UUID id,
            @RequestHeader("If-Match") Long version,
            @Validated @RequestBody CampaignRequest request) {
            
        return ResponseEntity.ok(campaignService.updateCampaign(id, request, version));
    }

    @PostMapping("/{id}/pause")
    public ResponseEntity<Void> pauseCampaign(
            @PathVariable UUID advertiserId,
            @PathVariable UUID id) {
        campaignService.pauseCampaign(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/wallet/topup")
    public ResponseEntity<Map<String, String>> topupWallet(
            @PathVariable UUID advertiserId,
            @RequestBody Map<String, Object> request) {
        
        BigDecimal amountInInr = new BigDecimal(request.get("amount").toString());
        
        Map<String, Object> paymentReq = new HashMap<>();
        // Note: internalOrderId must match the WALLET_{uuid}_{uuid} format or WALLET_{uuid}
        paymentReq.put("internalOrderId", "WALLET_" + advertiserId.toString() + "_" + UUID.randomUUID().toString().substring(0, 8));
        paymentReq.put("amountInInr", amountInInr);
        
        String intentOrOrderId = paymentClient.createOrder("RAZORPAY", paymentReq);
        
        Map<String, String> response = new HashMap<>();
        response.put("orderId", intentOrOrderId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<CampaignResponse>> getCampaigns(
            @PathVariable UUID advertiserId,
            @org.springframework.data.web.PageableDefault(size = 20) org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(campaignService.getCampaignsByAdvertiser(advertiserId, pageable));
    }
}