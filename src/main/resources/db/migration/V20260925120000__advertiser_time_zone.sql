-- Every advertiser names the IANA time zone its campaigns run in.
--
-- Why: a daily budget resets at midnight, dayparting says "Mondays 18:00-22:00", and the performance
-- report is per day, and all three used one platform-wide zone (or UTC, or a rolling 24 hours from
-- the first impression). An advertiser anywhere else got the wrong day.
-- RandomDocuments/TimezoneCorrectness_2026-09-25, defects D6/D7.
--
-- Backfill: every advertiser that exists today is Indian (dev data), so Asia/Kolkata. No column
-- default afterwards: a new advertiser states its zone at registration.

ALTER TABLE advertiser_profiles ADD COLUMN time_zone VARCHAR(64);
UPDATE advertiser_profiles SET time_zone = 'Asia/Kolkata';
ALTER TABLE advertiser_profiles ALTER COLUMN time_zone SET NOT NULL;
