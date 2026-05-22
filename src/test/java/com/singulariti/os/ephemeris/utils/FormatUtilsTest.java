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

public class FormatUtilsTest {

    @Test
    public void dateAndTimeStringsUseExpectedZones() {
        Observatory obs = observatory(ZonedDateTime.of(2024, 6, 1, 18, 5, 9, 0, ZoneId.of("UTC")));

        assertEquals("2024:06:01", FormatUtils.datestring(obs));
        assertEquals("18:05:09", FormatUtils.UTString(obs));
        assertEquals("23:35:09", FormatUtils.timestring(obs, true));
        assertEquals("18:05:09", FormatUtils.timestring(obs, false));
    }

    @Test
    public void hourFormattingHandlesNegativeValuesAndWraparound() {
        assertEquals("-01:30:00", FormatUtils.hmsstring(-1.5));
        assertEquals("00:00:00", FormatUtils.hmsstring(23.9999));
        assertEquals("00:00", FormatUtils.hmstring(23.999));
        assertEquals("02:15.3", FormatUtils.hmdstring(2.256));
    }

    @Test
    public void angleAndLocationFormattingAreDeterministic() {
        assertEquals("12:20:44", FormatUtils.llstring(-12.3456));
        assertEquals("+12:35", FormatUtils.anglestring(12.58, false));
        assertEquals("359:59", FormatUtils.anglestring(359.991, true));
        assertEquals("Hassan 13:00:24N 076:05:58E", FormatUtils.sitename(observatory(ZonedDateTime.of(2024, 6, 1, 0, 0, 0, 0, ZoneId.of("UTC")))));
    }

    @Test
    public void parseColHandlesMultipleInputShapes() {
        assertEquals(12.5, FormatUtils.parseCol("12:30:00"), 0.0000001);
        assertEquals(12.0, FormatUtils.parseCol("12:30"), 0.0000001);
        assertEquals(42.0, FormatUtils.parseCol("42"), 0.0000001);
    }

    private Observatory observatory(ZonedDateTime time) {
        Place place = new Place("Hassan", 13.0068, Pole.NORTH, 76.0996, Pole.EAST,
                TimeZone.getTimeZone("Asia/Kolkata"), "", "");
        return new Observatory(place, time);
    }
}
