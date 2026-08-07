package com.fooddelivery.ad.campaign.repository;

import com.fooddelivery.ad.campaign.entity.CampaignPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CampaignPerformanceRepository extends JpaRepository<CampaignPerformance, UUID> {
    Optional<CampaignPerformance> findByCampaignIdAndDate(UUID campaignId, LocalDate date);
    List<CampaignPerformance> findByCampaignIdOrderByDateDesc(UUID campaignId);
    List<CampaignPerformance> findByAdvertiserIdOrderByDateDesc(UUID advertiserId);
}
