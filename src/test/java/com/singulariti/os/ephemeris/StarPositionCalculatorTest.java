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
import com.singulariti.os.ephemeris.domain.Star;
import com.singulariti.os.ephemeris.domain.StarPosition;
import com.singulariti.os.ephemeris.utils.StarCatalog;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.TimeZone;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class StarPositionCalculatorTest {

    private final StarPositionCalculator calculator = new StarPositionCalculator();
    private final Star polaris = StarCatalog.byName("Polaris");

    @Test
    public void starPositionPopulatesFieldsFromCatalogAndComputedCoordinates() {
        ZonedDateTime time = ZonedDateTime.of(2017, 11, 7, 0, 0, 0, 0, ZoneId.of("UTC"));
        Observatory obs = observatory(time);

        StarPosition position = calculator.getPosition(polaris, obs);

        assertEquals(time, position.getDate());
        assertTrue(position.getSiteName().contains("Hassan"));
        assertEquals(polaris.getId(), position.getStar());
        assertEquals(polaris.getTraditionalName(), position.getName());
        assertEquals(polaris.getConstellation(), position.getConstellation());
        assertEquals(polaris.getRa(), position.getRa());
        assertEquals(polaris.getDe(), position.getDec());
        assertEquals(String.valueOf(Integer.valueOf(polaris.getMg())), position.getMg());
        assertEquals(polaris.getType(), position.getType());
        assertEquals(polaris.getSpec(), position.getSpectralClass());
        assertFormattedCircleAngle(position.getAzimuth());
        assertFormattedSignedAngle(position.getAltitude());
    }

    @Test
    public void starGetPositionWithExplicitTimeUpdatesObservatoryAndChangesCoordinates() {
        ZonedDateTime initialTime = ZonedDateTime.of(2017, 11, 7, 0, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime updatedTime = initialTime.plusHours(6);
        Observatory obs = observatory(initialTime);

        StarPosition initialPosition = calculator.getPosition(polaris, obs);
        StarPosition updatedPosition = calculator.getPosition(polaris, obs, updatedTime);

        assertEquals(updatedTime, obs.getCurrentTime());
        assertEquals(updatedTime, updatedPosition.getDate());
        assertEquals(initialPosition.getRa(), updatedPosition.getRa());
        assertEquals(initialPosition.getDec(), updatedPosition.getDec());
        assertFalse(initialPosition.getAltitude().equals(updatedPosition.getAltitude()));
        assertFalse(initialPosition.getAzimuth().equals(updatedPosition.getAzimuth()));
    }

    @Test
    public void starEphemerisIncludesEndpointsAndRespectsInterval() {
        ZonedDateTime start = ZonedDateTime.of(2017, 11, 7, 0, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime end = start.plusHours(6);
        Observatory obs = observatory(start);

        List<StarPosition> ephemeris = calculator.getEphemeris(polaris, obs, start, end, 180);

        assertEquals(3, ephemeris.size());
        assertEquals(start, ephemeris.get(0).getDate());
        assertEquals(start.plusHours(3), ephemeris.get(1).getDate());
        assertEquals(end, ephemeris.get(2).getDate());
        assertEquals(Duration.ofHours(3), Duration.between(ephemeris.get(0).getDate(), ephemeris.get(1).getDate()));
        assertEquals(Duration.ofHours(3), Duration.between(ephemeris.get(1).getDate(), ephemeris.get(2).getDate()));
        assertEquals(end, obs.getCurrentTime());
        assertFalse(ephemeris.get(0).getAltitude().equals(ephemeris.get(1).getAltitude()));
        assertFalse(ephemeris.get(1).getAzimuth().equals(ephemeris.get(2).getAzimuth()));
    }

    private Observatory observatory(ZonedDateTime time) {
        Place place = new Place("Hassan", 13.0068, Pole.NORTH, 76.0996, Pole.EAST,
                TimeZone.getTimeZone("Asia/Kolkata"), "", "");
        return new Observatory(place, time);
    }

    private void assertFormattedSignedAngle(String value) {
        assertNotNull(value);
        assertTrue(value.matches("[+-]\\d{2,3}:\\d{2}"));
    }

    private void assertFormattedCircleAngle(String value) {
        assertNotNull(value);
        assertTrue(value.matches("\\d{3}:\\d{2}"));
    }
}
