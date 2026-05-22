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

import com.singulariti.os.ephemeris.domain.Planet;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class PlanetCatalogTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void byNameReturnsPlanetForCanonicalName() {
        Planet planet = PlanetCatalog.byName("Jupiter");

        assertEquals("Jupiter", planet.getName());
        assertEquals(4, planet.getL().size());
        assertEquals(4, planet.getA().size());
        assertEquals(4, planet.getE().size());
        assertEquals(4, planet.getI().size());
        assertEquals(4, planet.getN().size());
        assertEquals(4, planet.getP().size());
        assertEquals(5.202603191, planet.getA(0), 0.0);
    }

    @Test
    public void byNameIsCaseInsensitive() {
        assertEquals("Mars", PlanetCatalog.byName("mArS").getName());
    }

    @Test
    public void byNameRejectsNoMatchUsingSingletonCollectorContract() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("More than 1 item found");

        PlanetCatalog.byName("Pluto");
    }
}
