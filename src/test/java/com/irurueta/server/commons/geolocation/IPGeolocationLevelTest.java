/**
 * Copyright (C) 2016 Alberto Irurueta Carro (alberto@irurueta.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.irurueta.server.commons.geolocation;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class IPGeolocationLevelTest {
    
    public IPGeolocationLevelTest() {}
    
    @BeforeClass
    public static void setUpClass() {}
    
    @AfterClass
    public static void tearDownClass() {}
    
    @Before
    public void setUp() {}
    
    @After
    public void tearDown() {}
    
    @Test
    public void testGetValue() {
        assertEquals(IPGeolocationLevel.DISABLED.getValue(), "disabled");
        assertEquals(IPGeolocationLevel.COUNTRY.getValue(), "country");
        assertEquals(IPGeolocationLevel.CITY.getValue(), "city");
    }

    @Test
    public void testFromValue() {
        assertEquals(IPGeolocationLevel.fromValue(
                IPGeolocationLevel.DISABLED.getValue()),
                IPGeolocationLevel.DISABLED);
        assertEquals(IPGeolocationLevel.fromValue(
                IPGeolocationLevel.COUNTRY.getValue()),
                IPGeolocationLevel.COUNTRY);
        assertEquals(IPGeolocationLevel.fromValue(
                IPGeolocationLevel.CITY.getValue()),
                IPGeolocationLevel.CITY);
        
        assertEquals(IPGeolocationLevel.fromValue(null),
                IPGeolocationLevel.DISABLED);
        assertEquals(IPGeolocationLevel.fromValue("wrong"),
                IPGeolocationLevel.DISABLED);
    }
}
