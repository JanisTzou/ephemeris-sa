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

import com.singulariti.os.ephemeris.domain.Star;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class StarCatalogTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void byNameReturnsStarForCanonicalName() {
        Star star = StarCatalog.byName("Polaris");

        assertEquals("a", star.getId());
        assertEquals("UMI", star.getConstellation());
        assertEquals("Polaris", star.getTraditionalName());
        assertEquals("02:31:13", star.getRa());
        assertEquals("+89:15:00", star.getDe());
        assertEquals("204", star.getMg());
        assertEquals("SD", star.getType());
        assertEquals("F8", star.getSpec());
    }

    @Test
    public void byNameIsCaseInsensitive() {
        assertEquals("Vega", StarCatalog.byName("vEgA").getTraditionalName());
    }

    @Test
    public void byIdAndConstellationReturnsStarForCanonicalId() {
        Star star = StarCatalog.byIdAndConstellation("a", "UMI");

        assertEquals("Polaris", star.getTraditionalName());
    }

    @Test
    public void byIdAndConstellationIsCaseInsensitive() {
        assertEquals("Polaris", StarCatalog.byIdAndConstellation("A", "umi").getTraditionalName());
    }

    @Test
    public void byIdAndConstellationRejectsNoMatchUsingSingletonCollectorContract() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("More than 1 item found");

        StarCatalog.byIdAndConstellation("zzz", "UMI");
    }

    @Test
    public void byNameRejectsAmbiguousMatchUsingSingletonCollectorContract() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("More than 1 item found");

        StarCatalog.byName("Albireo");
    }
}
