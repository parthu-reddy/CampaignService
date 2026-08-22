package com.fooddelivery.ad.campaign.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class ClockConfig {
    
    @org.springframework.beans.factory.annotation.Value("${platform.business-zone:UTC}")
    private String businessZone;

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of(businessZone));
    }
}
