package com.fooddelivery.ad.campaign.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CampaignBudgetValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCampaignBudget {
    String message() default "Invalid campaign budget configuration";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
