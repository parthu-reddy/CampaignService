package com.fooddelivery.ad.campaign.kafka;

public class InvalidTrackingEventException extends RuntimeException {
    public InvalidTrackingEventException(String message) {
        super(message);
    }
}
