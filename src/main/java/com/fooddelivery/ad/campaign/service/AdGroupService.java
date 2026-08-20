package com.fooddelivery.ad.campaign.service;

import com.fooddelivery.ad.campaign.dto.AdGroupRequest;
import com.fooddelivery.ad.campaign.dto.AdGroupResponse;
import com.fooddelivery.ad.campaign.entity.AdGroup;
import com.fooddelivery.ad.campaign.repository.AdGroupRepository;
import com.fooddelivery.ad.campaign.repository.CampaignRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

@Service
@Transactional
public class AdGroupService {
    private final AdGroupRepository adGroupRepository;
    private final CampaignRepository campaignRepository;

    public AdGroupService(AdGroupRepository adGroupRepository, CampaignRepository campaignRepository) {
        this.adGroupRepository = adGroupRepository;
        this.campaignRepository = campaignRepository;
    }

    public AdGroupResponse createAdGroup(UUID advertiserId, UUID campaignId, AdGroupRequest request) {
        campaignRepository.findByIdAndAdvertiserId(campaignId, advertiserId)
                .orElseThrow(() -> new IllegalArgumentException("Campaign not found for advertiser"));

        AdGroup adGroup = new AdGroup();
        adGroup.setCampaignId(campaignId);
        adGroup.setName(request.getName());
        adGroup.setGeoTargeting(request.getGeoTargeting());
        adGroup.setDaypartingConfig(request.getDaypartingConfig());
        adGroup.setDemographicTargeting(request.getDemographicTargeting());
        adGroup.setBehavioralTargeting(request.getBehavioralTargeting());
        adGroup.setContextualKeywords(request.getContextualKeywords());
        adGroup.setBrandSafetyBlocklist(request.getBrandSafetyBlocklist());
        adGroup.setActive(request.isActive());

        AdGroup saved = adGroupRepository.save(adGroup);
        return toResponse(saved);
    }

    public Page<AdGroupResponse> listAdGroups(UUID advertiserId, UUID campaignId, Pageable pageable) {
        campaignRepository.findByIdAndAdvertiserId(campaignId, advertiserId)
                .orElseThrow(() -> new IllegalArgumentException("Campaign not found for advertiser"));
        
        return adGroupRepository.findByCampaignId(campaignId, pageable)
                .map(this::toResponse);
    }

    public AdGroupResponse getAdGroup(UUID advertiserId, UUID campaignId, UUID adGroupId) {
        AdGroup adGroup = adGroupRepository.findByIdAndCampaignAdvertiserId(adGroupId, advertiserId)
                .filter(a -> a.getCampaignId().equals(campaignId))
                .orElseThrow(() -> new IllegalArgumentException("Ad group not found"));
        return toResponse(adGroup);
    }

    public AdGroupResponse updateAdGroup(UUID advertiserId, UUID campaignId, UUID adGroupId, AdGroupRequest request) {
        AdGroup adGroup = adGroupRepository.findByIdAndCampaignAdvertiserId(adGroupId, advertiserId)
                .filter(a -> a.getCampaignId().equals(campaignId))
                .orElseThrow(() -> new IllegalArgumentException("Ad group not found"));

        adGroup.setName(request.getName());
        adGroup.setGeoTargeting(request.getGeoTargeting());
        adGroup.setDaypartingConfig(request.getDaypartingConfig());
        adGroup.setDemographicTargeting(request.getDemographicTargeting());
        adGroup.setBehavioralTargeting(request.getBehavioralTargeting());
        adGroup.setContextualKeywords(request.getContextualKeywords());
        adGroup.setBrandSafetyBlocklist(request.getBrandSafetyBlocklist());
        adGroup.setActive(request.isActive());

        AdGroup saved = adGroupRepository.save(adGroup);
        return toResponse(saved);
    }

    public void deleteAdGroup(UUID advertiserId, UUID campaignId, UUID adGroupId) {
        AdGroup adGroup = adGroupRepository.findByIdAndCampaignAdvertiserId(adGroupId, advertiserId)
                .filter(a -> a.getCampaignId().equals(campaignId))
                .orElseThrow(() -> new IllegalArgumentException("Ad group not found"));
        adGroupRepository.delete(adGroup);
    }

    private AdGroupResponse toResponse(AdGroup entity) {
        return new AdGroupResponse(
            entity.getId(),
            entity.getCampaignId(),
            entity.getName(),
            entity.getGeoTargeting(),
            entity.getDaypartingConfig(),
            entity.getDemographicTargeting(),
            entity.getBehavioralTargeting(),
            entity.getContextualKeywords(),
            entity.getBrandSafetyBlocklist(),
            entity.isActive(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
