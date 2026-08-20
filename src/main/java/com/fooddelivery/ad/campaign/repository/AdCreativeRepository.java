package com.fooddelivery.ad.campaign.repository;

import com.fooddelivery.ad.campaign.entity.AdCreative;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AdCreativeRepository extends JpaRepository<AdCreative, UUID> {
    java.util.List<AdCreative> findByAdGroupId(UUID adGroupId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(c) FROM AdCreative c JOIN AdGroup g ON c.adGroupId = g.id WHERE g.campaignId = :campaignId AND c.auditStatus = 'APPROVED'")
    long countApprovedCreativesByCampaignId(@org.springframework.data.repository.query.Param("campaignId") UUID campaignId);

    @org.springframework.data.jpa.repository.Query("SELECT c FROM AdCreative c JOIN AdGroup g ON c.adGroupId = g.id WHERE g.campaignId = :campaignId AND c.auditStatus = 'APPROVED'")
    java.util.List<AdCreative> findApprovedCreativesByCampaignId(@org.springframework.data.repository.query.Param("campaignId") UUID campaignId);
}