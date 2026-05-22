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

import java.util.TimeZone;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class PlaceTest {

    @Test
    public void numericConstructorAppliesLatitudeLongitudeSigns() {
        Place place = new Place("Hassan", 13.0068, Pole.SOUTH, 76.0996, Pole.EAST,
                TimeZone.getTimeZone("Asia/Kolkata"), "start", "end");

        assertEquals(-13.0068, place.getLatitude(), 0.0000001);
        assertEquals(-76.0996, place.getLongitude(), 0.0000001);
        assertEquals(-330, place.getTimeDifferenceGMTMinutes());
    }

    @Test
    public void stringConstructorParsesCoordinatesAndCopyPreservesValues() {
        Place place = new Place("Hassan", "13:00:24", Pole.NORTH, "76:05:58", Pole.EAST,
                TimeZone.getTimeZone("Asia/Kolkata"), "start", "end");

        Place copy = place.copy();

        assertEquals(13.006666666666666, place.getLatitude(), 0.0000001);
        assertEquals(-76.0994, place.getLongitude(), 0.0000001);
        assertNotSame(place, copy);
        assertEquals(place.getName(), copy.getName());
        assertEquals(place.getLatitude(), copy.getLatitude(), 0.0000001);
        assertEquals(place.getLongitude(), copy.getLongitude(), 0.0000001);
        assertEquals(place.getLatitudePole(), copy.getLatitudePole());
        assertEquals(place.getLongitudePole(), copy.getLongitudePole());
        assertEquals(place.getTimeZone(), copy.getTimeZone());
    }
}
