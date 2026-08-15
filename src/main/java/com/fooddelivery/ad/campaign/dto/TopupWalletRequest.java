package com.fooddelivery.ad.campaign.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class TopupWalletRequest {

    @NotNull
    @Positive
    private BigDecimal amount;

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
}
