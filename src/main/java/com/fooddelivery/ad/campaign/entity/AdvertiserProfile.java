package com.fooddelivery.ad.campaign.entity;

import jakarta.persistence.*;
import java.util.UUID;
import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "advertiser_profiles")
@EntityListeners(AuditingEntityListener.class)@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.Data

public class AdvertiserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId; // Links to IdentityService
    
    @Column(name = "external_ref", unique = true)
    private String externalRef; // Links to external systems (e.g. brandId)
    
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
