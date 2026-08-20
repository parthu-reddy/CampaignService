package com.fooddelivery.ad.campaign.e2e;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.jdbc.core.JdbcTemplate;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=com.fooddelivery.contract.CustomH2Dialect",
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.cloud.config.enabled=false",
    "spring.main.allow-bean-definition-overriding=true",
    "platform.auction.token-secret=testsecret",
    "spring.redis.enabled=false",
    "spring.profiles.active=test"
})
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class CampaignLifecycleE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private com.fooddelivery.common.outbox.service.OutboxProcessor outboxProcessor;

    @Autowired
    private org.springframework.kafka.test.EmbeddedKafkaBroker embeddedKafkaBroker;

    @Test
    void testCampaignLifecycle() throws Exception {
        UUID advertiserId = UUID.randomUUID();
        jdbcTemplate.update("INSERT INTO advertiser_profiles (id, user_id, company_name, created_at, updated_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", advertiserId, advertiserId.toString(), "Test Company");
        
        // 1. POST /api/v1/advertisers/{id}/campaigns returns 201
        String payload = """
            {
                "advertiserId": "%s",
                "name": "Test Campaign",
                "dailyBudget": 100.0,
                "lifetimeBudget": 1000.0,
                "maxBid": 2.5,
                "startDate": "2025-01-01T00:00:00Z",
                "endDate": "2025-12-31T23:59:59Z"
            }
        """.formatted(advertiserId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-User-Id", advertiserId.toString());
        headers.set("X-User-Roles", "ADVERTISER");
        
        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/api/v1/advertisers/" + advertiserId + "/campaigns",
            request,
            String.class
        );
        
        if (response.getStatusCode() != HttpStatus.CREATED) {
            System.out.println("E2E Test POST failed with: " + response.getBody());
        }
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        Integer campaignCount = jdbcTemplate.queryForObject("SELECT count(*) FROM campaigns", Integer.class);
        assertThat(campaignCount).isEqualTo(1);
        
        String campaignId = jdbcTemplate.queryForObject("SELECT id FROM campaigns LIMIT 1", String.class);
        
        // Check outbox status before
        String statusBefore = jdbcTemplate.queryForObject("SELECT status FROM outbox_events LIMIT 1", String.class);
        System.out.println("Outbox status before processor: " + statusBefore);

        // Force the OutboxProcessor to run so we don't have to wait for the @Scheduled delay
        outboxProcessor.processOutboxEvents();
        
        // Check outbox status after
        String statusAfter = jdbcTemplate.queryForObject("SELECT status FROM outbox_events LIMIT 1", String.class);
        String eventType = jdbcTemplate.queryForObject("SELECT type FROM outbox_events LIMIT 1", String.class);
        System.out.println("Outbox status after processor: " + statusAfter + ", eventType: " + eventType);

        // 4. After OutboxProcessor runs, one message on ad-events carrying an eventType header
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group-" + UUID.randomUUID().toString()); // Use unique group ID just in case
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList("ad-events"));
            
            boolean messageFound = false;
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 10000) { // Wait up to 10 seconds
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println("Consumed record: " + record.value());
                    if (record.headers().lastHeader("eventType") != null) {
                        messageFound = true;
                        break;
                    }
                }
                if (messageFound) break;
            }
            assertThat(messageFound).isTrue();
        }
        
        HttpHeaders deleteHeaders = new HttpHeaders();
        deleteHeaders.set("X-User-Id", advertiserId.toString());
        deleteHeaders.set("X-User-Roles", "ADVERTISER");
        HttpEntity<Void> deleteRequest = new HttpEntity<>(null, deleteHeaders);

        // 5. DELETE /api/v1/advertisers/{id}/campaigns/{cid} returns 204 and status = 'DELETED'
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
            "/api/v1/advertisers/" + advertiserId + "/campaigns/" + campaignId,
            HttpMethod.DELETE,
            deleteRequest,
            Void.class
        );
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        String status = jdbcTemplate.queryForObject("SELECT status FROM campaigns WHERE id = ?", String.class, UUID.fromString(campaignId));
        assertThat(status).isEqualTo("DELETED");
    }
}
