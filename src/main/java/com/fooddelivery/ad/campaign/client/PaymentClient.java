package com.fooddelivery.ad.campaign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "payment-gateway-service")
public interface PaymentClient {

    @PostMapping("/api/v1/payments/create-order")
    String createOrder(
            @RequestParam("gateway") String gateway,
            @RequestBody Map<String, Object> request
    );
}
