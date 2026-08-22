package com.fooddelivery.ad.campaign.service;

import com.fooddelivery.ad.campaign.dto.CampaignRequest;
import com.fooddelivery.ad.campaign.dto.CampaignResponse;
import com.fooddelivery.ad.campaign.entity.Campaign;
import com.fooddelivery.ad.campaign.enums.CampaignStatus;
import com.fooddelivery.ad.campaign.repository.CampaignRepository;
import com.fooddelivery.common.constants.EventType;
import com.fooddelivery.common.outbox.entity.OutboxEventEntity;
import com.fooddelivery.common.enums.OutboxStatus;
import com.fooddelivery.common.constants.AggregateType;
import com.fooddelivery.common.outbox.repository.OutboxEventRepository;
import com.fooddelivery.common.exception.ResourceNotFoundException;
import com.fooddelivery.common.exception.IllegalStateTransitionException;
import com.fooddelivery.common.client.WalletServiceClient;
import com.fooddelivery.common.service.NotificationRouterService;
import com.fooddelivery.common.event.NotificationRequestEvent;
import com.fooddelivery.common.enums.ChannelType;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CampaignServiceImpl implements CampaignService {
    private final CampaignRepository campaignRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final NotificationRouterService notificationRouterService;
    private final com.fooddelivery.ad.campaign.repository.CampaignPerformanceRepository campaignPerformanceRepository;
    private final WalletServiceClient walletServiceClient;

    @Override
    @Transactional
    public CampaignResponse createCampaign(CampaignRequest request) {
        if (request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("Campaign end date cannot be before start date");
        }
        Campaign campaign = new Campaign();
        campaign.setAdvertiserId(request.getAdvertiserId());
        campaign.setName(request.getName());
        campaign.setDailyBudget(request.getDailyBudget());
        campaign.setLifetimeBudget(request.getLifetimeBudget());
        campaign.setMaxBid(request.getMaxBid());
        campaign.setStartDate(request.getStartDate());
        campaign.setEndDate(request.getEndDate());
        if (request.getFrequencyCap() != null) {
            campaign.setFrequencyCap(request.getFrequencyCap());
        }
        campaign.setStatus(CampaignStatus.DRAFT);
        Campaign saved = campaignRepository.save(campaign);
        publishOutboxEvent(saved.getId(), EventType.AD_CAMPAIGN_CREATED, saved);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public CampaignResponse updateCampaign(UUID id, UUID advertiserId, CampaignRequest request, Long version) {
        Campaign campaign = campaignRepository.findByIdAndAdvertiserId(id, advertiserId).orElseThrow(() -> new ResourceNotFoundException("Campaign not found with ID: " + id));
        if (request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("Campaign end date cannot be before start date");
        }
        campaign.setName(request.getName());
        campaign.setDailyBudget(request.getDailyBudget());
        campaign.setLifetimeBudget(request.getLifetimeBudget());
        campaign.setMaxBid(request.getMaxBid());
        campaign.setStartDate(request.getStartDate());
        campaign.setEndDate(request.getEndDate());
        if (request.getFrequencyCap() != null) {
            campaign.setFrequencyCap(request.getFrequencyCap());
        }
        Campaign saved = campaignRepository.save(campaign);
        publishOutboxEvent(saved.getId(), EventType.AD_CAMPAIGN_UPDATED, saved);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public CampaignResponse activateCampaign(UUID id, UUID advertiserId) {
        Campaign campaign = campaignRepository.findByIdAndAdvertiserId(id, advertiserId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with ID: " + id));
        
        if (campaign.getDailyBudget() == null || campaign.getDailyBudget().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("dailyBudget must be > 0");
        }
        if (campaign.getMaxBid() == null || campaign.getMaxBid().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("maxBid must be > 0");
        }
        if (campaign.getLifetimeBudget() != null && campaign.getDailyBudget().compareTo(campaign.getLifetimeBudget()) > 0) {
            throw new IllegalArgumentException("dailyBudget cannot exceed lifetimeBudget");
        }
        if (campaign.getMaxBid().compareTo(campaign.getDailyBudget()) > 0) {
            throw new IllegalArgumentException("maxBid cannot exceed dailyBudget");
        }
        
        com.fooddelivery.common.dto.wallet.WalletDto wallet = walletServiceClient.getWallet("ADVERTISER", campaign.getAdvertiserId());
        if (wallet == null || wallet.getBalance().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Insufficient wallet balance");
        }
        
        long approvedCreatives = adCreativeRepository.countApprovedCreativesByCampaignId(campaign.getId());
        if (approvedCreatives == 0) {
            throw new IllegalStateException("Campaign must have at least one APPROVED creative to activate");
        }
        
        transitionState(campaign, CampaignStatus.SCHEDULED);
        Campaign saved = campaignRepository.save(campaign);
        publishOutboxEvent(saved.getId(), EventType.AD_CAMPAIGN_UPDATED, saved);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void pauseCampaign(UUID id, UUID advertiserId) {
        Campaign campaign = campaignRepository.findByIdAndAdvertiserId(id, advertiserId).orElseThrow(() -> new ResourceNotFoundException("Campaign not found with ID: " + id));
        transitionState(campaign, CampaignStatus.PAUSED);
        Campaign saved = campaignRepository.save(campaign);
        publishOutboxEvent(id, EventType.AD_CAMPAIGN_PAUSED, saved);
    }

    @Override
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void resumeCampaign(UUID id, UUID advertiserId) {
        Campaign campaign = campaignRepository.findByIdAndAdvertiserId(id, advertiserId).orElseThrow(() -> new ResourceNotFoundException("Campaign not found with ID: " + id));
        if (campaign.getLifetimeBudget() != null) {
            java.math.BigDecimal lifetimeSpend = campaignPerformanceRepository.sumSpendByCampaignId(id);
            if (lifetimeSpend != null && lifetimeSpend.compareTo(campaign.getLifetimeBudget()) >= 0) {
                throw new IllegalArgumentException("Cannot resume campaign: lifetime budget exhausted.");
            }
        }
        transitionState(campaign, CampaignStatus.ACTIVE);
        Campaign saved = campaignRepository.save(campaign);
        publishOutboxEvent(id, EventType.AD_CAMPAIGN_RESUMED, saved);
    }

    @Override
    @Transactional
    public void deleteCampaign(UUID id, UUID advertiserId) {
        Campaign campaign = campaignRepository.findByIdAndAdvertiserId(id, advertiserId).orElseThrow(() -> new ResourceNotFoundException("Campaign not found with ID: " + id));
        transitionState(campaign, CampaignStatus.DELETED);
        Campaign saved = campaignRepository.save(campaign);
        publishOutboxEvent(id, EventType.AD_CAMPAIGN_DELETED, saved);
    }

    @Override
    public CampaignResponse getCampaign(UUID id, UUID advertiserId) {
        Campaign campaign = campaignRepository.findByIdAndAdvertiserId(id, advertiserId).orElseThrow(() -> new ResourceNotFoundException("Campaign not found with ID: " + id));
        return mapToResponse(campaign);
    }

    @Override
    public List<CampaignResponse> getCampaignsByAdvertiser(UUID advertiserId) {
        return campaignRepository.findByAdvertiserId(advertiserId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public org.springframework.data.domain.Page<CampaignResponse> getCampaignsByAdvertiser(UUID advertiserId, org.springframework.data.domain.Pageable pageable) {
        return campaignRepository.findByAdvertiserId(advertiserId, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void completeCampaign(UUID id, UUID advertiserId) {
        Campaign campaign = campaignRepository.findByIdAndAdvertiserId(id, advertiserId).orElseThrow(() -> new ResourceNotFoundException("Campaign not found with ID: " + id));
        transitionState(campaign, CampaignStatus.COMPLETED);
        Campaign saved = campaignRepository.save(campaign);
        publishOutboxEvent(id, EventType.AD_CAMPAIGN_COMPLETED, saved);
    }

    private void transitionState(Campaign campaign, CampaignStatus toState) {
        CampaignStatus fromState = campaign.getStatus();
        if (fromState == toState) {
            return;
        }
        boolean valid = false;
        if (fromState == null) {
            valid = true;
        } else {
            switch (fromState) {
                case DRAFT:
                    valid = toState == CampaignStatus.SCHEDULED || toState == CampaignStatus.DELETED;
                    break;
                case SCHEDULED:
                    valid = toState == CampaignStatus.ACTIVE || toState == CampaignStatus.DELETED;
                    break;
                case ACTIVE:
                    valid = toState == CampaignStatus.PAUSED || toState == CampaignStatus.COMPLETED || toState == CampaignStatus.DELETED;
                    break;
                case PAUSED:
                    valid = toState == CampaignStatus.ACTIVE || toState == CampaignStatus.DELETED;
                    break;
                case COMPLETED:
                case ARCHIVED:
                case DELETED:
                    valid = false;
                    break;
            }
        }
        if (!valid) {
            throw new IllegalStateTransitionException("Cannot transition campaign from " + fromState + " to " + toState);
        }
        campaign.setStatus(toState);
    }

    /**
     * Merges the campaign's active ad groups into the campaign-level targeting summary that
     * BiddingEngine indexes and filters on (schemaVersion 2).
     *
     * <p>Collections are unioned across ad groups: a campaign is eligible wherever any of its
     * groups is. A dimension with no values is left null rather than empty, because the
     * BiddingEngine filters treat null as "unrestricted" and an empty list would otherwise read
     * as "matches nothing".
     */
    private com.fooddelivery.common.dto.targeting.TargetingSummary buildTargetingSummary(
            java.util.List<com.fooddelivery.ad.campaign.entity.AdGroup> adGroups) {

        java.util.List<String> regions = new java.util.ArrayList<>();
        java.util.List<com.fooddelivery.common.dto.targeting.DaypartingConfig.Daypart> dayparts = new java.util.ArrayList<>();
        java.util.List<String> keywords = new java.util.ArrayList<>();
        java.util.List<String> blocklist = new java.util.ArrayList<>();
        com.fooddelivery.common.dto.targeting.DemographicTargeting demographics = null;
        com.fooddelivery.common.dto.targeting.BehavioralTargeting behavioral = null;

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
            // Demographic and behavioural targeting are not list-mergeable; the first group that
            // declares them wins, which matches how a single-ad-group campaign behaves today.
            if (demographics == null) {
                demographics = group.getDemographicTargeting();
            }
            if (behavioral == null) {
                behavioral = group.getBehavioralTargeting();
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
        summary.setDemographicTargeting(demographics);
        summary.setBehavioralTargeting(behavioral);
        return summary;
    }

    private static <T> java.util.List<T> distinct(java.util.List<T> values) {
        return values.stream().distinct().collect(java.util.stream.Collectors.toList());
    }

    private void publishOutboxEvent(UUID aggregateId, EventType eventType, Campaign campaign) {
        try {
            com.fooddelivery.common.dto.targeting.TargetingSummary targeting =
                    buildTargetingSummary(adGroupRepository.findByCampaignIdAndActiveTrue(campaign.getId()));
            targeting.setFrequencyCap(campaign.getFrequencyCap());

            String creativeFormat = null;
            String creativeAssetUrl = null;
            String creativeVastXml = null;
            
            java.util.List<com.fooddelivery.ad.campaign.entity.AdCreative> approvedCreatives = adCreativeRepository.findApprovedCreativesByCampaignId(campaign.getId());
            if (!approvedCreatives.isEmpty()) {
                com.fooddelivery.ad.campaign.entity.AdCreative creative = approvedCreatives.get(0);
                creativeFormat = creative.getFormat() != null ? creative.getFormat().name() : null;
                creativeAssetUrl = creative.getAssetUrl();
                creativeVastXml = creative.getVastXml();
            }

            com.fooddelivery.common.event.CampaignChangedEvent eventPayload = com.fooddelivery.common.event.CampaignChangedEvent.builder()
                    .campaignId(campaign.getId())
                    .advertiserId(campaign.getAdvertiserId())
                    .status(campaign.getStatus() != null ? campaign.getStatus().name() : null)
                    .maxBid(campaign.getMaxBid())
                    .budget(campaign.getDailyBudget())
                    .budgetExhausted(false)
                    .pacingMultiplier(null)
                    .schemaVersion(2)
                    .targeting(targeting)
                    .creativeFormat(creativeFormat)
                    .creativeAssetUrl(creativeAssetUrl)
                    .creativeVastXml(creativeVastXml)
                    .build();

            OutboxEventEntity event = OutboxEventEntity.builder()
                    .id(UUID.randomUUID())
                    .createdAt(java.time.LocalDateTime.now())
                    .aggregateType(AggregateType.ADVERTISEMENT)
                    .aggregateId(aggregateId.toString())
                    .eventType(eventType)
                    .idempotencyKey(aggregateId.toString() + ":" + eventType.name() + ":" + (campaign.getVersion() != null ? campaign.getVersion() : 0))
                    .payload(objectMapper.writeValueAsString(eventPayload))
                    .status(OutboxStatus.UNPROCESSED)
                    .build();
            outboxEventRepository.save(event);
        } catch (com.fasterxml.jackson.core.JsonProcessingException $ex) {
            throw new com.fooddelivery.ad.campaign.exception.EventSerializationException("Failed to serialize campaign outbox event payload", $ex);
        }
    }

    private CampaignResponse mapToResponse(Campaign campaign) {
        CampaignResponse response = new CampaignResponse();
        response.setId(campaign.getId());
        response.setAdvertiserId(campaign.getAdvertiserId());
        response.setName(campaign.getName());
        response.setStatus(campaign.getStatus());
        response.setDailyBudget(campaign.getDailyBudget());
        response.setLifetimeBudget(campaign.getLifetimeBudget());
        response.setMaxBid(campaign.getMaxBid());
        response.setStartDate(campaign.getStartDate());
        response.setEndDate(campaign.getEndDate());
        response.setFrequencyCap(campaign.getFrequencyCap());
        response.setVersion(campaign.getVersion());
        return response;
    }

    private com.fooddelivery.ad.campaign.dto.CampaignPerformanceResponse mapPerformanceToResponse(com.fooddelivery.ad.campaign.entity.CampaignPerformance cp) {
        com.fooddelivery.ad.campaign.dto.CampaignPerformanceResponse resp = new com.fooddelivery.ad.campaign.dto.CampaignPerformanceResponse();
        resp.setId(cp.getId());
        resp.setAdvertiserId(cp.getAdvertiserId());
        resp.setCampaignId(cp.getCampaignId());
        resp.setDate(cp.getDate());
        resp.setImpressions(cp.getImpressions());
        resp.setClicks(cp.getClicks());
        resp.setConversions(cp.getConversions());
        resp.setSpend(cp.getSpend());
        return resp;
    }

    @Override
    public org.springframework.data.domain.Page<com.fooddelivery.ad.campaign.dto.CampaignPerformanceResponse> getCampaignPerformance(UUID campaignId, UUID advertiserId, java.time.LocalDate from, java.time.LocalDate to, org.springframework.data.domain.Pageable pageable) {
        campaignRepository.findByIdAndAdvertiserId(campaignId, advertiserId).orElseThrow(() -> new ResourceNotFoundException("Campaign not found with ID: " + campaignId));
        return campaignPerformanceRepository.findByCampaignIdAndDateBetweenOrderByDateDesc(campaignId, from, to, pageable).map(this::mapPerformanceToResponse);
    }

    @Override
    public org.springframework.data.domain.Page<com.fooddelivery.ad.campaign.dto.CampaignPerformanceResponse> getAllCampaignPerformance(UUID advertiserId, java.time.LocalDate from, java.time.LocalDate to, org.springframework.data.domain.Pageable pageable) {
        return campaignPerformanceRepository.findByAdvertiserIdAndDateBetweenOrderByDateDesc(advertiserId, from, to, pageable).map(this::mapPerformanceToResponse);
    }

    @java.lang.SuppressWarnings("all")
    private final com.fooddelivery.ad.campaign.repository.AdCreativeRepository adCreativeRepository;
    private final com.fooddelivery.ad.campaign.repository.AdGroupRepository adGroupRepository;

    public CampaignServiceImpl(final CampaignRepository campaignRepository, final OutboxEventRepository outboxEventRepository, final ObjectMapper objectMapper, final NotificationRouterService notificationRouterService, final com.fooddelivery.ad.campaign.repository.CampaignPerformanceRepository campaignPerformanceRepository, final WalletServiceClient walletServiceClient, final com.fooddelivery.ad.campaign.repository.AdCreativeRepository adCreativeRepository, final com.fooddelivery.ad.campaign.repository.AdGroupRepository adGroupRepository) {
        this.campaignRepository = campaignRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
        this.notificationRouterService = notificationRouterService;
        this.campaignPerformanceRepository = campaignPerformanceRepository;
        this.walletServiceClient = walletServiceClient;
        this.adCreativeRepository = adCreativeRepository;
        this.adGroupRepository = adGroupRepository;
    }
}
