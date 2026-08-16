package com.fooddelivery.ad.campaign.controller;

import com.fooddelivery.ad.campaign.dto.CampaignRequest;
import com.fooddelivery.ad.campaign.dto.CampaignResponse;
import com.fooddelivery.ad.campaign.dto.TopupWalletRequest;
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
import com.fooddelivery.common.client.PaymentServiceClient;
import com.fooddelivery.common.dto.payment.CreateOrderRequest;
import com.fooddelivery.ad.campaign.entity.CampaignPerformance;
import com.fooddelivery.ad.campaign.repository.CampaignPerformanceRepository;

@RestController
@RequestMapping("/api/v1/advertisers/{advertiserId}/campaigns")
@lombok.extern.slf4j.Slf4j
public class CampaignController {
    @java.lang.SuppressWarnings("all")

    private final CampaignService campaignService;
    private final PaymentServiceClient paymentClient;
    private final CampaignPerformanceRepository performanceRepository;

    @PostMapping
    public ResponseEntity<CampaignResponse> createCampaign(@PathVariable UUID advertiserId, @RequestHeader(value = "X-User-Id", required = false) String userId, @Validated @RequestBody CampaignRequest request) {
        // Gateway handles RBAC. A restaurant user (userId) manages the restaurant (advertiserId).

        if (!advertiserId.equals(request.getAdvertiserId())) {
            throw new IllegalArgumentException("Path advertiserId must match request payload");
        }
        return new ResponseEntity<>(campaignService.createCampaign(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CampaignResponse> updateCampaign(@PathVariable UUID advertiserId, @PathVariable UUID id, @RequestHeader(value = "X-User-Id", required = false) String userId, @RequestHeader(value = "If-Match", required = false) Long version, @Validated @RequestBody CampaignRequest request) {
        // Gateway handles RBAC. A restaurant user (userId) manages the restaurant (advertiserId).
        return ResponseEntity.ok(campaignService.updateCampaign(id, request, version));
    }

    @PostMapping("/{id}/pause")
    public ResponseEntity<Void> pauseCampaign(@PathVariable UUID advertiserId, @RequestHeader(value = "X-User-Id", required = false) String userId, @PathVariable UUID id) {
        // Gateway handles RBAC. A restaurant user (userId) manages the restaurant (advertiserId).
        campaignService.pauseCampaign(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/wallet/topup")
    public ResponseEntity<Map<String, String>> topupWallet(@PathVariable UUID advertiserId, @RequestHeader(value = "X-User-Id", required = false) String userId, @Validated @RequestBody TopupWalletRequest request) {
        // Gateway handles RBAC. A restaurant user (userId) manages the restaurant (advertiserId).
        BigDecimal amountInInr = request.getAmount();
        String internalOrderId = "WALLET_" + advertiserId.toString() + "_" + UUID.randomUUID().toString();
        CreateOrderRequest paymentReq = new CreateOrderRequest(internalOrderId, amountInInr);
        String gateway = request.getGatewayName() != null && !request.getGatewayName().isBlank() ? request.getGatewayName() : "RAZORPAY";
        String intentOrOrderId = paymentClient.createOrder(gateway, paymentReq);
        Map<String, String> response = new HashMap<>();
        response.put("orderId", intentOrOrderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<CampaignResponse>> getCampaigns(@PathVariable UUID advertiserId, @RequestHeader(value = "X-User-Id", required = false) String userId, @org.springframework.data.web.PageableDefault(size = 20) org.springframework.data.domain.Pageable pageable) {
        // Gateway handles RBAC. A restaurant user (userId) manages the restaurant (advertiserId).
        return ResponseEntity.ok(campaignService.getCampaignsByAdvertiser(advertiserId, pageable));
    }

    @GetMapping("/{id}/performance")
    public ResponseEntity<List<CampaignPerformance>> getCampaignPerformance(@PathVariable UUID advertiserId, @RequestHeader(value = "X-User-Id", required = false) String userId, @PathVariable UUID id) {
        // Gateway handles RBAC. A restaurant user (userId) manages the restaurant (advertiserId).
        return ResponseEntity.ok(performanceRepository.findByCampaignIdOrderByDateDesc(id));
    }

    @GetMapping("/performance")
    public ResponseEntity<List<CampaignPerformance>> getAllCampaignPerformance(@PathVariable UUID advertiserId, @RequestHeader(value = "X-User-Id", required = false) String userId) {
        // Gateway handles RBAC. A restaurant user (userId) manages the restaurant (advertiserId).
        return ResponseEntity.ok(performanceRepository.findByAdvertiserIdOrderByDateDesc(advertiserId));
    }

    @java.lang.SuppressWarnings("all")
    public CampaignController(final CampaignService campaignService, final PaymentServiceClient paymentClient, final CampaignPerformanceRepository performanceRepository) {
        this.campaignService = campaignService;
        this.paymentClient = paymentClient;
        this.performanceRepository = performanceRepository;
    }
}
