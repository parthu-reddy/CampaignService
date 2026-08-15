package com.fooddelivery.ad.campaign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.fooddelivery.ad.campaign",
    "com.fooddelivery.common", "com.fooddelivery"})
@EntityScan(basePackages = {
    "com.fooddelivery.ad.campaign.entity",
    "com.fooddelivery.common.outbox.entity"
})
@EnableJpaRepositories(basePackages = {
    "com.fooddelivery.ad.campaign.repository",
    "com.fooddelivery.common.outbox.repository"
})
@EnableJpaAuditing
@EnableScheduling
@EnableFeignClients(basePackages = "com.fooddelivery.ad.campaign.client")
public class CampaignServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CampaignServiceApplication.class, args);
    }
}
