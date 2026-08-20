package com.fooddelivery.ad.campaign.repository;

import com.fooddelivery.ad.campaign.entity.AdGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AdGroupRepository extends JpaRepository<AdGroup, UUID> {
    @org.springframework.data.jpa.repository.Query("SELECT a FROM AdGroup a WHERE a.id = :id AND a.campaignId IN (SELECT c.id FROM Campaign c WHERE c.advertiserId = :advertiserId)")
    java.util.Optional<AdGroup> findByIdAndCampaignAdvertiserId(@org.springframework.data.repository.query.Param("id") UUID id, @org.springframework.data.repository.query.Param("advertiserId") UUID advertiserId);
    
    org.springframework.data.domain.Page<AdGroup> findByCampaignId(UUID campaignId, org.springframework.data.domain.Pageable pageable);
    java.util.List<AdGroup> findByCampaignIdAndActiveTrue(UUID campaignId);
}