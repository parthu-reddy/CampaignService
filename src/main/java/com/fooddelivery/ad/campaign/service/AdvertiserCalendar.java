package com.fooddelivery.ad.campaign.service;

import com.fooddelivery.ad.campaign.entity.AdvertiserProfile;
import com.fooddelivery.ad.campaign.repository.AdvertiserProfileRepository;
import com.fooddelivery.common.exception.ResourceNotFoundException;
import com.fooddelivery.common.time.BusinessCalendar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

/**
 * An advertiser's calendar: the zone its daily budgets reset in, its dayparts run in and its
 * performance days are counted in. Every "which day" question about a campaign is answered here, from
 * the advertiser's own zone, where it used to come from UTC, from a platform-wide business zone, or
 * from a rolling 24 hours. RandomDocuments/TimezoneCorrectness_2026-09-25.
 */
@Component
@RequiredArgsConstructor
public class AdvertiserCalendar {

    private final AdvertiserProfileRepository advertisers;
    private final Clock clock;

    public ZoneId zoneOf(UUID advertiserId) {
        return advertisers.findById(advertiserId)
                .map(AdvertiserProfile::getTimeZone)
                .orElseThrow(() -> new ResourceNotFoundException("Advertiser not found: " + advertiserId));
    }

    /** Today on the advertiser's calendar. */
    public LocalDate today(UUID advertiserId) {
        return BusinessCalendar.today(clock, zoneOf(advertiserId));
    }

    /** A campaign starts at the first instant of its first day, in the advertiser's zone. */
    public static Instant startOf(LocalDate firstDay, ZoneId zone) {
        return BusinessCalendar.startOfDay(firstDay, zone);
    }

    /** ...and ends (exclusive) at the first instant of the day after its last, so it runs all of its last day. */
    public static Instant endOf(LocalDate lastDay, ZoneId zone) {
        return BusinessCalendar.startOfDay(lastDay.plusDays(1), zone);
    }

    /** The last day of a campaign whose exclusive end is {@code end}. */
    public static LocalDate lastDayOf(Instant end, ZoneId zone) {
        return BusinessCalendar.localDate(end, zone).minusDays(1);
    }
}
