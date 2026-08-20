package com.fooddelivery.ad.campaign.validation;

import com.fooddelivery.ad.campaign.dto.CampaignRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CampaignBudgetValidator implements ConstraintValidator<ValidCampaignBudget, CampaignRequest> {
    @Override
    public boolean isValid(CampaignRequest request, ConstraintValidatorContext context) {
        if (request == null) return true;
        boolean valid = true;

        if (request.getLifetimeBudget() != null && request.getDailyBudget() != null) {
            if (request.getDailyBudget().compareTo(request.getLifetimeBudget()) > 0) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("dailyBudget cannot exceed lifetimeBudget")
                       .addPropertyNode("dailyBudget")
                       .addConstraintViolation();
                valid = false;
            }
        }

        if (request.getMaxBid() != null && request.getDailyBudget() != null) {
            if (request.getMaxBid().compareTo(request.getDailyBudget()) > 0) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("maxBid cannot exceed dailyBudget")
                       .addPropertyNode("maxBid")
                       .addConstraintViolation();
                valid = false;
            }
        }

        return valid;
    }
}
