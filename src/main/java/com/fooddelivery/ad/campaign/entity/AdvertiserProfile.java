package com.fooddelivery.ad.campaign.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@Table(name = "advertiser_profiles")
@EntityListeners(AuditingEntityListener.class)
public class AdvertiserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    
    @Column(name = "user_id", nullable = false)
    private String userId; // Links to IdentityService
    
    @Column(name = "company_name", nullable = false)
    private String companyName;
    
    @Column(name = "wallet_balance_id")
    private UUID walletBalanceId; // Links to BillingWalletService
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
