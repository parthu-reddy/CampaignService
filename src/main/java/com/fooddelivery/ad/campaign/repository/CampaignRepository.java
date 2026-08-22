package com.fooddelivery.ad.campaign.repository;

import com.fooddelivery.ad.campaign.entity.Campaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CampaignRepository extends JpaRepository<Campaign, UUID> {
    List<Campaign> findByAdvertiserId(UUID advertiserId);
    Page<Campaign> findByAdvertiserId(UUID advertiserId, Pageable pageable);
    List<Campaign> findByStatus(com.fooddelivery.ad.campaign.enums.CampaignStatus status);
    List<Campaign> findByAdvertiserIdAndStatus(UUID advertiserId, com.fooddelivery.ad.campaign.enums.CampaignStatus status);
    java.util.Optional<Campaign> findByIdAndAdvertiserId(UUID id, UUID advertiserId);
    Page<Campaign> findByStatusAndStartDateLessThanEqual(com.fooddelivery.ad.campaign.enums.CampaignStatus status, java.time.Instant date, Pageable pageable);
    Page<Campaign> findByStatusAndEndDateLessThan(com.fooddelivery.ad.campaign.enums.CampaignStatus status, java.time.Instant date, Pageable pageable);
}