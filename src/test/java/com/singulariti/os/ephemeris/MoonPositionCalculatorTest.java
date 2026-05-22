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

import com.singulariti.os.ephemeris.domain.MoonPosition;
import com.singulariti.os.ephemeris.domain.Observatory;
import com.singulariti.os.ephemeris.domain.Place;
import com.singulariti.os.ephemeris.domain.Pole;
import com.singulariti.os.ephemeris.domain.RiseSetStatus;
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

public class MoonPositionCalculatorTest {

    private final MoonPositionCalculator calculator = new MoonPositionCalculator();

    @Test
    public void moonPositionPopulatesCoreFieldsAndUsesObservatoryZoneForEvents() {
        ZonedDateTime time = ZonedDateTime.of(2017, 11, 7, 0, 0, 0, 0, ZoneId.of("UTC"));
        Observatory obs = observatory(time);

        MoonPosition position = calculator.getPosition(obs);

        assertEquals(time, position.getDate());
        assertTrue(position.getSiteName().contains("Hassan"));
        assertNonBlank(position.getRa());
        assertNonBlank(position.getDec());
        assertNonBlank(position.getAltitude());
        assertNonBlank(position.getAzimuth());
        assertNonBlank(position.getDistance());
        assertNonBlank(position.getPhaseAngle());
        assertNonBlank(position.getIlluminatedPercentage());
        assertEquals(RiseSetStatus.NORMAL, position.getRiseSetStatus());
        assertNotNull(position.getRiseTime());
        assertNotNull(position.getSetTime());
        assertEquals(ZoneId.of("Asia/Kolkata"), position.getRiseTime().getZone());
        assertEquals(ZoneId.of("Asia/Kolkata"), position.getSetTime().getZone());
        assertFalse(position.getRiseTime().equals(position.getSetTime()));
        assertTrue(Double.parseDouble(position.getDistance()) > 0.0);
        int phaseAngle = Integer.parseInt(position.getPhaseAngle());
        assertTrue(phaseAngle >= 0);
        assertTrue(phaseAngle <= 360);
        int illuminatedPercentage = Integer.parseInt(position.getIlluminatedPercentage());
        assertTrue(illuminatedPercentage >= 0);
        assertTrue(illuminatedPercentage <= 100);
    }

    @Test
    public void moonGetPositionWithExplicitTimeUpdatesObservatoryTime() {
        ZonedDateTime initialTime = ZonedDateTime.of(2017, 11, 7, 0, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime updatedTime = initialTime.plusHours(12);
        Observatory obs = observatory(initialTime);

        MoonPosition initialPosition = calculator.getPosition(obs);
        MoonPosition updatedPosition = calculator.getPosition(obs, updatedTime);

        assertEquals(updatedTime, obs.getCurrentTime());
        assertEquals(updatedTime, updatedPosition.getDate());
        assertFalse(initialPosition.getAltitude().equals(updatedPosition.getAltitude()));
    }

    @Test
    public void moonEphemerisIncludesEndpointsAndRespectsInterval() {
        ZonedDateTime start = ZonedDateTime.of(2017, 11, 7, 0, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime end = start.plusHours(3);
        Observatory obs = observatory(start);

        List<MoonPosition> ephemeris = calculator.getEphemeris(obs, start, end, 60);

        assertEquals(4, ephemeris.size());
        assertEquals(start, ephemeris.get(0).getDate());
        assertEquals(start.plusHours(1), ephemeris.get(1).getDate());
        assertEquals(start.plusHours(2), ephemeris.get(2).getDate());
        assertEquals(end, ephemeris.get(3).getDate());
        assertEquals(Duration.ofHours(1), Duration.between(ephemeris.get(0).getDate(), ephemeris.get(1).getDate()));
        assertEquals(Duration.ofHours(1), Duration.between(ephemeris.get(1).getDate(), ephemeris.get(2).getDate()));
        assertEquals(Duration.ofHours(1), Duration.between(ephemeris.get(2).getDate(), ephemeris.get(3).getDate()));
        assertEquals(end, obs.getCurrentTime());
    }

    private Observatory observatory(ZonedDateTime time) {
        Place place = new Place("Hassan", 13.0068, Pole.NORTH, 76.0996, Pole.EAST,
                TimeZone.getTimeZone("Asia/Kolkata"), "", "");
        return new Observatory(place, time);
    }

    private void assertNonBlank(String value) {
        assertNotNull(value);
        assertFalse(value.isEmpty());
    }
}
