package com.fooddelivery.ad.campaign.exception;

import com.fooddelivery.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.fooddelivery.ad.campaign")
@lombok.extern.slf4j.Slf4j
public class CampaignGlobalExceptionHandler {

    @ExceptionHandler(EventSerializationException.class)
    public ResponseEntity<ApiResponse<Void>> handleEventSerializationException(EventSerializationException ex) {
        log.error("Failed to serialize outbox event payload", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Internal Server Error: Failed to serialize event data."));
    }
}
