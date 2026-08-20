WITH ranked AS (
  SELECT id, ROW_NUMBER() OVER (PARTITION BY campaign_id, date ORDER BY created_at) AS rn
    FROM campaign_performance)
DELETE FROM campaign_performance WHERE id IN (SELECT id FROM ranked WHERE rn > 1);

ALTER TABLE campaign_performance
  ADD CONSTRAINT uq_campaign_performance_campaign_date UNIQUE (campaign_id, date);
