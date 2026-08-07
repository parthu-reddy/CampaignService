CREATE TABLE campaign_performance (
    id UUID PRIMARY KEY,
    campaign_id UUID NOT NULL,
    advertiser_id UUID NOT NULL,
    date DATE NOT NULL,
    impressions BIGINT NOT NULL DEFAULT 0,
    clicks BIGINT NOT NULL DEFAULT 0,
    conversions BIGINT NOT NULL DEFAULT 0,
    spend DECIMAL(19, 4) NOT NULL DEFAULT 0.0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_campaign_performance_campaign_id ON campaign_performance(campaign_id);
CREATE INDEX idx_campaign_performance_advertiser_id ON campaign_performance(advertiser_id);
CREATE INDEX idx_campaign_performance_date ON campaign_performance(date);
