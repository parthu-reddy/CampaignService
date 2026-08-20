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
    org.springframework.data.domain.Page<CampaignPerformance> findByCampaignIdAndDateBetweenOrderByDateDesc(UUID campaignId, LocalDate from, LocalDate to, org.springframework.data.domain.Pageable pageable);
    org.springframework.data.domain.Page<CampaignPerformance> findByAdvertiserIdAndDateBetweenOrderByDateDesc(UUID advertiserId, LocalDate from, LocalDate to, org.springframework.data.domain.Pageable pageable);
    
    @org.springframework.data.jpa.repository.Query("SELECT SUM(c.spend) FROM CampaignPerformance c WHERE c.campaignId = :campaignId")
    java.math.BigDecimal sumSpendByCampaignId(@org.springframework.data.repository.query.Param("campaignId") UUID campaignId);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query(value = "INSERT INTO campaign_performance (id, campaign_id, advertiser_id, date, impressions, clicks, conversions, spend, created_at, updated_at) " +
            "VALUES (:id, :campaignId, :advertiserId, :date, :impressions, :clicks, :conversions, :spend, now(), now()) " +
            "ON CONFLICT (campaign_id, date) DO UPDATE SET " +
            "impressions = campaign_performance.impressions + EXCLUDED.impressions, " +
            "clicks = campaign_performance.clicks + EXCLUDED.clicks, " +
            "conversions = campaign_performance.conversions + EXCLUDED.conversions, " +
            "spend = campaign_performance.spend + EXCLUDED.spend, " +
            "updated_at = now()", nativeQuery = true)
    void upsertPerformance(@org.springframework.data.repository.query.Param("id") UUID id,
                           @org.springframework.data.repository.query.Param("campaignId") UUID campaignId,
                           @org.springframework.data.repository.query.Param("advertiserId") UUID advertiserId,
                           @org.springframework.data.repository.query.Param("date") LocalDate date,
                           @org.springframework.data.repository.query.Param("impressions") int impressions,
                           @org.springframework.data.repository.query.Param("clicks") int clicks,
                           @org.springframework.data.repository.query.Param("conversions") int conversions,
                           @org.springframework.data.repository.query.Param("spend") java.math.BigDecimal spend);
}
