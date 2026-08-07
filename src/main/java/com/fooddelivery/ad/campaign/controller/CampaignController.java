package com.fooddelivery.ad.campaign.controller;

import com.fooddelivery.ad.campaign.dto.CampaignRequest;
import com.fooddelivery.ad.campaign.dto.CampaignResponse;
import com.fooddelivery.ad.campaign.service.CampaignService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import com.fooddelivery.ad.campaign.client.PaymentClient;
import com.fooddelivery.ad.campaign.entity.CampaignPerformance;
import com.fooddelivery.ad.campaign.repository.CampaignPerformanceRepository;

@RestController
@RequestMapping("/api/v1/advertisers/{advertiserId}/campaigns")
public class CampaignController {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CampaignController.class);
    private final CampaignService campaignService;
    private final PaymentClient paymentClient;
    private final CampaignPerformanceRepository performanceRepository;

    @PostMapping
    public ResponseEntity<CampaignResponse> createCampaign(@PathVariable UUID advertiserId, @RequestHeader(value = "X-User-Id", required = false) String userId, @Validated @RequestBody CampaignRequest request) {
        if (userId != null && !userId.equals(advertiserId.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (!advertiserId.equals(request.getAdvertiserId())) {
            throw new IllegalArgumentException("Path advertiserId must match request payload");
        }
        return new ResponseEntity<>(campaignService.createCampaign(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CampaignResponse> updateCampaign(@PathVariable UUID advertiserId, @PathVariable UUID id, @RequestHeader(value = "X-User-Id", required = false) String userId, @RequestHeader(value = "If-Match", required = false) Long version, @Validated @RequestBody CampaignRequest request) {
        if (userId != null && !userId.equals(advertiserId.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(campaignService.updateCampaign(id, request, version));
    }

    @PostMapping("/{id}/pause")
    public ResponseEntity<Void> pauseCampaign(@PathVariable UUID advertiserId, @RequestHeader(value = "X-User-Id", required = false) String userId, @PathVariable UUID id) {
        if (userId != null && !userId.equals(advertiserId.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        campaignService.pauseCampaign(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/wallet/topup")
    public ResponseEntity<Map<String, String>> topupWallet(@PathVariable UUID advertiserId, @RequestHeader(value = "X-User-Id", required = false) String userId, @RequestBody Map<String, Object> request) {
        if (userId != null && !userId.equals(advertiserId.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
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
    public ResponseEntity<org.springframework.data.domain.Page<CampaignResponse>> getCampaigns(@PathVariable UUID advertiserId, @RequestHeader(value = "X-User-Id", required = false) String userId, @org.springframework.data.web.PageableDefault(size = 20) org.springframework.data.domain.Pageable pageable) {
        if (userId != null && !userId.equals(advertiserId.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(campaignService.getCampaignsByAdvertiser(advertiserId, pageable));
    }

    @GetMapping("/{id}/performance")
    public ResponseEntity<List<CampaignPerformance>> getCampaignPerformance(@PathVariable UUID advertiserId, @RequestHeader(value = "X-User-Id", required = false) String userId, @PathVariable UUID id) {
        if (userId != null && !userId.equals(advertiserId.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(performanceRepository.findByCampaignIdOrderByDateDesc(id));
    }

    @GetMapping("/performance")
    public ResponseEntity<List<CampaignPerformance>> getAllCampaignPerformance(@PathVariable UUID advertiserId, @RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId != null && !userId.equals(advertiserId.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(performanceRepository.findByAdvertiserIdOrderByDateDesc(advertiserId));
    }

    @java.lang.SuppressWarnings("all")
    public CampaignController(final CampaignService campaignService, final PaymentClient paymentClient, final CampaignPerformanceRepository performanceRepository) {
        this.campaignService = campaignService;
        this.paymentClient = paymentClient;
        this.performanceRepository = performanceRepository;
    }
}
