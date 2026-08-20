package com.fooddelivery.ad.campaign.service;

import com.fooddelivery.ad.campaign.dto.CampaignRequest;
import com.fooddelivery.ad.campaign.dto.CampaignResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
import java.util.List;

public interface CampaignService {
    CampaignResponse createCampaign(CampaignRequest request);
    CampaignResponse updateCampaign(UUID id, UUID advertiserId, CampaignRequest request, Long version);
    CampaignResponse activateCampaign(UUID id, UUID advertiserId);
    void pauseCampaign(UUID id, UUID advertiserId);
    void resumeCampaign(UUID id, UUID advertiserId);
    void deleteCampaign(UUID id, UUID advertiserId);
    void completeCampaign(UUID id, UUID advertiserId);
    CampaignResponse getCampaign(UUID id, UUID advertiserId);
    List<CampaignResponse> getCampaignsByAdvertiser(UUID advertiserId);
    Page<CampaignResponse> getCampaignsByAdvertiser(UUID advertiserId, Pageable pageable);
    Page<com.fooddelivery.ad.campaign.dto.CampaignPerformanceResponse> getCampaignPerformance(UUID campaignId, UUID advertiserId, java.time.LocalDate from, java.time.LocalDate to, Pageable pageable);
    Page<com.fooddelivery.ad.campaign.dto.CampaignPerformanceResponse> getAllCampaignPerformance(UUID advertiserId, java.time.LocalDate from, java.time.LocalDate to, Pageable pageable);
}