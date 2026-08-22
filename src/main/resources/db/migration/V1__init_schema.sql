CREATE TYPE campaign_status AS ENUM ('DRAFT', 'ACTIVE', 'PAUSED', 'COMPLETED', 'ARCHIVED', 'DELETED', 'SCHEDULED');

CREATE TYPE ad_format AS ENUM ('BANNER', 'VIDEO', 'CAROUSEL', 'NATIVE', 'VIDEO_VAST');

CREATE TYPE creative_audit_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED');

CREATE TABLE advertiser_profiles (
    id UUID PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    wallet_balance_id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    external_ref VARCHAR(255),
    CONSTRAINT uk_advertiser_profiles_user_id UNIQUE (user_id),
    CONSTRAINT uk_advertiser_profiles_external_ref UNIQUE (external_ref)
);

CREATE TABLE campaigns (
    id UUID PRIMARY KEY,
    advertiser_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    status campaign_status NOT NULL DEFAULT 'DRAFT',
    daily_budget DECIMAL(19, 4) NOT NULL,
    lifetime_budget DECIMAL(19, 4) NOT NULL,
    start_date TIMESTAMP WITH TIME ZONE NOT NULL,
    end_date TIMESTAMP WITH TIME ZONE,
    frequency_cap INTEGER NOT NULL DEFAULT 5,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    max_bid DECIMAL(19, 4) NOT NULL DEFAULT 1.0,
    CONSTRAINT uq_campaigns_id_adv UNIQUE (advertiser_id, id)
);

CREATE TABLE ad_groups (
    id UUID PRIMARY KEY,
    campaign_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    geo_targeting JSONB,
    dayparting_config JSONB,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    contextual_keywords JSONB,
    brand_safety_blocklist JSONB
);

CREATE TABLE ad_creatives (
    id UUID PRIMARY KEY,
    ad_group_id UUID NOT NULL,
    format ad_format NOT NULL,
    asset_url VARCHAR(1024) NOT NULL,
    audit_status creative_audit_status NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    vast_xml TEXT
);

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
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_campaign_performance_campaign_date UNIQUE (campaign_id, date)
);
















CREATE INDEX idx_campaign_performance_campaign_id ON campaign_performance(campaign_id);

CREATE INDEX idx_campaign_performance_advertiser_id ON campaign_performance(advertiser_id);

CREATE INDEX idx_campaigns_advertiser_id ON campaigns (advertiser_id);

CREATE INDEX idx_campaigns_status        ON campaigns (status);

CREATE INDEX idx_campaigns_status_dates  ON campaigns (status, start_date, end_date);

CREATE INDEX idx_ad_groups_campaign_id   ON ad_groups (campaign_id);

CREATE INDEX idx_ad_creatives_group_id   ON ad_creatives (ad_group_id);

CREATE INDEX idx_perf_campaign_date      ON campaign_performance (campaign_id, date DESC);

CREATE INDEX idx_perf_advertiser_date    ON campaign_performance (advertiser_id, date DESC);