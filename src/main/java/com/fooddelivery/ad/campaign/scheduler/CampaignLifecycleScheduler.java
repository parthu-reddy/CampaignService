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
    public void sweepCampaigns() {
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(LOCK_KEY, "locked", Duration.ofSeconds(30));
        if (Boolean.TRUE.equals(acquired)) {
            try {
                java.time.Instant now = java.time.Instant.now(clock);
                
                // SCHEDULED and startDate <= now -> ACTIVE
                int page = 0;
                while (true) {
                    org.springframework.data.domain.Page<Campaign> pageResult = campaignRepository.findByStatusAndStartDateLessThanEqual(
                            CampaignStatus.SCHEDULED, now, org.springframework.data.domain.PageRequest.of(page, 100));
                    for (Campaign campaign : pageResult) {
                        try {
                            log.info("Activating scheduled campaign {}", campaign.getId());
                            campaignService.resumeCampaign(campaign.getId(), campaign.getAdvertiserId());
                        } catch (Exception e) {
                            log.error("Failed to activate campaign {}", campaign.getId(), e);
                        }
                    }
                    if (!pageResult.hasNext()) break;
                    page++;
                }
                
                // ACTIVE and endDate < now -> COMPLETED
                page = 0;
                while (true) {
                    org.springframework.data.domain.Page<Campaign> pageResult = campaignRepository.findByStatusAndEndDateLessThan(
                            CampaignStatus.ACTIVE, now, org.springframework.data.domain.PageRequest.of(page, 100));
                    for (Campaign campaign : pageResult) {
                        try {
                            log.info("Completing expired campaign {}", campaign.getId());
                            campaignService.completeCampaign(campaign.getId(), campaign.getAdvertiserId());
                        } catch (Exception e) {
                            log.error("Failed to complete campaign {}", campaign.getId(), e);
                        }
                    }
                    if (!pageResult.hasNext()) break;
                    page++;
                }
            } finally {
                redisTemplate.delete(LOCK_KEY);
            }
        } else {
            log.debug("Could not acquire lock for campaign lifecycle sweeper");
        }
    }
}
