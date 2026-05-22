/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.singulariti.os.ephemeris.utils;

import com.singulariti.os.ephemeris.domain.Observatory;
import com.singulariti.os.ephemeris.domain.Place;
import com.singulariti.os.ephemeris.domain.Pole;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DateTimeUtilsTest {

    @Test
    public void julianDateUsesInstant() {
        Observatory obs = observatory(ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC")));

        assertEquals(2451545.0, DateTimeUtils.jd(obs), 0.0000001);
    }

    @Test
    public void julianDateIsIndependentOfInputZoneForSameInstant() {
        ZonedDateTime utc = ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime kolkata = utc.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));

        assertEquals(DateTimeUtils.jd(observatory(utc)), DateTimeUtils.jd(observatory(kolkata)), 0.0000001);
    }

    @Test
    public void getFullYearReturnsCalendarYear() {
        Date date = Date.from(Instant.parse("1999-07-01T12:00:00Z"));

        assertEquals(1999, DateTimeUtils.getFullYear(date));
    }

    @Test
    public void isLeapYearHandlesCenturyRules() {
        assertTrue(DateTimeUtils.isLeapYear(2000));
        assertFalse(DateTimeUtils.isLeapYear(1900));
        assertTrue(DateTimeUtils.isLeapYear(2024));
        assertFalse(DateTimeUtils.isLeapYear(2023));
    }

    @Test
    public void observatoryZoneAndDayStartUseObservatoryTimeZone() {
        Observatory obs = observatory(ZonedDateTime.of(2024, 6, 1, 4, 45, 0, 0, ZoneId.of("UTC")));

        assertEquals(ZoneId.of("Asia/Kolkata"), DateTimeUtils.observatoryZone(obs));
        assertEquals(ZonedDateTime.of(2024, 6, 1, 0, 0, 0, 0, ZoneId.of("Asia/Kolkata")), DateTimeUtils.observatoryDayStart(obs));
    }

    @Test
    public void timeAtHourRoundsToNearestSecond() {
        ZonedDateTime dayStart = ZonedDateTime.of(2024, 6, 1, 0, 0, 0, 0, ZoneId.of("UTC"));

        assertEquals(ZonedDateTime.of(2024, 6, 1, 1, 14, 2, 0, ZoneId.of("UTC")), DateTimeUtils.timeAtHour(dayStart, 1.2339));
    }

    @Test
    public void jd0MatchesJ2000Epoch() {
        assertEquals(2451544.5, DateTimeUtils.jd0(2000, 1, 1), 0.0000001);
    }

    @Test
    public void jdtocdHandlesGregorianBoundaryAndSecondCarry() {
        int[] gregorianBoundary = DateTimeUtils.jdtocd(2299160.5);
        int[] carriedSeconds = DateTimeUtils.jdtocd(DateTimeUtils.jd0(2000, 1, 1) + (7199.6 / 86400.0));

        assertEquals(1582, gregorianBoundary[0]);
        assertEquals(10, gregorianBoundary[1]);
        assertEquals(15, gregorianBoundary[2]);
        assertEquals(5, gregorianBoundary[3]);

        assertEquals(2000, carriedSeconds[0]);
        assertEquals(1, carriedSeconds[1]);
        assertEquals(1, carriedSeconds[2]);
        assertEquals(2, carriedSeconds[4]);
        assertEquals(0, carriedSeconds[5]);
        assertEquals(0, carriedSeconds[6]);
    }

    @Test
    public void greenwichAndLocalSiderealAreDeterministic() {
        assertEquals(6.66451964579034, DateTimeUtils.g_sidereal(2000, 1, 1), 0.0000001);

        Place place = new Place("Greenwich", 51.4769, Pole.NORTH, 0.0, Pole.WEST,
                TimeZone.getTimeZone("UTC"), "", "");
        Observatory obs = new Observatory(place, ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC));
        assertEquals(18.69737455799034, DateTimeUtils.local_sidereal(obs), 0.0000001);
    }

    @Test
    public void checkdstReturnsNegativeOffsetDuringDaylightSavings() {
        Place place = new Place("New York", 40.7128, Pole.NORTH, 74.0060, Pole.WEST,
                TimeZone.getTimeZone("America/New_York"), "", "");
        Observatory obs = new Observatory(place, ZonedDateTime.of(2024, 7, 1, 0, 0, 0, 0, ZoneId.of("UTC")));

        assertEquals(-60, DateTimeUtils.checkdst(obs, Date.from(Instant.parse("2024-07-01T12:00:00Z"))));
        assertEquals(0, DateTimeUtils.checkdst(obs, Date.from(Instant.parse("2024-01-01T12:00:00Z"))));
    }

    private Observatory observatory(ZonedDateTime time) {
        Place place = new Place("Hassan", 13.0068, Pole.NORTH, 76.0996, Pole.EAST,
                TimeZone.getTimeZone("Asia/Kolkata"), "", "");
        return new Observatory(place, time);
    }
}
