CREATE INDEX idx_campaigns_advertiser_id ON campaigns (advertiser_id);
CREATE INDEX idx_campaigns_status        ON campaigns (status);
CREATE INDEX idx_campaigns_status_dates  ON campaigns (status, start_date, end_date);
CREATE INDEX idx_ad_groups_campaign_id   ON ad_groups (campaign_id);
CREATE INDEX idx_ad_creatives_group_id   ON ad_creatives (ad_group_id);
CREATE INDEX idx_perf_campaign_date      ON campaign_performance (campaign_id, date DESC);
CREATE INDEX idx_perf_advertiser_date    ON campaign_performance (advertiser_id, date DESC);
DROP INDEX IF EXISTS idx_campaign_performance_date;

ALTER TABLE campaigns ADD CONSTRAINT uq_campaigns_id_adv UNIQUE (advertiser_id, id);
