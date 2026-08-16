package com.fooddelivery.ad.campaign.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class TopupWalletRequest {

    @NotNull
    @Positive
    private BigDecimal amount;

    private String gatewayName;

    public TopupWalletRequest() {}

    public TopupWalletRequest(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }
}
