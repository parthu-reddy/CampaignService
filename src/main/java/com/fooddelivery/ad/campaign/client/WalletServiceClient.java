package com.fooddelivery.ad.campaign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "wallet-service")
public interface WalletServiceClient {
    
    @GetMapping("/api/v1/wallets/{entityId}/{entityType}")
    Object getWallet(@PathVariable("entityId") UUID entityId, @PathVariable("entityType") String entityType);
}
