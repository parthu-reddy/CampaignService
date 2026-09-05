package com.fooddelivery.ad.campaign.controller;

import com.fooddelivery.common.dto.campaign.CampaignPacingDTO;
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
/*
 * Every caller is background work -- BiddingEngine's IndexReconciliationScheduler and
 * IndexBootstrapService, and BudgetLimitingService's PacingEngineService -- so these can require
 * SERVICE. That became expressible only once FeignSecurityInterceptor started minting a signed
 * SERVICE identity for calls with no principal; before it, any annotation here would have 403'd ad
 * index reconciliation.
 */
public class InternalCampaignController {

    private final CampaignRepository campaignRepository;
    private final com.fooddelivery.ad.campaign.repository.AdGroupRepository adGroupRepository;

    public InternalCampaignController(CampaignRepository campaignRepository, com.fooddelivery.ad.campaign.repository.AdGroupRepository adGroupRepository) {
        this.campaignRepository = campaignRepository;
        this.adGroupRepository = adGroupRepository;
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('SERVICE', 'ADMIN')")
    @PostMapping("/batch/budgets")
    public ResponseEntity<Map<String, CampaignPacingDTO>> getDailyBudgets(@RequestBody List<String> campaignIds) {
        Map<String, CampaignPacingDTO> budgets = new HashMap<>();
        
        try {
            List<UUID> uuidList = campaignIds.stream()
                .map(UUID::fromString)
                .collect(java.util.stream.Collectors.toList());
                
            List<com.fooddelivery.ad.campaign.entity.Campaign> campaigns = campaignRepository.findAllById(uuidList);
            
            for (com.fooddelivery.ad.campaign.entity.Campaign campaign : campaigns) {
                if (campaign.getDailyBudget() != null) {
                    budgets.put(campaign.getId().toString(), new CampaignPacingDTO(
                        campaign.getDailyBudget().doubleValue(), 
                        campaign.getLifetimeBudget() != null ? campaign.getLifetimeBudget().doubleValue() : null, 
                        campaign.getAdvertiserId()
                    ));
                }
            }
        } catch (Exception e) {
            log.error("Failed to fetch budget for campaigns batch", e);
        }
        
        return ResponseEntity.ok(budgets);
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('SERVICE', 'ADMIN')")
    @GetMapping("/{campaignId}/advertiser")
    public ResponseEntity<Map<String, String>> getCampaignAdvertiser(@PathVariable UUID campaignId) {
        return campaignRepository.findById(campaignId)
                .map(campaign -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("advertiserId", campaign.getAdvertiserId().toString());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('SERVICE', 'ADMIN')")
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
            
            com.fooddelivery.common.dto.targeting.TargetingSummary targeting = buildTargetingSummary(
                adGroupRepository.findByCampaignIdAndActiveTrue(c.getId()));
            if (targeting != null) {
                map.put("targeting", targeting);
            }
            
            return map;
        }).collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    private com.fooddelivery.common.dto.targeting.TargetingSummary buildTargetingSummary(
            java.util.List<com.fooddelivery.ad.campaign.entity.AdGroup> adGroups) {

        java.util.List<String> regions = new java.util.ArrayList<>();
        java.util.List<com.fooddelivery.common.dto.targeting.DaypartingConfig.Daypart> dayparts = new java.util.ArrayList<>();
        java.util.List<String> keywords = new java.util.ArrayList<>();
        java.util.List<String> blocklist = new java.util.ArrayList<>();

        for (com.fooddelivery.ad.campaign.entity.AdGroup group : adGroups) {
            if (group.getGeoTargeting() != null && group.getGeoTargeting().getRegions() != null) {
                regions.addAll(group.getGeoTargeting().getRegions());
            }
            if (group.getDaypartingConfig() != null && group.getDaypartingConfig().getDayparts() != null) {
                dayparts.addAll(group.getDaypartingConfig().getDayparts());
            }
            if (group.getContextualKeywords() != null && group.getContextualKeywords().getKeywords() != null) {
                keywords.addAll(group.getContextualKeywords().getKeywords());
            }
            if (group.getBrandSafetyBlocklist() != null) {
                blocklist.addAll(group.getBrandSafetyBlocklist());
            }
        }

        com.fooddelivery.common.dto.targeting.TargetingSummary summary =
                new com.fooddelivery.common.dto.targeting.TargetingSummary();
        if (!regions.isEmpty()) {
            summary.setGeoTargeting(new com.fooddelivery.common.dto.targeting.GeoTargeting(distinct(regions)));
        }
        if (!dayparts.isEmpty()) {
            summary.setDaypartingConfig(new com.fooddelivery.common.dto.targeting.DaypartingConfig(distinct(dayparts)));
        }
        if (!keywords.isEmpty()) {
            summary.setContextualKeywords(new com.fooddelivery.common.dto.targeting.ContextualKeywords(distinct(keywords)));
        }
        if (!blocklist.isEmpty()) {
            summary.setBrandSafetyBlocklist(distinct(blocklist));
        }
        return summary;
    }

    private static <T> java.util.List<T> distinct(java.util.List<T> values) {
        return values.stream().distinct().collect(java.util.stream.Collectors.toList());
    }
}
