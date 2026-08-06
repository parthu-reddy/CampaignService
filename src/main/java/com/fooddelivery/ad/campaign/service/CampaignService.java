package com.fooddelivery.ad.campaign.service;

import com.fooddelivery.ad.campaign.dto.CampaignRequest;
import com.fooddelivery.ad.campaign.dto.CampaignResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
import java.util.List;

public interface CampaignService {
    CampaignResponse createCampaign(CampaignRequest request);
    CampaignResponse updateCampaign(UUID id, CampaignRequest request, Long version);
    void pauseCampaign(UUID id);
    List<CampaignResponse> getCampaignsByAdvertiser(UUID advertiserId);
    Page<CampaignResponse> getCampaignsByAdvertiser(UUID advertiserId, Pageable pageable);
}