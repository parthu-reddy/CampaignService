-- Update campaigns table
ALTER TABLE campaigns ADD COLUMN brand_safety_blocklist TEXT;

-- Update ad_groups table
ALTER TABLE ad_groups ADD COLUMN demographic_targeting TEXT;
ALTER TABLE ad_groups ADD COLUMN behavioral_targeting TEXT;
ALTER TABLE ad_groups ADD COLUMN contextual_keywords TEXT;

-- Update ad_creatives table
ALTER TABLE ad_creatives ADD COLUMN vast_xml TEXT;
