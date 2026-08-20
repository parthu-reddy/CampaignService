package com.fooddelivery.ad.campaign.service;

import com.fooddelivery.ad.campaign.dto.AdCreativeRequest;
import com.fooddelivery.ad.campaign.dto.AdCreativeResponse;
import com.fooddelivery.ad.campaign.entity.AdCreative;
import com.fooddelivery.ad.campaign.entity.AdGroup;
import com.fooddelivery.ad.campaign.repository.AdCreativeRepository;
import com.fooddelivery.ad.campaign.repository.AdGroupRepository;
import com.fooddelivery.common.constants.AggregateType;
import com.fooddelivery.common.constants.EventType;
import com.fooddelivery.common.outbox.entity.OutboxEventEntity;
import com.fooddelivery.common.enums.OutboxStatus;
import com.fooddelivery.common.outbox.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdCreativeService {
    private final AdCreativeRepository adCreativeRepository;
    private final AdGroupRepository adGroupRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final S3PresignedUrlService s3PresignedUrlService;

    public AdCreativeService(AdCreativeRepository adCreativeRepository,
                             AdGroupRepository adGroupRepository,
                             OutboxEventRepository outboxEventRepository,
                             ObjectMapper objectMapper,
                             S3PresignedUrlService s3PresignedUrlService) {
        this.adCreativeRepository = adCreativeRepository;
        this.adGroupRepository = adGroupRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
        this.s3PresignedUrlService = s3PresignedUrlService;
    }

    public String generateUploadUrl(UUID advertiserId, UUID campaignId, UUID adGroupId, String fileName, String contentType) {
        verifyTenancy(advertiserId, campaignId, adGroupId);
        return s3PresignedUrlService.generatePresignedUrl(advertiserId, fileName, contentType);
    }

    public AdCreativeResponse createCreative(UUID advertiserId, UUID campaignId, UUID adGroupId, AdCreativeRequest request) {
        verifyTenancy(advertiserId, campaignId, adGroupId);

        AdCreative creative = new AdCreative();
        creative.setAdGroupId(adGroupId);
        creative.setFormat(request.getFormat());
        creative.setAssetUrl(request.getAssetUrl() != null ? request.getAssetUrl() : "");
        creative.setVastXml(request.getVastXml());

        AdCreative saved = adCreativeRepository.save(creative);
        publishOutboxEvent(saved.getId(), EventType.AD_CREATIVE_PENDING, saved);
        return toResponse(saved);
    }

    public void auditCreative(UUID creativeId, boolean approve, String reason) {
        AdCreative creative = adCreativeRepository.findById(creativeId)
                .orElseThrow(() -> new IllegalArgumentException("Creative not found"));

        if (approve) {
            creative.approve();
            publishOutboxEvent(creative.getId(), EventType.AD_CREATIVE_APPROVED, creative);
        } else {
            creative.reject();
            publishOutboxEvent(creative.getId(), EventType.AD_CREATIVE_REJECTED, creative);
        }
        adCreativeRepository.save(creative);
    }

    public List<AdCreativeResponse> listCreatives(UUID advertiserId, UUID campaignId, UUID adGroupId) {
        verifyTenancy(advertiserId, campaignId, adGroupId);
        
        return adCreativeRepository.findByAdGroupId(adGroupId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private void verifyTenancy(UUID advertiserId, UUID campaignId, UUID adGroupId) {
        AdGroup adGroup = adGroupRepository.findByIdAndCampaignAdvertiserId(adGroupId, advertiserId)
                .filter(a -> a.getCampaignId().equals(campaignId))
                .orElseThrow(() -> new IllegalArgumentException("Ad group not found or unauthorized"));
    }

    private void publishOutboxEvent(UUID aggregateId, EventType eventType, AdCreative creative) {
        try {
            OutboxEventEntity event = OutboxEventEntity.builder()
                    .aggregateId(aggregateId.toString())
                    .aggregateType(AggregateType.ADVERTISEMENT)
                    .eventType(eventType)
                    .payload(objectMapper.writeValueAsString(creative))
                    .status(OutboxStatus.UNPROCESSED)
                    .build();
            outboxEventRepository.save(event);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize creative outbox event", e);
        }
    }

    private AdCreativeResponse toResponse(AdCreative entity) {
        return new AdCreativeResponse(
            entity.getId(),
            entity.getAdGroupId(),
            entity.getFormat(),
            entity.getAssetUrl(),
            entity.getVastXml(),
            entity.getAuditStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
