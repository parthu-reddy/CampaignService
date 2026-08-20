-- Move brand_safety_blocklist from campaigns to ad_groups
ALTER TABLE ad_groups ADD COLUMN brand_safety_blocklist TEXT;

UPDATE ad_groups a 
SET brand_safety_blocklist = c.brand_safety_blocklist 
FROM campaigns c 
WHERE a.campaign_id = c.id;

ALTER TABLE campaigns DROP COLUMN brand_safety_blocklist;

-- Convert TEXT targeting columns to JSONB
ALTER TABLE ad_groups
  ALTER COLUMN geo_targeting TYPE JSONB USING geo_targeting::jsonb,
  ALTER COLUMN dayparting_config TYPE JSONB USING dayparting_config::jsonb,
  ALTER COLUMN demographic_targeting TYPE JSONB USING demographic_targeting::jsonb,
  ALTER COLUMN behavioral_targeting TYPE JSONB USING behavioral_targeting::jsonb,
  ALTER COLUMN contextual_keywords TYPE JSONB USING contextual_keywords::jsonb,
  ALTER COLUMN brand_safety_blocklist TYPE JSONB USING brand_safety_blocklist::jsonb;
