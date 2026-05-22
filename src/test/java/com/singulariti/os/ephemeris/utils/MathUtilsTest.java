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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MathUtilsTest {

    @Test
    public void angleAndTrigHelpersReturnExpectedValues() {
        assertEquals(10.0, MathUtils.rev(370.0), 0.0000001);
        assertEquals(350.0, MathUtils.rev(-10.0), 0.0000001);
        assertEquals(0.5, MathUtils.sind(30.0), 0.0000001);
        assertEquals(0.5, MathUtils.cosd(60.0), 0.0000001);
        assertEquals(1.0, MathUtils.tand(45.0), 0.0000001);
        assertEquals(30.0, MathUtils.asind(0.5), 0.0000001);
        assertEquals(60.0, MathUtils.acosd(0.5), 0.0000001);
        assertEquals(-225.0, MathUtils.atan2d(1.0, -1.0), 0.0000001);
    }

    @Test
    public void signAndParsingHelpersUseCurrentContract() {
        assertEquals(-1, MathUtils.SGN(-3));
        assertEquals(1, MathUtils.SGN(0));
        assertEquals(-2, MathUtils.intr(-2.9));
        assertEquals(3, MathUtils.intr(3.9));
        assertEquals(1.25f, MathUtils.numFloat("1.25"), 0.0000001f);
    }

    @Test
    public void coordinateConversionRoundTripsForSimpleCase() {
        Observatory obs = observatory();

        double[] altitudeAzimuth = MathUtils.radtoaa(10.0, 20.0, obs);
        double[] rightAscensionDeclination = MathUtils.aatorad(altitudeAzimuth[0], altitudeAzimuth[1], obs);

        assertEquals(10.0, rightAscensionDeclination[0], 0.0000001);
        assertEquals(20.0, rightAscensionDeclination[1], 0.0000001);
    }

    @Test
    public void aatoradWrapsRightAscensionIntoExpectedRange() {
        Observatory obs = observatory();
        double[] coordinates = MathUtils.aatorad(0.0, 90.0, obs);

        assertEquals(DateTimeUtils.local_sidereal(obs) - 18.0, coordinates[0], 0.0000001);
        assertEquals(0.0, coordinates[1], 0.0000001);
        assertTrue(coordinates[0] >= 0.0);
        assertTrue(coordinates[0] < 24.0);
    }

    private Observatory observatory() {
        Place place = new Place("Hassan", 13.0068, Pole.NORTH, 76.0996, Pole.EAST,
                TimeZone.getTimeZone("Asia/Kolkata"), "", "");
        return new Observatory(place, ZonedDateTime.of(2024, 6, 1, 0, 0, 0, 0, ZoneId.of("UTC")));
    }
}
