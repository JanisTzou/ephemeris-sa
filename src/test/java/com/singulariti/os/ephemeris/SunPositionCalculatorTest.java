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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SunPositionCalculatorTest {

    @Test
    public void sunEventsAreReturnedInObservatoryZone() {
        ZoneId inputZone = ZoneId.of("UTC");
        ZonedDateTime time = ZonedDateTime.of(2017, 11, 7, 0, 0, 0, 0, inputZone);
        Observatory obs = observatory(time);

        SunPosition position = new SunPositionCalculator().getPosition(obs);

        assertEquals(time, position.getDate());
        assertEquals(RiseSetStatus.NORMAL, position.getRiseSetStatus());
        assertNotNull(position.getRiseTime());
        assertNotNull(position.getSetTime());
        assertEquals(ZoneId.of("Asia/Kolkata"), position.getRiseTime().getZone());
        assertEquals(ZoneId.of("Asia/Kolkata"), position.getSetTime().getZone());
    }

    private Observatory observatory(ZonedDateTime time) {
        Place place = new Place("Hassan", 13.0068, Pole.NORTH, 76.0996, Pole.EAST,
                TimeZone.getTimeZone("Asia/Kolkata"), "", "");
        return new Observatory(place, time);
    }
}
