package com.fooddelivery.ad.campaign.controller;

import com.fooddelivery.ad.campaign.dto.CampaignPacingDTO;
import com.fooddelivery.ad.campaign.repository.CampaignRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/internal/campaigns")
@lombok.extern.slf4j.Slf4j
public class InternalCampaignController {

    private final CampaignRepository campaignRepository;

    public InternalCampaignController(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    @PostMapping("/batch/budgets")
    public ResponseEntity<Map<String, CampaignPacingDTO>> getDailyBudgets(@RequestBody List<String> campaignIds) {
        Map<String, CampaignPacingDTO> budgets = new HashMap<>();
        for (String id : campaignIds) {
            try {
                com.fooddelivery.ad.campaign.entity.Campaign campaign = campaignRepository.findAllById(java.util.Collections.singletonList(UUID.fromString(id))).stream().findFirst().orElse(null);
                if (campaign != null && campaign.getDailyBudget() != null) {
                    budgets.put(id, new CampaignPacingDTO(campaign.getDailyBudget().doubleValue(), campaign.getLifetimeBudget() != null ? campaign.getLifetimeBudget().doubleValue() : null, campaign.getAdvertiserId()));
                }
            } catch (Exception e) {
                log.error("Failed to fetch budget for campaign " + id, e);
            }
        }
        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/active-for-bidding")
    public ResponseEntity<List<Map<String, Object>>> getActiveCampaignsForBidding() {
        // In a real implementation this would fetch ACTIVE campaigns from repository
        List<com.fooddelivery.ad.campaign.entity.Campaign> activeCampaigns = campaignRepository.findByStatus(com.fooddelivery.ad.campaign.enums.CampaignStatus.ACTIVE);
        
        List<Map<String, Object>> response = activeCampaigns.stream().map(c -> {
            if (c.getMaxBid() == null) {
                throw new IllegalStateException("Missing maxBid for active campaign: " + c.getId());
            }
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId().toString());
            map.put("advertiserId", c.getAdvertiserId().toString());
            map.put("maxBid", c.getMaxBid());
            return map;
        }).collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
}
