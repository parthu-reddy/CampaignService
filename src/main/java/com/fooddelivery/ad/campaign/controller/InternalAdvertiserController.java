package com.fooddelivery.ad.campaign.controller;

import com.fooddelivery.ad.campaign.repository.AdvertiserProfileRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/internal/advertisers")
@lombok.extern.slf4j.Slf4j
public class InternalAdvertiserController {

    private final AdvertiserProfileRepository advertiserProfileRepository;

    public InternalAdvertiserController(AdvertiserProfileRepository advertiserProfileRepository) {
        this.advertiserProfileRepository = advertiserProfileRepository;
    }

    @PreAuthorize("hasAnyRole('SERVICE', 'ADMIN')")
    @GetMapping("/{advertiserId}/owner")
    public ResponseEntity<Map<String, String>> getAdvertiserUserId(@PathVariable UUID advertiserId) {
        return advertiserProfileRepository.findById(advertiserId)
                .map(profile -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("advertiserId", advertiserId.toString());
                    response.put("userId", profile.getUserId());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
