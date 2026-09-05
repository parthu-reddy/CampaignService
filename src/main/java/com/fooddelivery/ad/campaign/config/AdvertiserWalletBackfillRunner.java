package com.fooddelivery.ad.campaign.config;

import com.fooddelivery.common.client.WalletServiceClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.List;
import com.fooddelivery.common.dto.wallet.CreateWalletRequest;
import com.fooddelivery.common.enums.WalletEntityType;
import java.util.Map;
import java.util.UUID;

@Component
@lombok.extern.slf4j.Slf4j
public class AdvertiserWalletBackfillRunner implements ApplicationRunner {
    
    private final JdbcTemplate jdbcTemplate;
    private final WalletServiceClient walletServiceClient;
    
    public AdvertiserWalletBackfillRunner(JdbcTemplate jdbcTemplate, WalletServiceClient walletServiceClient) {
        this.jdbcTemplate = jdbcTemplate;
        this.walletServiceClient = walletServiceClient;
    }
    
    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting one-time Advertiser Wallet Backfill...");
        
        try {
            String sql = "SELECT DISTINCT advertiser_id FROM campaigns";
            List<String> advertiserIds = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("advertiser_id"));
            
            int count = 0;
            for (String advertiserIdStr : advertiserIds) {
                try {
                    UUID id = UUID.fromString(advertiserIdStr);
                    // Ensure wallet exists for this advertiser
                    CreateWalletRequest req = new CreateWalletRequest()
                        .entityType(WalletEntityType.ADVERTISER)
                        .entityId(id)
                        .currency("INR");
                    walletServiceClient.getOrCreateWallet(req);
                    count++;
                } catch (Exception e) {
                    log.warn("Failed to create wallet for advertiser {} during backfill: {}", advertiserIdStr, e.getMessage());
                }
            }
            
            log.info("Wallet Backfill completed. Successfully checked/created wallets for {} advertisers.", count);
        } catch (Exception e) {
            log.error("Failed to run Wallet Backfill", e);
        }
    }
}
