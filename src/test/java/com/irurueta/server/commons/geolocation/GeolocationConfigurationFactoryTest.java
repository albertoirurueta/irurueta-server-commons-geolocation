/*
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

import com.irurueta.server.commons.configuration.ConfigurationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.*;

public class GeolocationConfigurationFactoryTest {
    public static final String FOLDER =
            "./src/test/java/com/irurueta/server/commons/geolocation/tmp";
    public static final String COUNTRY_FILE =
            "./src/test/java/com/irurueta/server/commons/geolocation/tmp/GeoLite2-Country.mmdb";
    public static final String CITY_FILE =
            "./src/test/java/com/irurueta/server/commons/geolocation/tmp/GeoLite2-City.mmdb";

    @AfterClass
    public static void tearDownClass() {
        final File folder = new File(FOLDER);
        assertTrue(folder.exists());
        //noinspection ResultOfMethodCallIgnored
        folder.delete();
    }

    @Before
    public void setUp() {
        // check that destination files do not exist
        File f = new File(COUNTRY_FILE);
        assertFalse(f.exists());

        f = new File(CITY_FILE);
        assertFalse(f.exists());
    }

    @After
    public void tearDown() {
        // check that destination files no not exist
        File f = new File(COUNTRY_FILE);
        assertFalse(f.exists());

        f = new File(CITY_FILE);
        assertFalse(f.exists());
    }

    @Test
    public void testGetInstance() {
        final GeolocationConfigurationFactory factory1 =
                GeolocationConfigurationFactory.getInstance();
        assertNotNull(factory1);

        final GeolocationConfigurationFactory factory2 =
                GeolocationConfigurationFactory.getInstance();
        assertSame(factory1, factory2);
    }

    @Test
    public void testConfigure() throws ConfigurationException, IOException {
        final GeolocationConfigurationFactory factory =
                GeolocationConfigurationFactory.getInstance();

        // test configuration with properties
        final Properties props = new Properties();
        props.setProperty(GeolocationConfigurationFactory.
                        IP_GEOLOCATION_COUNTRY_DATABASE_FILE_PROPERTY,
                COUNTRY_FILE);
        props.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_CITY_DATABASE_FILE_PROPERTY, CITY_FILE);

        factory.reset();
        final GeolocationConfiguration cfg1 = factory.configure(props);
        assertNotNull(cfg1);

        // test default configuration
        final GeolocationConfiguration cfg2 = factory.configure();
        assertNotNull(cfg2);

        assertSame(cfg1, cfg2);

        // check that destination files exist

        // country files do not exist because city databases are being used
        File f = new File(COUNTRY_FILE);
        assertFalse(f.exists());

        f = new File(CITY_FILE);
        assertTrue(f.exists());

        IPGeolocator.getInstance().close();
    }

    @Test
    public void testReset() throws ConfigurationException, IOException {
        final GeolocationConfigurationFactory factory =
                GeolocationConfigurationFactory.getInstance();

        final Properties props = new Properties();
        props.setProperty(GeolocationConfigurationFactory.
                        IP_GEOLOCATION_COUNTRY_DATABASE_FILE_PROPERTY,
                COUNTRY_FILE);
        props.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_CITY_DATABASE_FILE_PROPERTY, CITY_FILE);

        final GeolocationConfiguration cfg1 = factory.configure(props);
        final GeolocationConfiguration cfg2 = factory.configure();

        assertSame(cfg1, cfg2);

        // test reset
        factory.reset();
        final GeolocationConfiguration cfg3 = factory.configure(props);
        assertNotSame(cfg3, cfg1);

        IPGeolocator.getInstance().close();
    }

    @Test
    public void testReconfigure() throws ConfigurationException, IOException {
        final GeolocationConfigurationFactory factory =
                GeolocationConfigurationFactory.getInstance();

        final Properties props = new Properties();
        props.setProperty(GeolocationConfigurationFactory.
                        IP_GEOLOCATION_COUNTRY_DATABASE_FILE_PROPERTY,
                COUNTRY_FILE);
        props.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_CITY_DATABASE_FILE_PROPERTY, CITY_FILE);

        final GeolocationConfiguration cfg1 = factory.reconfigure(props);
        final GeolocationConfiguration cfg2 = factory.reconfigure();

        assertNotSame(cfg1, cfg2);

        final GeolocationConfiguration cfg3 = factory.reconfigure(props);

        assertNotSame(cfg2, cfg3);

        // check that destination files exist

        // country files do not exist because city databases are being used
        File f = new File(COUNTRY_FILE);
        assertFalse(f.exists());

        f = new File(CITY_FILE);
        assertTrue(f.exists());

        IPGeolocator.getInstance().close();
    }

    @Test
    public void testGetConfiguration() throws ConfigurationException, IOException {
        final GeolocationConfigurationFactory factory =
                GeolocationConfigurationFactory.getInstance();

        factory.reset();

        assertNull(factory.getConfiguration());

        final Properties props = new Properties();
        props.setProperty(GeolocationConfigurationFactory.
                        IP_GEOLOCATION_COUNTRY_DATABASE_FILE_PROPERTY,
                COUNTRY_FILE);
        props.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_CITY_DATABASE_FILE_PROPERTY, CITY_FILE);

        final GeolocationConfiguration cfg1 = factory.configure(props);
        final GeolocationConfiguration cfg2 = factory.getConfiguration();

        assertNotNull(cfg2);
        assertSame(cfg1, cfg2);

        // check that destination files exist

        // country files do not exist because city databases are being used
        File f = new File(COUNTRY_FILE);
        assertFalse(f.exists());

        f = new File(CITY_FILE);
        assertTrue(f.exists());

        IPGeolocator.getInstance().close();
    }
}
