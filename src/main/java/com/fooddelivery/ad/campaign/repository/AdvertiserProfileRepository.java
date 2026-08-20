package com.fooddelivery.ad.campaign.repository;

import com.fooddelivery.ad.campaign.entity.AdvertiserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

import java.util.Optional;

public interface AdvertiserProfileRepository extends JpaRepository<AdvertiserProfile, UUID> {
    Optional<AdvertiserProfile> findByUserId(String userId);
    Optional<AdvertiserProfile> findByExternalRef(String externalRef);
}