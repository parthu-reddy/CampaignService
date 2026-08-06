package com.fooddelivery.ad.campaign.repository;

import com.fooddelivery.ad.campaign.entity.AdGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AdGroupRepository extends JpaRepository<AdGroup, UUID> {
}