package com.fooddelivery.ad.campaign.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/advertiser")
@lombok.extern.slf4j.Slf4j
public class AdvertiserPortalController {
    @java.lang.SuppressWarnings("all")

    @PostMapping("/register")
    public ResponseEntity<String> registerAdvertiser() {
        return ResponseEntity.ok("Advertiser registered successfully");
    }

    @GetMapping("/{advertiserId}/presigned-url")
    public ResponseEntity<String> getPresignedUrlForUpload(@PathVariable UUID advertiserId, @RequestParam String fileName) {
        // Mock generation of presigned URL for S3/GCS
        String mockUrl = String.format("https://storage.googleapis.com/fooddelivery-creatives/upload/%s/%s?signature=mock_sig", advertiserId, fileName);
        return ResponseEntity.ok(mockUrl);
    }
}
