package com.fooddelivery.ad.campaign.scheduler;

import com.fooddelivery.ad.campaign.entity.Campaign;
import com.fooddelivery.ad.campaign.enums.CampaignStatus;
import com.fooddelivery.ad.campaign.repository.CampaignRepository;
import com.fooddelivery.ad.campaign.service.CampaignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.time.Duration;

@Slf4j
@Component
// The contract-test profile excludes Redis autoconfiguration, and a scheduler has nothing to do
// in a contract test. Mirrors IdempotencyFilter and RateLimitingService.RateLimitConfig.
@org.springframework.context.annotation.Profile("!contract-test")
public class CampaignLifecycleScheduler {

    private final CampaignRepository campaignRepository;
    private final CampaignService campaignService;
    private final StringRedisTemplate redisTemplate;
    private final Clock clock;
    
    private static final String LOCK_KEY = "lock:campaign_lifecycle_sweeper";

    public CampaignLifecycleScheduler(CampaignRepository campaignRepository, CampaignService campaignService, StringRedisTemplate redisTemplate, Clock clock) {
        this.campaignRepository = campaignRepository;
        this.campaignService = campaignService;
        this.redisTemplate = redisTemplate;
        this.clock = clock;
    }

    @Scheduled(fixedDelayString = "${campaign.lifecycle.interval.ms:60000}")
    @Transactional
    public void sweepCampaigns() {
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(LOCK_KEY, "locked", Duration.ofSeconds(30));
        if (Boolean.TRUE.equals(acquired)) {
            try {
                java.time.Instant now = java.time.Instant.now(clock);
                
                // SCHEDULED and startDate <= now -> ACTIVE
                List<Campaign> scheduledCampaigns = campaignRepository.findByStatus(CampaignStatus.SCHEDULED);
                for (Campaign campaign : scheduledCampaigns) {
                    if (!campaign.getStartDate().isAfter(now)) {
                        log.info("Activating scheduled campaign {}", campaign.getId());
                        campaignService.resumeCampaign(campaign.getId(), campaign.getAdvertiserId());
                    }
                }
                
                // ACTIVE and endDate < now -> COMPLETED
                List<Campaign> activeCampaigns = campaignRepository.findByStatus(CampaignStatus.ACTIVE);
                for (Campaign campaign : activeCampaigns) {
                    if (campaign.getEndDate() != null && campaign.getEndDate().isBefore(now)) {
                        log.info("Completing expired campaign {}", campaign.getId());
                        campaignService.completeCampaign(campaign.getId(), campaign.getAdvertiserId());
                    }
                }
            } finally {
                redisTemplate.delete(LOCK_KEY);
            }
        } else {
            log.debug("Could not acquire lock for campaign lifecycle sweeper");
        }
    }
}
