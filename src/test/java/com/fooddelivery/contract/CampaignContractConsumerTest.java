package com.fooddelivery.contract;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.ActiveProfiles;

import com.fooddelivery.common.client.WalletServiceClient;
import com.fooddelivery.common.dto.wallet.WalletDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * CampaignService against wallet-service's published contract.
 *
 * <p>This used to be a {@code contextLoads} shell whose comment said "No Feign clients to test".
 * That was wrong: CampaignService has no {@code @FeignClient} declarations **of its own**, but
 * {@code CampaignController} and {@code AdvertiserWalletBackfillRunner} both consume
 * {@code WalletServiceClient} from common-library — which is exactly the relationship the stub
 * runner above was configured for. So the test downloaded wallet-service's stubs, started them, and
 * asserted nothing, while {@code validate_phase3_consumers.py} reported it as a consumer with no
 * client injected.
 *
 * <p>It now calls the client against those stubs, so a change to wallet-service's response shape
 * fails here rather than in an advertiser's wallet balance.
 */
@ActiveProfiles("contract-test")
@SpringBootTest(classes = CampaignContractConsumerTest.TestConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(ids = {
    "com.fooddelivery:wallet-service:+:stubs"
}, stubsMode = StubRunnerProperties.StubsMode.LOCAL)
public class CampaignContractConsumerTest {

    /** The entity the published get-wallet contract is written against. */
    private static final UUID CONTRACT_ENTITY_ID =
            UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    @org.springframework.boot.SpringBootConfiguration
    @org.springframework.boot.autoconfigure.EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            DataSourceTransactionManagerAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class
    })
    @org.springframework.cloud.openfeign.EnableFeignClients(
            basePackages = "com.fooddelivery.common.client")
    static class TestConfig {
    }

    @Autowired
    private WalletServiceClient walletServiceClient;

    /**
     * The call {@code CampaignController} makes to show an advertiser their balance. Asserting the
     * fields, not just non-null: a contract that returned an empty body would satisfy
     * {@code assertNotNull} while telling the advertiser nothing about their money.
     */
    @Test
    public void getWalletMatchesThePublishedContract() {
        WalletDto wallet = walletServiceClient.getWallet("CUSTOMER", CONTRACT_ENTITY_ID);

        assertNotNull(wallet, "wallet-service returned no body for a wallet the contract declares");
        assertNotNull(wallet.getId(), "wallet id is what the advertiser's balance is keyed on");
        assertEquals("CUSTOMER", wallet.getEntityType().toString());
        assertEquals(CONTRACT_ENTITY_ID.toString(), wallet.getEntityId().toString());
        assertNotNull(wallet.getBalance(), "a wallet with no balance is not a usable response");
    }
}
