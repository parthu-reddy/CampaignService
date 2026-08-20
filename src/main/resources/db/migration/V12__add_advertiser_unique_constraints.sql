ALTER TABLE advertiser_profiles
ADD COLUMN external_ref VARCHAR(255);

ALTER TABLE advertiser_profiles
ADD CONSTRAINT uk_advertiser_profiles_user_id UNIQUE (user_id);

ALTER TABLE advertiser_profiles
ADD CONSTRAINT uk_advertiser_profiles_external_ref UNIQUE (external_ref);
