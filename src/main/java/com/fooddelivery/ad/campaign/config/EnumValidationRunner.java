package com.fooddelivery.ad.campaign.config;

import com.fooddelivery.ad.campaign.enums.CampaignStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@lombok.extern.slf4j.Slf4j
@org.springframework.context.annotation.Profile("!test & !contract-test")
public class EnumValidationRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public EnumValidationRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        log.info("Validating Postgres enum mappings...");
        List<String> dbEnums = jdbcTemplate.queryForList(
                "SELECT unnest(enum_range(NULL::campaign_status))::text", 
                String.class
        );
        
        List<String> javaEnums = Arrays.stream(CampaignStatus.values())
                .map(Enum::name)
                .collect(Collectors.toList());

        for (String javaEnum : javaEnums) {
            if (!dbEnums.contains(javaEnum)) {
                throw new IllegalStateException("Postgres enum campaign_status is missing label: " + javaEnum);
            }
        }
        log.info("Postgres enum campaign_status validated successfully.");
        
        log.info("Validating ad_format enum...");
        List<String> adFormatDbEnums = jdbcTemplate.queryForList(
                "SELECT unnest(enum_range(NULL::ad_format))::text", 
                String.class
        );
        
        List<String> adFormatJavaEnums = Arrays.stream(com.fooddelivery.ad.campaign.enums.AdFormat.values())
                .map(Enum::name)
                .collect(Collectors.toList());

        for (String javaEnum : adFormatJavaEnums) {
            if (!adFormatDbEnums.contains(javaEnum)) {
                throw new IllegalStateException("Postgres enum ad_format is missing label: " + javaEnum);
            }
        }
        log.info("Postgres enum ad_format validated successfully.");

        log.info("Validating creative_audit_status enum...");
        List<String> creativeAuditStatusDbEnums = jdbcTemplate.queryForList(
                "SELECT unnest(enum_range(NULL::creative_audit_status))::text",
                String.class
        );

        List<String> creativeAuditStatusJavaEnums = Arrays.stream(com.fooddelivery.ad.campaign.enums.CreativeAuditStatus.values())
                .map(Enum::name)
                .collect(Collectors.toList());

        for (String javaEnum : creativeAuditStatusJavaEnums) {
            if (!creativeAuditStatusDbEnums.contains(javaEnum)) {
                throw new IllegalStateException("Postgres enum creative_audit_status is missing label: " + javaEnum);
            }
        }
        log.info("Postgres enum creative_audit_status validated successfully.");
    }
}
