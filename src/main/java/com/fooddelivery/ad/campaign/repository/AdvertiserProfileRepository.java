package com.fooddelivery.ad.campaign.repository;

import com.fooddelivery.ad.campaign.entity.AdvertiserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AdvertiserProfileRepository extends JpaRepository<AdvertiserProfile, UUID> {
}