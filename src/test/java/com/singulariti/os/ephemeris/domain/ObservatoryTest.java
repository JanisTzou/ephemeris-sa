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
package com.singulariti.os.ephemeris.domain;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public class ObservatoryTest {

    @Test
    public void observatoryExposesLocationAndCurrentTimeFields() {
        Place place = place();
        ZonedDateTime time = ZonedDateTime.of(2024, 6, 1, 18, 5, 9, 0, ZoneId.of("UTC"));
        Observatory observatory = new Observatory(place, time);

        assertSame(place, observatory.getLocation());
        assertEquals("Hassan", observatory.getName());
        assertEquals(2024, observatory.getFullYear());
        assertEquals(6, observatory.getMonth());
        assertEquals(1, observatory.getDate());
        assertEquals(18, observatory.getHours());
        assertEquals(5, observatory.getMinutes());
        assertEquals(9, observatory.getSeconds());
        assertEquals(13.0068, observatory.getLatitude(), 0.0000001);
        assertEquals(-76.0996, observatory.getLongitude(), 0.0000001);
        assertEquals(-330, observatory.getTimeDifferenceGMTMinutes());
    }

    @Test
    public void copyCreatesIndependentPlaceButRetainsSameTimestamp() {
        Observatory observatory = new Observatory(place(), ZonedDateTime.of(2024, 6, 1, 18, 5, 9, 0, ZoneId.of("UTC")));

        Observatory copy = observatory.copy();

        assertNotSame(observatory, copy);
        assertNotSame(observatory.getLocation(), copy.getLocation());
        assertEquals(observatory.getName(), copy.getName());
        assertEquals(observatory.getLatitude(), copy.getLatitude(), 0.0000001);
        assertEquals(observatory.getLongitude(), copy.getLongitude(), 0.0000001);
        assertSame(observatory.getCurrentTime(), copy.getCurrentTime());
    }

    private Place place() {
        return new Place("Hassan", 13.0068, Pole.NORTH, 76.0996, Pole.EAST,
                TimeZone.getTimeZone("Asia/Kolkata"), "", "");
    }
}
