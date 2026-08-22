package com.fooddelivery.ad.campaign.service;

import com.fooddelivery.ad.campaign.dto.AdvertiserRegistrationRequest;
import com.fooddelivery.ad.campaign.dto.AdvertiserResponse;
import com.fooddelivery.ad.campaign.entity.AdvertiserProfile;
import com.fooddelivery.ad.campaign.repository.AdvertiserProfileRepository;
import com.fooddelivery.common.client.WalletServiceClient;
import com.fooddelivery.common.dto.wallet.WalletDto;
import com.fooddelivery.common.dto.wallet.CreateWalletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@lombok.extern.slf4j.Slf4j
public class AdvertiserService {
    @java.lang.SuppressWarnings("all")
    
    private final AdvertiserProfileRepository advertiserRepository;
    private final WalletServiceClient walletClient;
    private final String defaultCurrency;

    public AdvertiserService(AdvertiserProfileRepository advertiserRepository, WalletServiceClient walletClient, @org.springframework.beans.factory.annotation.Value("${platform.default-currency:INR}") String defaultCurrency) {
        this.advertiserRepository = advertiserRepository;
        this.walletClient = walletClient;
        this.defaultCurrency = defaultCurrency;
    }

    @Transactional
    public AdvertiserResponse registerAdvertiser(String userId, AdvertiserRegistrationRequest request) {
        if (advertiserRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("Advertiser profile already exists for this user");
        }

        if (request.getExternalRef() != null && advertiserRepository.findByExternalRef(request.getExternalRef()).isPresent()) {
            throw new IllegalArgumentException("Advertiser profile already exists for this external reference");
        }

        AdvertiserProfile profile = new AdvertiserProfile();
        profile.setUserId(userId);
        profile.setCompanyName(request.getCompanyName());
        profile.setExternalRef(request.getExternalRef());
        
        profile = advertiserRepository.save(profile);

        // Synchronously create wallet
        try {
            CreateWalletRequest createWalletReq = new CreateWalletRequest();
            createWalletReq.setEntityId(profile.getId());
            createWalletReq.setEntityType(CreateWalletRequest.EntityTypeEnum.ADVERTISER);
            createWalletReq.setCurrency(defaultCurrency);
            WalletDto wallet = walletClient.createWallet(createWalletReq);
            profile.setWalletBalanceId(wallet.getId());
            profile = advertiserRepository.save(profile);
        } catch (Exception e) {
            log.error("Failed to create wallet for advertiser: {}", profile.getId(), e);
            throw new RuntimeException("Failed to provision advertiser wallet", e);
        }

        return mapToResponse(profile);
    }

    @Transactional(readOnly = true)
    public AdvertiserResponse getAdvertiser(UUID id) {
        return advertiserRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new IllegalArgumentException("Advertiser not found"));
    }

    @Transactional(readOnly = true)
    public AdvertiserResponse getAdvertiserByUserId(String userId) {
        return advertiserRepository.findByUserId(userId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new IllegalArgumentException("Advertiser not found for user"));
    }

    @Transactional(readOnly = true)
    public AdvertiserResponse getAdvertiserByExternalRef(String externalRef) {
        return advertiserRepository.findByExternalRef(externalRef)
                .map(this::mapToResponse)
                .orElseThrow(() -> new IllegalArgumentException("Advertiser not found for external reference"));
    }

    private AdvertiserResponse mapToResponse(AdvertiserProfile profile) {
        AdvertiserResponse response = new AdvertiserResponse();
        response.setId(profile.getId());
        response.setUserId(profile.getUserId());
        response.setCompanyName(profile.getCompanyName());
        response.setExternalRef(profile.getExternalRef());
        response.setWalletBalanceId(profile.getWalletBalanceId());
        response.setCreatedAt(profile.getCreatedAt());
        response.setUpdatedAt(profile.getUpdatedAt());
        return response;
    }
}
