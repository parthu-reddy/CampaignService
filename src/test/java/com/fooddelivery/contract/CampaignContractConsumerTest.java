package com.fooddelivery.contract;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("contract-test")
@SpringBootTest(classes = CampaignContractConsumerTest.TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(ids = {
    "com.fooddelivery:wallet-service:+:stubs:8099"
}, stubsMode = StubRunnerProperties.StubsMode.LOCAL)
public class CampaignContractConsumerTest {

    @Configuration
    @EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            DataSourceTransactionManagerAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class
    })
    @EnableFeignClients(basePackages = "com.fooddelivery.ad.campaign.client")
    static class TestConfig {
    }

    @Autowired
    private com.fooddelivery.ad.campaign.client.WalletServiceClient walletServiceClient;

    @Test
    public void testClientInvocations() {
        Object response = walletServiceClient.getWallet("CUSTOMER", UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        assertNotNull(response);
    }
}
