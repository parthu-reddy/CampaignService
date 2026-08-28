package com.fooddelivery.ad.campaign.scheduler;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * Compares the two independent records of ad spend and reports the drift between them.
 *
 * <p>Spend is recorded twice by different paths: {@code campaign_performance.spend}, aggregated by
 * {@code KafkaAnalyticsConsumer} from {@code ad-tracking-events}, and the Redis counter
 * {@code campaign:spend:lifetime:&lt;campaignId&gt;}, incremented by UserTrackingService. They are
 * derived from the same auction price but never compared, so a divergence is invisible.
 *
 * <p>This job does not correct anything. It measures, and raises
 * {@code campaign_spend_drift_total} when a campaign's two numbers disagree by more than the
 * configured tolerance, so the discrepancy becomes a dashboard line rather than a surprise.
 */
@Slf4j
@Component
// The contract-test profile excludes Redis autoconfiguration, and a scheduler has nothing to
// do in a contract test. Mirrors CampaignLifecycleScheduler.
@org.springframework.context.annotation.Profile("!contract-test")
/**
 * <strong>@replication-safe: idempotent</strong> -- reads and emits metrics only -- it never writes a correction, so concurrent runs cannot diverge.
 *
 * <p>Classification recorded 2026-08-27 (Phase 7). Every @Scheduled class in this workspace
 * carries one of these markers; the BOOT-SCHEDULE-CLASSIFIED check fails on a new one that
 * does not. Change the marker only after re-reading what the job actually does.
 */
public class SpendReconciliationScheduler {

    /** UserTrackingService stores spend as ten-thousandths, matching DECIMAL(19,4). */
    private static final BigDecimal REDIS_SCALE = new BigDecimal("10000");

    private final JdbcTemplate jdbcTemplate;
    private final StringRedisTemplate redisTemplate;
    private final MeterRegistry meterRegistry;

    @Value("${campaign.reconciliation.tolerance:0.01}")
    private BigDecimal tolerance;

    @Value("${campaign.reconciliation.lookback-days:7}")
    private int lookbackDays;

    public SpendReconciliationScheduler(JdbcTemplate jdbcTemplate,
                                        StringRedisTemplate redisTemplate,
                                        MeterRegistry meterRegistry) {
        this.jdbcTemplate = jdbcTemplate;
        this.redisTemplate = redisTemplate;
        this.meterRegistry = meterRegistry;
    }

    @Scheduled(fixedDelayString = "${campaign.reconciliation.interval.ms:3600000}")
    public void reconcileSpend() {
        // Aggregate per campaign: the Redis counter is lifetime, so the DB side must be too.
        // Bounded by a lookback window rather than scanning the whole table.
        String sql = "SELECT campaign_id, SUM(spend) AS total_spend "
                   + "FROM campaign_performance "
                   + "WHERE date >= CURRENT_DATE - CAST(? AS INTEGER) "
                   + "GROUP BY campaign_id";

        List<Map<String, Object>> rows;
        try {
            rows = jdbcTemplate.queryForList(sql, lookbackDays);
        } catch (Exception e) {
            log.error("Spend reconciliation could not read campaign_performance", e);
            return;
        }

        int compared = 0;
        int drifted = 0;
        for (Map<String, Object> row : rows) {
            Object campaignId = row.get("campaign_id");
            Object totalSpend = row.get("total_spend");
            if (campaignId == null || totalSpend == null) {
                continue;
            }

            String raw = redisTemplate.opsForValue().get("campaign:spend:lifetime:" + campaignId);
            if (raw == null) {
                // No counter yet, or it has aged out of its 90-day TTL. Not a discrepancy.
                continue;
            }

            BigDecimal redisSpend;
            try {
                redisSpend = new BigDecimal(raw).divide(REDIS_SCALE, 4, RoundingMode.HALF_UP);
            } catch (NumberFormatException e) {
                log.warn("Campaign {} has a non-numeric Redis spend counter: {}", campaignId, raw);
                meterRegistry.counter("campaign_spend_drift_total", "reason", "unparseable").increment();
                continue;
            }

            BigDecimal dbSpend = new BigDecimal(totalSpend.toString());
            BigDecimal delta = dbSpend.subtract(redisSpend).abs();
            compared++;

            if (delta.compareTo(tolerance) > 0) {
                drifted++;
                meterRegistry.counter("campaign_spend_drift_total", "reason", "mismatch").increment();
                log.warn("Spend drift for campaign {}: campaign_performance={} redis={} delta={} (tolerance {})",
                        campaignId, dbSpend, redisSpend, delta, tolerance);
            }
        }

        log.info("Spend reconciliation complete: {} campaigns compared, {} drifted beyond {}",
                compared, drifted, tolerance);
    }
}
