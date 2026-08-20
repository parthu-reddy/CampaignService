package com.fooddelivery.ad.campaign;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Proves config/platform-defaults.yml is imported from the common-library jar through the
 * service's own spring.config.import, and that the shared values actually reach the Environment.
 */
@ActiveProfiles("contract-test")
@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "spring.redis.enabled=false",
        "spring.main.allow-bean-definition-overriding=true",
        "eureka.client.enabled=false"
})
class ConfigCentralizationTest {

    @Autowired
    private Environment env;

    @Test
    void sharedDefaultsAreImportedFromTheCommonLibraryJar() {
        assertEquals("10", env.getProperty("resilience4j.circuitbreaker.configs.default.slidingWindowSize"));
        assertEquals("3s", env.getProperty("resilience4j.timelimiter.configs.default.timeoutDuration"));
        assertEquals("2000", env.getProperty("spring.cloud.openfeign.client.config.default.connectTimeout"));
        assertEquals("health,info,metrics,prometheus", env.getProperty("management.endpoints.web.exposure.include"));
        // Not asserted here, because @SpringBootTest overrides them regardless of our config:
        //   management.tracing.enabled  - DisableObservabilityContextCustomizer forces it false
        //   eureka.instance.*           - EurekaClientAutoConfiguration supplies its own defaults
        //                                 when the client is disabled
    }
}
