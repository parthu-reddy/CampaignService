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
        campaign.setStatus(CampaignStatus.ACTIVE);
        Campaign saved = campaignRepository.save(campaign);
        publishOutboxEvent(saved.getId(), EventType.AD_CAMPAIGN_CREATED, saved);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public CampaignResponse updateCampaign(UUID id, CampaignRequest request, Long version) {
        Campaign campaign = campaignRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Campaign not found with ID: " + id));
        // Optimistic locking checked by JPA automatically via @Version
        if (!campaign.getVersion().equals(version)) {
            throw new org.springframework.orm.ObjectOptimisticLockingFailureException(Campaign.class, id);
        }
        if (request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("Campaign end date cannot be before start date");
        }
        campaign.setName(request.getName());
        campaign.setDailyBudget(request.getDailyBudget());
        campaign.setLifetimeBudget(request.getLifetimeBudget());
        campaign.setMaxBid(request.getMaxBid());
        campaign.setStartDate(request.getStartDate());
        campaign.setEndDate(request.getEndDate());
        Campaign saved = campaignRepository.save(campaign);
        publishOutboxEvent(saved.getId(), EventType.AD_CAMPAIGN_UPDATED, saved);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void pauseCampaign(UUID id) {
        Campaign campaign = campaignRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Campaign not found with ID: " + id));
        campaign.setStatus(CampaignStatus.PAUSED);
        Campaign saved = campaignRepository.save(campaign);
        publishOutboxEvent(id, EventType.AD_CAMPAIGN_PAUSED, saved);
        NotificationRequestEvent evt = NotificationRequestEvent.builder().channel(ChannelType.EMAIL).eventName("CAMPAIGN_PAUSED").explicitRecipient(campaign.getAdvertiserId().toString()).payload(Map.of("campaignId", id.toString(), "message", "Your campaign has been paused.")).build();
        notificationRouterService.routeNotification(evt);
    }

    @Override
    public List<CampaignResponse> getCampaignsByAdvertiser(UUID advertiserId) {
        return campaignRepository.findByAdvertiserId(advertiserId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public org.springframework.data.domain.Page<CampaignResponse> getCampaignsByAdvertiser(UUID advertiserId, org.springframework.data.domain.Pageable pageable) {
        return campaignRepository.findByAdvertiserId(advertiserId, pageable).map(this::mapToResponse);
    }

    private void publishOutboxEvent(UUID aggregateId, EventType eventType, Object payloadObj) {
        try {
            OutboxEventEntity event = new OutboxEventEntity();
            event.setAggregateType(AggregateType.ADVERTISEMENT);
            event.setAggregateId(aggregateId.toString());
            event.setEventType(eventType);
            event.setPayload(objectMapper.writeValueAsString(payloadObj));
            event.setStatus(OutboxStatus.UNPROCESSED);
            outboxEventRepository.save(event);
        } catch (final java.lang.Throwable $ex) {
            throw new RuntimeException($ex);
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
        response.setVersion(campaign.getVersion());
        return response;
    }

    @java.lang.SuppressWarnings("all")
    public CampaignServiceImpl(final CampaignRepository campaignRepository, final OutboxEventRepository outboxEventRepository, final ObjectMapper objectMapper, final NotificationRouterService notificationRouterService) {
        this.campaignRepository = campaignRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
        this.notificationRouterService = notificationRouterService;
    }
}
