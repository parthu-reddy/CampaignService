package com.fooddelivery.ad.campaign.client;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component("campaignPaymentClientFallback")
public class PaymentClientFallback implements PaymentClient {
    @Override
    public String createOrder(String gateway, Map<String, Object> request) {
        throw new IllegalStateException("Payment service is currently unavailable. Failing fast to ensure financial integrity.");
    }
}
