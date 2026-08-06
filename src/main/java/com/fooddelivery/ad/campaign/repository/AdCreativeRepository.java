package com.fooddelivery.ad.campaign.repository;

import com.fooddelivery.ad.campaign.entity.AdCreative;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AdCreativeRepository extends JpaRepository<AdCreative, UUID> {
}