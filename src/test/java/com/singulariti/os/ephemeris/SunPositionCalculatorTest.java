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
package com.singulariti.os.ephemeris;

import com.singulariti.os.ephemeris.domain.Observatory;
import com.singulariti.os.ephemeris.domain.Place;
import com.singulariti.os.ephemeris.domain.Pole;
import com.singulariti.os.ephemeris.domain.RiseSetStatus;
import com.singulariti.os.ephemeris.domain.SunPosition;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.TimeZone;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SunPositionCalculatorTest {

    private final SunPositionCalculator calculator = new SunPositionCalculator();

    @Test
    public void sunEventsAreReturnedInObservatoryZone() {
        ZoneId inputZone = ZoneId.of("UTC");
        ZonedDateTime time = ZonedDateTime.of(2017, 11, 7, 0, 0, 0, 0, inputZone);
        Observatory obs = observatory(time);

        SunPosition position = calculator.getPosition(obs);

        assertEquals(time, position.getDate());
        assertTrue(position.getSiteName().contains("Hassan"));
        assertNonBlank(position.getRa());
        assertNonBlank(position.getDec());
        assertNonBlank(position.getAltitude());
        assertNonBlank(position.getAzimuth());
        assertNonBlank(position.getEarthDistance());
        assertEquals(RiseSetStatus.NORMAL, position.getRiseSetStatus());
        assertNotNull(position.getRiseTime());
        assertNotNull(position.getSetTime());
        assertEquals(ZoneId.of("Asia/Kolkata"), position.getRiseTime().getZone());
        assertEquals(ZoneId.of("Asia/Kolkata"), position.getSetTime().getZone());
        assertTrue(position.getRiseTime().isBefore(position.getSetTime()));
        assertEquals(RiseSetStatus.NORMAL, position.getCivilTwilightStatus());
        assertEquals(RiseSetStatus.NORMAL, position.getNauticalTwilightStatus());
        assertEquals(RiseSetStatus.NORMAL, position.getAstronomicalTwilightStatus());
        assertNotNull(position.getCivilDawnTime());
        assertNotNull(position.getCivilDuskTime());
        assertNotNull(position.getNauticalDawnTime());
        assertNotNull(position.getNauticalDuskTime());
        assertNotNull(position.getAstronomicalDawnTime());
        assertNotNull(position.getAstronomicalDuskTime());
        assertTrue(Double.parseDouble(position.getEarthDistance()) > 0.0);
    }

    @Test
    public void getPositionWithExplicitTimeUpdatesObservatoryTime() {
        ZonedDateTime initialTime = ZonedDateTime.of(2017, 11, 7, 0, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime updatedTime = initialTime.plusHours(12);
        Observatory obs = observatory(initialTime);

        SunPosition initialPosition = calculator.getPosition(obs);
        SunPosition updatedPosition = calculator.getPosition(obs, updatedTime);

        assertEquals(updatedTime, obs.getCurrentTime());
        assertEquals(updatedTime, updatedPosition.getDate());
        assertFalse(initialPosition.getAltitude().equals(updatedPosition.getAltitude()));
    }

    @Test
    public void getEphemerisIncludesEndpointsAndRespectsInterval() {
        ZonedDateTime start = ZonedDateTime.of(2017, 11, 7, 0, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime end = start.plusHours(6);
        Observatory obs = observatory(start);

        List<SunPosition> ephemeris = calculator.getEphemeris(obs, start, end, 180);

        assertEquals(3, ephemeris.size());
        assertEquals(start, ephemeris.get(0).getDate());
        assertEquals(start.plusHours(3), ephemeris.get(1).getDate());
        assertEquals(end, ephemeris.get(2).getDate());
        assertEquals(Duration.ofHours(3), Duration.between(ephemeris.get(0).getDate(), ephemeris.get(1).getDate()));
        assertEquals(Duration.ofHours(3), Duration.between(ephemeris.get(1).getDate(), ephemeris.get(2).getDate()));
        assertEquals(end, obs.getCurrentTime());
    }

    @Test
    public void sunPositionReportsPolarDayWithoutRiseOrSet() {
        ZonedDateTime time = ZonedDateTime.of(2020, 6, 21, 12, 0, 0, 0, ZoneId.of("UTC"));
        Observatory obs = longyearbyenObservatory(time);

        SunPosition position = calculator.getPosition(obs);

        assertEquals(RiseSetStatus.ALWAYS_ABOVE_HORIZON, position.getRiseSetStatus());
        assertNull(position.getRiseTime());
        assertNull(position.getSetTime());
    }

    @Test
    public void newYorkEquinoxMatchesReferenceTimesWithinTwoMinutes() {
        SunPosition position = calculator.getPosition(observatory(
                "New York", 40.7128, Pole.NORTH, 74.0060, Pole.WEST, "America/New_York",
                ZonedDateTime.of(2024, 3, 20, 12, 0, 0, 0, ZoneId.of("UTC"))));

        assertEquals(RiseSetStatus.NORMAL, position.getRiseSetStatus());
        assertEquals(RiseSetStatus.NORMAL, position.getCivilTwilightStatus());
        assertEquals(RiseSetStatus.NORMAL, position.getNauticalTwilightStatus());
        assertEquals(RiseSetStatus.NORMAL, position.getAstronomicalTwilightStatus());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 3, 20, 6, 56, 40, 0, ZoneId.of("America/New_York")), position.getRiseTime());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 3, 20, 19, 9, 52, 0, ZoneId.of("America/New_York")), position.getSetTime());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 3, 20, 6, 30, 46, 0, ZoneId.of("America/New_York")), position.getCivilDawnTime());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 3, 20, 19, 35, 47, 0, ZoneId.of("America/New_York")), position.getCivilDuskTime());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 3, 20, 5, 58, 46, 0, ZoneId.of("America/New_York")), position.getNauticalDawnTime());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 3, 20, 20, 7, 47, 0, ZoneId.of("America/New_York")), position.getNauticalDuskTime());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 3, 20, 5, 26, 10, 0, ZoneId.of("America/New_York")), position.getAstronomicalDawnTime());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 3, 20, 20, 40, 23, 0, ZoneId.of("America/New_York")), position.getAstronomicalDuskTime());
    }

    @Test
    public void singaporeEquinoxMatchesReferenceTimesWithinTwoMinutes() {
        SunPosition position = calculator.getPosition(observatory(
                "Singapore", 1.3521, Pole.NORTH, 103.8198, Pole.EAST, "Asia/Singapore",
                ZonedDateTime.of(2024, 3, 20, 12, 0, 0, 0, ZoneId.of("UTC"))));

        assertEquals(RiseSetStatus.NORMAL, position.getRiseSetStatus());
        assertEquals(RiseSetStatus.NORMAL, position.getCivilTwilightStatus());
        assertEquals(RiseSetStatus.NORMAL, position.getNauticalTwilightStatus());
        assertEquals(RiseSetStatus.NORMAL, position.getAstronomicalTwilightStatus());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 3, 20, 7, 7, 42, 0, ZoneId.of("Asia/Singapore")), position.getRiseTime());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 3, 20, 19, 16, 31, 0, ZoneId.of("Asia/Singapore")), position.getSetTime());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 3, 20, 6, 48, 6, 0, ZoneId.of("Asia/Singapore")), position.getCivilDawnTime());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 3, 20, 19, 36, 7, 0, ZoneId.of("Asia/Singapore")), position.getCivilDuskTime());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 3, 20, 6, 24, 6, 0, ZoneId.of("Asia/Singapore")), position.getNauticalDawnTime());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 3, 20, 20, 0, 8, 0, ZoneId.of("Asia/Singapore")), position.getNauticalDuskTime());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 3, 20, 6, 0, 5, 0, ZoneId.of("Asia/Singapore")), position.getAstronomicalDawnTime());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 3, 20, 20, 24, 8, 0, ZoneId.of("Asia/Singapore")), position.getAstronomicalDuskTime());
    }

    @Test
    public void seattleSummerSolsticeMatchesReferenceTimesWithinTwoMinutes() {
        SunPosition position = calculator.getPosition(observatory(
                "Seattle", 47.6062, Pole.NORTH, 122.3321, Pole.WEST, "America/Los_Angeles",
                ZonedDateTime.of(2024, 6, 21, 12, 0, 0, 0, ZoneId.of("UTC"))));

        assertEquals(RiseSetStatus.NORMAL, position.getRiseSetStatus());
        assertEquals(RiseSetStatus.NORMAL, position.getCivilTwilightStatus());
        assertEquals(RiseSetStatus.NORMAL, position.getNauticalTwilightStatus());
        assertEquals(RiseSetStatus.NORMAL, position.getAstronomicalTwilightStatus());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 6, 21, 5, 9, 49, 0, ZoneId.of("America/Los_Angeles")), position.getRiseTime());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 6, 21, 21, 12, 50, 0, ZoneId.of("America/Los_Angeles")), position.getSetTime());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 6, 21, 4, 31, 2, 0, ZoneId.of("America/Los_Angeles")), position.getCivilDawnTime());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 6, 21, 21, 51, 36, 0, ZoneId.of("America/Los_Angeles")), position.getCivilDuskTime());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 6, 21, 3, 34, 36, 0, ZoneId.of("America/Los_Angeles")), position.getNauticalDawnTime());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 6, 21, 22, 48, 2, 0, ZoneId.of("America/Los_Angeles")), position.getNauticalDuskTime());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 6, 21, 2, 3, 25, 0, ZoneId.of("America/Los_Angeles")), position.getAstronomicalDawnTime());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 6, 22, 0, 19, 14, 0, ZoneId.of("America/Los_Angeles")), position.getAstronomicalDuskTime());
    }

    @Test
    public void stockholmSolsticeKeepsCivilTwilightButNoNauticalOrAstronomicalNight() {
        SunPosition position = calculator.getPosition(observatory(
                "Stockholm", 59.3293, Pole.NORTH, 18.0686, Pole.EAST, "Europe/Stockholm",
                ZonedDateTime.of(2024, 6, 21, 12, 0, 0, 0, ZoneId.of("UTC"))));

        assertEquals(RiseSetStatus.NORMAL, position.getRiseSetStatus());
        assertEquals(RiseSetStatus.NORMAL, position.getCivilTwilightStatus());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 6, 21, 1, 59, 15, 0, ZoneId.of("Europe/Stockholm")), position.getCivilDawnTime());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 6, 21, 23, 40, 1, 0, ZoneId.of("Europe/Stockholm")), position.getCivilDuskTime());
        assertEquals(RiseSetStatus.ALWAYS_ABOVE_HORIZON, position.getNauticalTwilightStatus());
        assertEquals(RiseSetStatus.ALWAYS_ABOVE_HORIZON, position.getAstronomicalTwilightStatus());
        assertNull(position.getNauticalDawnTime());
        assertNull(position.getNauticalDuskTime());
        assertNull(position.getAstronomicalDawnTime());
        assertNull(position.getAstronomicalDuskTime());
    }

    @Test
    public void longyearbyenSummerHasNoNightAtAnyTwilightThreshold() {
        SunPosition position = calculator.getPosition(observatory(
                "Longyearbyen", 78.2232, Pole.NORTH, 15.6469, Pole.EAST, "Europe/Oslo",
                ZonedDateTime.of(2024, 6, 21, 12, 0, 0, 0, ZoneId.of("UTC"))));

        assertEquals(RiseSetStatus.ALWAYS_ABOVE_HORIZON, position.getRiseSetStatus());
        assertEquals(RiseSetStatus.ALWAYS_ABOVE_HORIZON, position.getCivilTwilightStatus());
        assertEquals(RiseSetStatus.ALWAYS_ABOVE_HORIZON, position.getNauticalTwilightStatus());
        assertEquals(RiseSetStatus.ALWAYS_ABOVE_HORIZON, position.getAstronomicalTwilightStatus());
        assertNull(position.getRiseTime());
        assertNull(position.getSetTime());
        assertNull(position.getCivilDawnTime());
        assertNull(position.getCivilDuskTime());
        assertNull(position.getNauticalDawnTime());
        assertNull(position.getNauticalDuskTime());
        assertNull(position.getAstronomicalDawnTime());
        assertNull(position.getAstronomicalDuskTime());
    }

    @Test
    public void longyearbyenWinterHasNoSunriseOrCivilTwilightButDeeperTwilightStillOccurs() {
        SunPosition position = calculator.getPosition(observatory(
                "Longyearbyen", 78.2232, Pole.NORTH, 15.6469, Pole.EAST, "Europe/Oslo",
                ZonedDateTime.of(2024, 12, 21, 12, 0, 0, 0, ZoneId.of("UTC"))));

        assertEquals(RiseSetStatus.ALWAYS_BELOW_HORIZON, position.getRiseSetStatus());
        assertEquals(RiseSetStatus.ALWAYS_BELOW_HORIZON, position.getCivilTwilightStatus());
        assertNull(position.getRiseTime());
        assertNull(position.getSetTime());
        assertNull(position.getCivilDawnTime());
        assertNull(position.getCivilDuskTime());

        assertEquals(RiseSetStatus.NORMAL, position.getNauticalTwilightStatus());
        assertEquals(RiseSetStatus.NORMAL, position.getAstronomicalTwilightStatus());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 12, 21, 10, 58, 24, 0, ZoneId.of("Europe/Oslo")), position.getNauticalDawnTime());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 12, 21, 12, 52, 59, 0, ZoneId.of("Europe/Oslo")), position.getNauticalDuskTime());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 12, 21, 7, 37, 19, 0, ZoneId.of("Europe/Oslo")), position.getAstronomicalDawnTime());
        assertWithinTwoMinutes(ZonedDateTime.of(2024, 12, 21, 16, 14, 5, 0, ZoneId.of("Europe/Oslo")), position.getAstronomicalDuskTime());
    }

    private Observatory observatory(ZonedDateTime time) {
        return observatory("Hassan", 13.0068, Pole.NORTH, 76.0996, Pole.EAST, "Asia/Kolkata", time);
    }

    private Observatory longyearbyenObservatory(ZonedDateTime time) {
        return observatory("Longyearbyen", 78.2232, Pole.NORTH, 15.6469, Pole.EAST, "Europe/Oslo", time);
    }

    private Observatory observatory(String name, double latitude, Pole latitudePole, double longitude, Pole longitudePole,
            String zoneId, ZonedDateTime time) {
        Place place = new Place(name, latitude, latitudePole, longitude, longitudePole,
                TimeZone.getTimeZone(zoneId), "", "");
        return new Observatory(place, time);
    }

    private void assertNonBlank(String value) {
        assertNotNull(value);
        assertFalse(value.isEmpty());
    }

    private void assertWithinTwoMinutes(ZonedDateTime expected, ZonedDateTime actual) {
        assertNotNull(actual);
        long deltaSeconds = Math.abs(Duration.between(expected, actual).getSeconds());
        assertTrue("Expected " + actual + " to be within 120 seconds of " + expected + " but was " + deltaSeconds,
                deltaSeconds <= 120);
    }
}
