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
import com.singulariti.os.ephemeris.domain.Planet;
import com.singulariti.os.ephemeris.domain.PlanetPosition;
import com.singulariti.os.ephemeris.domain.Pole;
import com.singulariti.os.ephemeris.domain.RiseSetStatus;
import com.singulariti.os.ephemeris.utils.PlanetCatalog;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.TimeZone;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PlanetPositionCalculatorTest {

    private final PlanetPositionCalculator calculator = new PlanetPositionCalculator();
    private final Planet mars = PlanetCatalog.byName("Mars");
    private final Planet mercury = PlanetCatalog.byName("Mercury");
    private final Planet uranus = PlanetCatalog.byName("Uranus");

    @Test
    public void planetPositionPopulatesCoreFieldsAndNormalRiseTransitSet() {
        ZonedDateTime time = ZonedDateTime.of(2017, 11, 7, 0, 0, 0, 0, ZoneId.of("UTC"));
        Observatory obs = hassanObservatory(time);

        PlanetPosition position = calculator.getPosition(mars, obs);

        assertEquals(time, position.getDate());
        assertEquals("Mars", position.getName());
        assertFormattedHourValue(position.getRa());
        assertFormattedSignedAngle(position.getDec());
        assertFormattedSignedAngle(position.getAltitude());
        assertFormattedSignedAngle(position.getAzimuth());
        assertTrue(Double.parseDouble(position.getDistance()) > 0.0);
        assertEquals(RiseSetStatus.NORMAL, position.getRiseSetStatus());
        assertNotNull(position.getRise());
        assertNotNull(position.getTransit());
        assertNotNull(position.getSet());
        assertEquals(ZoneId.of("Asia/Kolkata"), position.getRise().getZone());
        assertEquals(ZoneId.of("Asia/Kolkata"), position.getTransit().getZone());
        assertEquals(ZoneId.of("Asia/Kolkata"), position.getSet().getZone());
        assertTrue(position.getRise().isBefore(position.getTransit()));
        assertTrue(position.getTransit().isBefore(position.getSet()));
    }

    @Test
    public void planetGetPositionWithExplicitTimeUpdatesObservatoryAndChangesCoordinates() {
        ZonedDateTime initialTime = ZonedDateTime.of(2017, 11, 7, 0, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime updatedTime = initialTime.plusHours(6);
        Observatory obs = hassanObservatory(initialTime);

        PlanetPosition initialPosition = calculator.getPosition(mars, obs);
        PlanetPosition updatedPosition = calculator.getPosition(mars, obs, updatedTime);

        assertEquals(updatedTime, obs.getCurrentTime());
        assertEquals(updatedTime, updatedPosition.getDate());
        assertFalse(initialPosition.getAltitude().equals(updatedPosition.getAltitude()));
        assertFalse(initialPosition.getAzimuth().equals(updatedPosition.getAzimuth()));
    }

    @Test
    public void planetEphemerisIncludesEndpointsAndRespectsInterval() {
        ZonedDateTime start = ZonedDateTime.of(2017, 11, 7, 0, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime end = start.plusHours(6);
        Observatory obs = hassanObservatory(start);

        List<PlanetPosition> ephemeris = calculator.getEphemeris(mars, obs, start, end, 180);

        assertEquals(3, ephemeris.size());
        assertEquals(start, ephemeris.get(0).getDate());
        assertEquals(start.plusHours(3), ephemeris.get(1).getDate());
        assertEquals(end, ephemeris.get(2).getDate());
        assertEquals(Duration.ofHours(3), Duration.between(ephemeris.get(0).getDate(), ephemeris.get(1).getDate()));
        assertEquals(Duration.ofHours(3), Duration.between(ephemeris.get(1).getDate(), ephemeris.get(2).getDate()));
        assertEquals(end, obs.getCurrentTime());
        assertEquals(RiseSetStatus.NORMAL, ephemeris.get(0).getRiseSetStatus());
        assertFalse(ephemeris.get(0).getAltitude().equals(ephemeris.get(1).getAltitude()));
        assertFalse(ephemeris.get(1).getAzimuth().equals(ephemeris.get(2).getAzimuth()));
    }

    @Test
    public void planetPositionReportsAlwaysBelowHorizonWithoutRiseOrSet() {
        ZonedDateTime time = ZonedDateTime.of(2017, 11, 7, 0, 0, 0, 0, ZoneId.of("UTC"));
        Observatory obs = longyearbyenObservatory(time);

        PlanetPosition position = calculator.getPosition(mercury, obs);

        assertEquals(RiseSetStatus.ALWAYS_BELOW_HORIZON, position.getRiseSetStatus());
        assertNull(position.getRise());
        assertNotNull(position.getTransit());
        assertNull(position.getSet());
        assertEquals(ZoneId.of("Europe/Oslo"), position.getTransit().getZone());
    }

    @Test
    public void planetPositionReportsAlwaysAboveHorizonWithoutRiseOrSet() {
        ZonedDateTime time = ZonedDateTime.of(2020, 6, 21, 12, 0, 0, 0, ZoneId.of("UTC"));
        Observatory obs = longyearbyenObservatory(time);

        PlanetPosition position = calculator.getPosition(uranus, obs);

        assertEquals(RiseSetStatus.ALWAYS_ABOVE_HORIZON, position.getRiseSetStatus());
        assertNull(position.getRise());
        assertNotNull(position.getTransit());
        assertNull(position.getSet());
        assertEquals(ZoneId.of("Europe/Oslo"), position.getTransit().getZone());
    }

    private Observatory hassanObservatory(ZonedDateTime time) {
        Place place = new Place("Hassan", 13.0068, Pole.NORTH, 76.0996, Pole.EAST,
                TimeZone.getTimeZone("Asia/Kolkata"), "", "");
        return new Observatory(place, time);
    }

    private Observatory longyearbyenObservatory(ZonedDateTime time) {
        Place place = new Place("Longyearbyen", 78.2232, Pole.NORTH, 15.6469, Pole.EAST,
                TimeZone.getTimeZone("Europe/Oslo"), "", "");
        return new Observatory(place, time);
    }

    private void assertFormattedHourValue(String value) {
        assertNotNull(value);
        assertTrue(value.matches("\\d{2}:\\d{2}\\.\\d"));
    }

    private void assertFormattedSignedAngle(String value) {
        assertNotNull(value);
        assertTrue(value.matches("[+-]\\d{2,3}:\\d{2}"));
    }
}
