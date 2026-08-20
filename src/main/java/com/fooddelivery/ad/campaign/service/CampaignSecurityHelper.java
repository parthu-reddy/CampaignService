package com.fooddelivery.ad.campaign.service;

import com.fooddelivery.ad.campaign.entity.AdvertiserProfile;
import com.fooddelivery.ad.campaign.repository.AdvertiserProfileRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CampaignSecurityHelper {

    private final AdvertiserProfileRepository advertiserProfileRepository;

    public CampaignSecurityHelper(AdvertiserProfileRepository advertiserProfileRepository) {
        this.advertiserProfileRepository = advertiserProfileRepository;
    }

    public void verifyAccess(String userId, UUID advertiserId) {
        AdvertiserProfile profile = advertiserProfileRepository.findById(advertiserId)
                .orElseThrow(() -> new IllegalArgumentException("Advertiser not found"));
        if (!profile.getUserId().equals(userId)) {
            throw new org.springframework.security.access.AccessDeniedException("User does not have access to this advertiser");
        }
    }
}
