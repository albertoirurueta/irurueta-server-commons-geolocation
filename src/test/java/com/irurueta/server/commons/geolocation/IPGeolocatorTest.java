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
import com.maxmind.geoip2.DatabaseReader;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author albertoirurueta
 */
public class IPGeolocatorTest {
    public static final String FOLDER =
            "./src/test/java/com/irurueta/server/commons/geolocation/tmp";
    public static final String COUNTRY_FILE =
            "./src/test/java/com/irurueta/server/commons/geolocation/tmp/GeoLite2-Country.mmdb";
    public static final String CITY_FILE =
            "./src/test/java/com/irurueta/server/commons/geolocation/tmp/GeoLite2-City.mmdb";

    public static final double ERROR = 1e-4;

    @BeforeClass
    public static void setUpClass() throws ConfigurationException {
        // check that destination files do not exist
        File f = new File(COUNTRY_FILE);
        assertFalse(f.exists());

        f = new File(CITY_FILE);
        assertFalse(f.exists());

        final Properties props = new Properties();
        props.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_COUNTRY_DATABASE_FILE_PROPERTY, COUNTRY_FILE);
        props.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_CITY_DATABASE_FILE_PROPERTY, CITY_FILE);

        GeolocationConfigurationFactory.getInstance().configure(props);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @AfterClass
    public static void tearDownClass() throws IOException {
        IPGeolocator.getInstance().close();

        // check that destination files do not exist
        File f = new File(COUNTRY_FILE);
        if (f.exists()) {
            f.delete();
            fail("Country file was not deleted");
        }

        f = new File(CITY_FILE);
        if (f.exists()) {
            f.delete();
            fail("City file was not deleted");
        }

        final File folder = new File(FOLDER);
        assertTrue(folder.exists());
        folder.delete();
    }

    @Before
    public void setUp() throws IOException, ConfigurationException {
        IPGeolocator.reset();
        GeolocationConfigurationFactory.getInstance().reset();
    }

    @Test
    public void testGetInstanceWhenDefaultConfiguration() throws IOException {
        final IPGeolocator locator1 = IPGeolocator.getInstance();
        IPGeolocator locator2 = IPGeolocator.getInstance();

        assertSame(locator1, locator2);

        IPGeolocator.reset();

        locator2 = IPGeolocator.getInstance();

        assertNotSame(locator1, locator2);
    }

    @Test
    public void testGetInstanceWhenCityLevel() throws ConfigurationException, IOException {
        final Properties props = new Properties();
        props.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_COUNTRY_DATABASE_FILE_PROPERTY, COUNTRY_FILE);
        props.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_CITY_DATABASE_FILE_PROPERTY, CITY_FILE);
        props.setProperty(GeolocationConfigurationFactory.IP_GEOLOCATION_LEVEL_PROPERTY,
                IPGeolocationLevel.CITY.getValue());

        GeolocationConfigurationFactory.getInstance().configure(props);

        final IPGeolocator locator1 = IPGeolocator.getInstance();
        IPGeolocator locator2 = IPGeolocator.getInstance();

        assertSame(locator1, locator2);

        IPGeolocator.reset();

        locator2 = IPGeolocator.getInstance();

        assertNotSame(locator1, locator2);
    }

    @Test
    public void testGetInstanceWhenCountryLevel() throws ConfigurationException, IOException {
        final Properties props = new Properties();
        props.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_COUNTRY_DATABASE_FILE_PROPERTY, COUNTRY_FILE);
        props.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_CITY_DATABASE_FILE_PROPERTY, CITY_FILE);
        props.setProperty(GeolocationConfigurationFactory.IP_GEOLOCATION_LEVEL_PROPERTY,
                IPGeolocationLevel.COUNTRY.getValue());

        GeolocationConfigurationFactory.getInstance().configure(props);

        final IPGeolocator locator1 = IPGeolocator.getInstance();
        IPGeolocator locator2 = IPGeolocator.getInstance();

        assertSame(locator1, locator2);

        IPGeolocator.reset();

        locator2 = IPGeolocator.getInstance();

        assertNotSame(locator1, locator2);
    }

    @Test
    public void testGetInstanceWhenDisabled() throws ConfigurationException, IOException {
        final Properties props = new Properties();
        props.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_COUNTRY_DATABASE_FILE_PROPERTY, COUNTRY_FILE);
        props.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_CITY_DATABASE_FILE_PROPERTY, CITY_FILE);
        props.setProperty(GeolocationConfigurationFactory.IP_GEOLOCATION_LEVEL_PROPERTY,
                IPGeolocationLevel.DISABLED.getValue());

        GeolocationConfigurationFactory.getInstance().configure(props);

        final IPGeolocator locator1 = IPGeolocator.getInstance();
        IPGeolocator locator2 = IPGeolocator.getInstance();

        assertSame(locator1, locator2);

        IPGeolocator.reset();

        locator2 = IPGeolocator.getInstance();

        assertNotSame(locator1, locator2);
    }

    @Test
    public void testLocateMicrosoftAddress() throws UnknownHostException,
            IPLocationNotFoundException, IPGeolocationDisabledException {

        final IPGeolocator locator = IPGeolocator.getInstance();

        final String address = "64.4.4.4";
        final InetAddress inetAddress = InetAddress.getByName(address);

        // test with default level
        IPLocation location = locator.locate(address);
        assertNotNull(location);
        assertEquals(location.getCity(), "Redmond");
        assertEquals(location.getTimeZone().getID(), "America/Los_Angeles");
        assertEquals(location.getAccuracyRadius().intValue(), 937);
        assertEquals(location.getMetroCode().intValue(), 819);
        assertEquals(location.getLatitude(), 47.68009948730469, ERROR);
        assertEquals(location.getLongitude(), -122.12060546875, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertEquals(location.getPostalCode(), "98052");
        assertTrue(location.getSubdivisionCodes().contains("WA"));
        assertTrue(location.getSubdivisionNames().contains("Washington"));
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);


        location = locator.locate(inetAddress);
        assertNotNull(location);
        assertEquals(location.getCity(), "Redmond");
        assertEquals(location.getTimeZone().getID(), "America/Los_Angeles");
        assertEquals(location.getAccuracyRadius().intValue(), 937);
        assertEquals(location.getMetroCode().intValue(), 819);
        assertEquals(location.getLatitude(), 47.68009948730469, ERROR);
        assertEquals(location.getLongitude(), -122.12060546875, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertEquals(location.getPostalCode(), "98052");
        assertTrue(location.getSubdivisionCodes().contains("WA"));
        assertTrue(location.getSubdivisionNames().contains("Washington"));
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);

        // test with CITY level
        location = locator.locate(address, IPGeolocationLevel.CITY);
        assertNotNull(location);
        assertEquals(location.getCity(), "Redmond");
        assertEquals(location.getTimeZone().getID(), "America/Los_Angeles");
        assertEquals(location.getAccuracyRadius().intValue(), 937);
        assertEquals(location.getMetroCode().intValue(), 819);
        assertEquals(location.getLatitude(), 47.68009948730469, ERROR);
        assertEquals(location.getLongitude(), -122.12060546875, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertEquals(location.getPostalCode(), "98052");
        assertTrue(location.getSubdivisionCodes().contains("WA"));
        assertTrue(location.getSubdivisionNames().contains("Washington"));
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);

        location = locator.locate(inetAddress, IPGeolocationLevel.CITY);
        assertNotNull(location);
        assertEquals(location.getCity(), "Redmond");
        assertEquals(location.getTimeZone().getID(), "America/Los_Angeles");
        assertEquals(location.getAccuracyRadius().intValue(), 937);
        assertEquals(location.getMetroCode().intValue(), 819);
        assertEquals(location.getLatitude(), 47.68009948730469, ERROR);
        assertEquals(location.getLongitude(), -122.12060546875, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertEquals(location.getPostalCode(), "98052");
        assertTrue(location.getSubdivisionCodes().contains("WA"));
        assertTrue(location.getSubdivisionNames().contains("Washington"));
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);


        // test with COUNTRY level
        location = locator.locate(address, IPGeolocationLevel.COUNTRY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertNull(location.getAccuracyRadius());
        assertNull(location.getMetroCode());
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
        assertFalse(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertNull(location.getSubdivisionCodes());
        assertNull(location.getSubdivisionNames());
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);

        location = locator.locate(inetAddress, IPGeolocationLevel.COUNTRY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertNull(location.getAccuracyRadius());
        assertNull(location.getMetroCode());
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
        assertFalse(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertNull(location.getSubdivisionCodes());
        assertNull(location.getSubdivisionNames());
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);
    }

    @Test
    public void testLocateMicrosoftIpv6Address() throws UnknownHostException,
            IPLocationNotFoundException, IPGeolocationDisabledException {

        final IPGeolocator locator = IPGeolocator.getInstance();

        final String address = "2002:4136:e383::4136";
        final InetAddress inetAddress = InetAddress.getByName(address);

        // test with default level
        IPLocation location = locator.locate(address);
        assertNotNull(location);
        assertEquals(location.getCity(), "San Jose");
        assertEquals(location.getTimeZone().getID(), "America/Los_Angeles");
        assertEquals(location.getAccuracyRadius().intValue(), 937);
        assertEquals(location.getMetroCode().intValue(), 807);
        assertEquals(location.getLatitude(), 37.3394, ERROR);
        assertEquals(location.getLongitude(), -121.895, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertEquals(location.getPostalCode(), "95141");
        assertTrue(location.getSubdivisionCodes().contains("CA"));
        assertTrue(location.getSubdivisionNames().contains("California"));
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);


        location = locator.locate(inetAddress);
        assertNotNull(location);
        assertEquals(location.getCity(), "San Jose");
        assertEquals(location.getTimeZone().getID(), "America/Los_Angeles");
        assertEquals(location.getAccuracyRadius().intValue(), 937);
        assertEquals(location.getMetroCode().intValue(), 807);
        assertEquals(location.getLatitude(), 37.3394, ERROR);
        assertEquals(location.getLongitude(), -121.895, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertEquals(location.getPostalCode(), "95141");
        assertTrue(location.getSubdivisionCodes().contains("CA"));
        assertTrue(location.getSubdivisionNames().contains("California"));
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);

        // test with CITY level
        location = locator.locate(address, IPGeolocationLevel.CITY);
        assertNotNull(location);
        assertEquals(location.getCity(), "San Jose");
        assertEquals(location.getTimeZone().getID(), "America/Los_Angeles");
        assertEquals(location.getAccuracyRadius().intValue(), 937);
        assertEquals(location.getMetroCode().intValue(), 807);
        assertEquals(location.getLatitude(), 37.3394, ERROR);
        assertEquals(location.getLongitude(), -121.895, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertEquals(location.getPostalCode(), "95141");
        assertTrue(location.getSubdivisionCodes().contains("CA"));
        assertTrue(location.getSubdivisionNames().contains("California"));
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);

        location = locator.locate(inetAddress, IPGeolocationLevel.CITY);
        assertNotNull(location);
        assertEquals(location.getCity(), "San Jose");
        assertEquals(location.getTimeZone().getID(), "America/Los_Angeles");
        assertEquals(location.getAccuracyRadius().intValue(), 937);
        assertEquals(location.getMetroCode().intValue(), 807);
        assertEquals(location.getLatitude(), 37.3394, ERROR);
        assertEquals(location.getLongitude(), -121.895, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertEquals(location.getPostalCode(), "95141");
        assertTrue(location.getSubdivisionCodes().contains("CA"));
        assertTrue(location.getSubdivisionNames().contains("California"));
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);


        // test with COUNTRY level
        location = locator.locate(address, IPGeolocationLevel.COUNTRY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertNull(location.getAccuracyRadius());
        assertNull(location.getMetroCode());
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
        assertFalse(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertNull(location.getSubdivisionCodes());
        assertNull(location.getSubdivisionNames());
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);

        location = locator.locate(inetAddress, IPGeolocationLevel.COUNTRY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertNull(location.getAccuracyRadius());
        assertNull(location.getMetroCode());
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
        assertFalse(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertNull(location.getSubdivisionCodes());
        assertNull(location.getSubdivisionNames());
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);
    }

    @Test
    public void testLocateNorwayAddress() throws UnknownHostException,
            IPLocationNotFoundException, IPGeolocationDisabledException {

        final IPGeolocator locator = IPGeolocator.getInstance();

        final String address = "213.52.50.8";
        final InetAddress inetAddress = InetAddress.getByName(address);

        // test with default level
        IPLocation location = locator.locate(address);
        assertNotNull(location);
        assertNull(location.getCity());
        assertEquals(location.getTimeZone().getID(), "Europe/Oslo");
        assertEquals(location.getAccuracyRadius().intValue(), 179);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 59.95, ERROR);
        assertEquals(location.getLongitude(), 10.75, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "NO");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Norway");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Norvège");
        assertEquals(location.getRegisteredCountryCode(), "NO");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Norway");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Norvège");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);


        location = locator.locate(inetAddress);
        assertNotNull(location);
        assertNull(location.getCity());
        assertEquals(location.getTimeZone().getID(), "Europe/Oslo");
        assertEquals(location.getAccuracyRadius().intValue(), 179);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 59.95, ERROR);
        assertEquals(location.getLongitude(), 10.75, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "NO");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Norway");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Norvège");
        assertEquals(location.getRegisteredCountryCode(), "NO");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Norway");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Norvège");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);

        // test with CITY level
        location = locator.locate(address, IPGeolocationLevel.CITY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertEquals(location.getTimeZone().getID(), "Europe/Oslo");
        assertEquals(location.getAccuracyRadius().intValue(), 179);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 59.95, ERROR);
        assertEquals(location.getLongitude(), 10.75, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "NO");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Norway");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Norvège");
        assertEquals(location.getRegisteredCountryCode(), "NO");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Norway");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Norvège");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);

        location = locator.locate(inetAddress, IPGeolocationLevel.CITY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertEquals(location.getTimeZone().getID(), "Europe/Oslo");
        assertEquals(location.getAccuracyRadius().intValue(), 179);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 59.95, ERROR);
        assertEquals(location.getLongitude(), 10.75, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "NO");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Norway");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Norvège");
        assertEquals(location.getRegisteredCountryCode(), "NO");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Norway");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Norvège");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);


        // test with COUNTRY level
        location = locator.locate(address, IPGeolocationLevel.COUNTRY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertNull(location.getAccuracyRadius());
        assertNull(location.getMetroCode());
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
        assertFalse(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertNull(location.getSubdivisionCodes());
        assertNull(location.getSubdivisionNames());
        assertEquals(location.getCountryCode(), "NO");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Norway");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Norvège");
        assertEquals(location.getRegisteredCountryCode(), "NO");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Norway");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Norvège");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);

        location = locator.locate(inetAddress, IPGeolocationLevel.COUNTRY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertNull(location.getAccuracyRadius());
        assertNull(location.getMetroCode());
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
        assertFalse(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertNull(location.getSubdivisionCodes());
        assertNull(location.getSubdivisionNames());
        assertEquals(location.getCountryCode(), "NO");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Norway");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Norvège");
        assertEquals(location.getRegisteredCountryCode(), "NO");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Norway");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Norvège");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);
    }

    @Test
    public void testLocateNorwayIpv6Address() throws UnknownHostException,
            IPLocationNotFoundException, IPGeolocationDisabledException {

        final IPGeolocator locator = IPGeolocator.getInstance();

        final String address = "::213.52.50.8";
        final InetAddress inetAddress = InetAddress.getByName(address);

        // test with default level
        IPLocation location = locator.locate(address);
        assertNotNull(location);
        assertNull(location.getCity());
        assertEquals(location.getTimeZone().getID(), "Europe/Oslo");
        assertEquals(location.getAccuracyRadius().intValue(), 179);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 59.95, ERROR);
        assertEquals(location.getLongitude(), 10.75, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "NO");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Norway");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Norvège");
        assertEquals(location.getRegisteredCountryCode(), "NO");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Norway");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Norvège");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);


        location = locator.locate(inetAddress);
        assertNotNull(location);
        assertNull(location.getCity());
        assertEquals(location.getTimeZone().getID(), "Europe/Oslo");
        assertEquals(location.getAccuracyRadius().intValue(), 179);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 59.95, ERROR);
        assertEquals(location.getLongitude(), 10.75, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "NO");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Norway");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Norvège");
        assertEquals(location.getRegisteredCountryCode(), "NO");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Norway");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Norvège");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);

        // test with CITY level
        location = locator.locate(address, IPGeolocationLevel.CITY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertEquals(location.getTimeZone().getID(), "Europe/Oslo");
        assertEquals(location.getAccuracyRadius().intValue(), 179);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 59.95, ERROR);
        assertEquals(location.getLongitude(), 10.75, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "NO");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Norway");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Norvège");
        assertEquals(location.getRegisteredCountryCode(), "NO");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Norway");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Norvège");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);

        location = locator.locate(inetAddress, IPGeolocationLevel.CITY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertEquals(location.getTimeZone().getID(), "Europe/Oslo");
        assertEquals(location.getAccuracyRadius().intValue(), 179);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 59.95, ERROR);
        assertEquals(location.getLongitude(), 10.75, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "NO");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Norway");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Norvège");
        assertEquals(location.getRegisteredCountryCode(), "NO");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Norway");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Norvège");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);


        // test with COUNTRY level
        location = locator.locate(address, IPGeolocationLevel.COUNTRY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertNull(location.getAccuracyRadius());
        assertNull(location.getMetroCode());
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
        assertFalse(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertNull(location.getSubdivisionCodes());
        assertNull(location.getSubdivisionNames());
        assertEquals(location.getCountryCode(), "NO");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Norway");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Norvège");
        assertEquals(location.getRegisteredCountryCode(), "NO");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Norway");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Norvège");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);

        location = locator.locate(inetAddress, IPGeolocationLevel.COUNTRY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertNull(location.getAccuracyRadius());
        assertNull(location.getMetroCode());
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
        assertFalse(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertNull(location.getSubdivisionCodes());
        assertNull(location.getSubdivisionNames());
        assertEquals(location.getCountryCode(), "NO");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Norway");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Norvège");
        assertEquals(location.getRegisteredCountryCode(), "NO");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Norway");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Norvège");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);
    }

    @Test
    public void testLocateSpainAddress() throws UnknownHostException,
            IPLocationNotFoundException, IPGeolocationDisabledException {

        final IPGeolocator locator = IPGeolocator.getInstance();

        final String address = "37.15.50.115";
        final InetAddress inetAddress = InetAddress.getByName(address);

        // test with default level
        IPLocation location = locator.locate(address);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertEquals(location.getAccuracyRadius().intValue(), 225);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 40.4172, ERROR);
        assertEquals(location.getLongitude(), -3.684, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "ES");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Espagne");
        assertEquals(location.getRegisteredCountryCode(), "ES");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Espagne");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);


        location = locator.locate(inetAddress);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertEquals(location.getAccuracyRadius().intValue(), 225);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 40.4172, ERROR);
        assertEquals(location.getLongitude(), -3.684, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "ES");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Espagne");
        assertEquals(location.getRegisteredCountryCode(), "ES");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Espagne");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);

        // test with CITY level
        location = locator.locate(address, IPGeolocationLevel.CITY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertEquals(location.getAccuracyRadius().intValue(), 225);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 40.4172, ERROR);
        assertEquals(location.getLongitude(), -3.684, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "ES");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Espagne");
        assertEquals(location.getRegisteredCountryCode(), "ES");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Espagne");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);

        location = locator.locate(inetAddress, IPGeolocationLevel.CITY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertEquals(location.getAccuracyRadius().intValue(), 225);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 40.4172, ERROR);
        assertEquals(location.getLongitude(), -3.684, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "ES");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Espagne");
        assertEquals(location.getRegisteredCountryCode(), "ES");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Espagne");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);


        // test with COUNTRY level
        location = locator.locate(address, IPGeolocationLevel.COUNTRY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertNull(location.getAccuracyRadius());
        assertNull(location.getMetroCode());
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
        assertFalse(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertNull(location.getSubdivisionCodes());
        assertNull(location.getSubdivisionNames());
        assertEquals(location.getCountryCode(), "ES");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Espagne");
        assertEquals(location.getRegisteredCountryCode(), "ES");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Espagne");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);

        location = locator.locate(inetAddress, IPGeolocationLevel.COUNTRY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertNull(location.getAccuracyRadius());
        assertNull(location.getMetroCode());
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
        assertFalse(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertNull(location.getSubdivisionCodes());
        assertNull(location.getSubdivisionNames());
        assertEquals(location.getCountryCode(), "ES");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Espagne");
        assertEquals(location.getRegisteredCountryCode(), "ES");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Espagne");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);
    }

    @Test
    public void testLocateSpainIpv6Address() throws UnknownHostException,
            IPLocationNotFoundException, IPGeolocationDisabledException {

        final IPGeolocator locator = IPGeolocator.getInstance();

        final String address = "::37.15.50.115";
        final InetAddress inetAddress = InetAddress.getByName(address);

        // test with default level
        IPLocation location = locator.locate(address);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertEquals(location.getAccuracyRadius().intValue(), 225);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 40.4172, ERROR);
        assertEquals(location.getLongitude(), -3.684, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "ES");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Espagne");
        assertEquals(location.getRegisteredCountryCode(), "ES");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Espagne");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);


        location = locator.locate(inetAddress);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertEquals(location.getAccuracyRadius().intValue(), 225);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 40.4172, ERROR);
        assertEquals(location.getLongitude(), -3.684, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "ES");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Espagne");
        assertEquals(location.getRegisteredCountryCode(), "ES");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Espagne");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);

        // test with CITY level
        location = locator.locate(address, IPGeolocationLevel.CITY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertEquals(location.getAccuracyRadius().intValue(), 225);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 40.4172, ERROR);
        assertEquals(location.getLongitude(), -3.684, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "ES");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Espagne");
        assertEquals(location.getRegisteredCountryCode(), "ES");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Espagne");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);

        location = locator.locate(inetAddress, IPGeolocationLevel.CITY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertEquals(location.getAccuracyRadius().intValue(), 225);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 40.4172, ERROR);
        assertEquals(location.getLongitude(), -3.684, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "ES");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Espagne");
        assertEquals(location.getRegisteredCountryCode(), "ES");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Espagne");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);


        // test with COUNTRY level
        location = locator.locate(address, IPGeolocationLevel.COUNTRY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertNull(location.getAccuracyRadius());
        assertNull(location.getMetroCode());
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
        assertFalse(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertNull(location.getSubdivisionCodes());
        assertNull(location.getSubdivisionNames());
        assertEquals(location.getCountryCode(), "ES");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Espagne");
        assertEquals(location.getRegisteredCountryCode(), "ES");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Espagne");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);

        location = locator.locate(inetAddress, IPGeolocationLevel.COUNTRY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertNull(location.getAccuracyRadius());
        assertNull(location.getMetroCode());
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
        assertFalse(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertNull(location.getSubdivisionCodes());
        assertNull(location.getSubdivisionNames());
        assertEquals(location.getCountryCode(), "ES");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Espagne");
        assertEquals(location.getRegisteredCountryCode(), "ES");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Espagne");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);
    }

    @Test
    public void testLocateItalyAddress() throws UnknownHostException,
            IPLocationNotFoundException, IPGeolocationDisabledException {

        final IPGeolocator locator = IPGeolocator.getInstance();

        final String address = "151.38.39.114";
        final InetAddress inetAddress = InetAddress.getByName(address);

        // test with default level
        IPLocation location = locator.locate(address);
        assertNotNull(location);
        assertEquals(location.getCity(), "Milan");
        assertEquals(location.getTimeZone().getID(), "Europe/Rome");
        assertEquals(location.getAccuracyRadius().intValue(), 201);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 45.4667, ERROR);
        assertEquals(location.getLongitude(), 9.2, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertEquals(location.getPostalCode(), "20131");
        assertTrue(location.getSubdivisionCodes().contains("MI"));
        assertTrue(location.getSubdivisionCodes().contains("25"));
        assertTrue(location.getSubdivisionNames().contains("Milan"));
        assertTrue(location.getSubdivisionNames().contains("Lombardy"));
        assertEquals(location.getCountryCode(), "IT");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Italy");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Italie");
        assertEquals(location.getRegisteredCountryCode(), "IT");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Italy");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Italie");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);


        location = locator.locate(inetAddress);
        assertNotNull(location);
        assertEquals(location.getCity(), "Milan");
        assertEquals(location.getTimeZone().getID(), "Europe/Rome");
        assertEquals(location.getAccuracyRadius().intValue(), 201);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 45.4667, ERROR);
        assertEquals(location.getLongitude(), 9.2, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertEquals(location.getPostalCode(), "20131");
        assertTrue(location.getSubdivisionCodes().contains("MI"));
        assertTrue(location.getSubdivisionCodes().contains("25"));
        assertTrue(location.getSubdivisionNames().contains("Milan"));
        assertTrue(location.getSubdivisionNames().contains("Lombardy"));
        assertEquals(location.getCountryCode(), "IT");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Italy");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Italie");
        assertEquals(location.getRegisteredCountryCode(), "IT");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Italy");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Italie");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);

        // test with CITY level
        location = locator.locate(address, IPGeolocationLevel.CITY);
        assertNotNull(location);
        assertEquals(location.getCity(), "Milan");
        assertEquals(location.getTimeZone().getID(), "Europe/Rome");
        assertEquals(location.getAccuracyRadius().intValue(), 201);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 45.4667, ERROR);
        assertEquals(location.getLongitude(), 9.2, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertEquals(location.getPostalCode(), "20131");
        assertTrue(location.getSubdivisionCodes().contains("MI"));
        assertTrue(location.getSubdivisionCodes().contains("25"));
        assertTrue(location.getSubdivisionNames().contains("Milan"));
        assertTrue(location.getSubdivisionNames().contains("Lombardy"));
        assertEquals(location.getCountryCode(), "IT");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Italy");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Italie");
        assertEquals(location.getRegisteredCountryCode(), "IT");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Italy");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Italie");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);

        location = locator.locate(inetAddress, IPGeolocationLevel.CITY);
        assertNotNull(location);
        assertEquals(location.getCity(), "Milan");
        assertEquals(location.getTimeZone().getID(), "Europe/Rome");
        assertEquals(location.getAccuracyRadius().intValue(), 201);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 45.4667, ERROR);
        assertEquals(location.getLongitude(), 9.2, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertEquals(location.getPostalCode(), "20131");
        assertTrue(location.getSubdivisionCodes().contains("MI"));
        assertTrue(location.getSubdivisionCodes().contains("25"));
        assertTrue(location.getSubdivisionNames().contains("Milan"));
        assertTrue(location.getSubdivisionNames().contains("Lombardy"));
        assertEquals(location.getCountryCode(), "IT");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Italy");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Italie");
        assertEquals(location.getRegisteredCountryCode(), "IT");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Italy");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Italie");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);


        // test with COUNTRY level
        location = locator.locate(address, IPGeolocationLevel.COUNTRY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertNull(location.getAccuracyRadius());
        assertNull(location.getMetroCode());
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
        assertFalse(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertNull(location.getSubdivisionCodes());
        assertNull(location.getSubdivisionNames());
        assertEquals(location.getCountryCode(), "IT");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Italy");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Italie");
        assertEquals(location.getRegisteredCountryCode(), "IT");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Italy");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Italie");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);

        location = locator.locate(inetAddress, IPGeolocationLevel.COUNTRY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertNull(location.getAccuracyRadius());
        assertNull(location.getMetroCode());
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
        assertFalse(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertNull(location.getSubdivisionCodes());
        assertNull(location.getSubdivisionNames());
        assertEquals(location.getCountryCode(), "IT");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Italy");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Italie");
        assertEquals(location.getRegisteredCountryCode(), "IT");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Italy");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Italie");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);
    }

    @Test
    public void testLocateUnitedStatesAddress() throws UnknownHostException,
            IPLocationNotFoundException, IPGeolocationDisabledException {

        final IPGeolocator locator = IPGeolocator.getInstance();

        final String address = "12.25.205.51";
        final InetAddress inetAddress = InetAddress.getByName(address);

        // test with default level
        IPLocation location = locator.locate(address);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertEquals(location.getAccuracyRadius().intValue(), 937);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 37.751, ERROR);
        assertEquals(location.getLongitude(), -97.822, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);


        location = locator.locate(inetAddress);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertEquals(location.getAccuracyRadius().intValue(), 937);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 37.751, ERROR);
        assertEquals(location.getLongitude(), -97.822, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);

        // test with CITY level
        location = locator.locate(address, IPGeolocationLevel.CITY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertEquals(location.getAccuracyRadius().intValue(), 937);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 37.751, ERROR);
        assertEquals(location.getLongitude(), -97.822, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);

        location = locator.locate(inetAddress, IPGeolocationLevel.CITY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertEquals(location.getAccuracyRadius().intValue(), 937);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 37.751, ERROR);
        assertEquals(location.getLongitude(), -97.822, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);


        // test with COUNTRY level
        location = locator.locate(address, IPGeolocationLevel.COUNTRY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertNull(location.getAccuracyRadius());
        assertNull(location.getMetroCode());
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
        assertFalse(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertNull(location.getSubdivisionCodes());
        assertNull(location.getSubdivisionNames());
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);

        location = locator.locate(inetAddress, IPGeolocationLevel.COUNTRY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertNull(location.getAccuracyRadius());
        assertNull(location.getMetroCode());
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
        assertFalse(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertNull(location.getSubdivisionCodes());
        assertNull(location.getSubdivisionNames());
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);
    }

    @Test
    public void testLocateUnitedStatesAddress2() throws UnknownHostException,
            IPLocationNotFoundException, IPGeolocationDisabledException {

        final IPGeolocator locator = IPGeolocator.getInstance();

        final String address = "64.81.104.131";
        final InetAddress inetAddress = InetAddress.getByName(address);

        // test with default level
        IPLocation location = locator.locate(address);
        assertNotNull(location);
        assertEquals(location.getCity(), "San Jose");
        assertEquals(location.getTimeZone().getID(), "America/Los_Angeles");
        assertEquals(location.getAccuracyRadius().intValue(), 937);
        assertEquals(location.getMetroCode().intValue(), 807);
        assertEquals(location.getLatitude(), 37.3874, ERROR);
        assertEquals(location.getLongitude(), -121.9024, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertEquals(location.getPostalCode(), "95131");
        assertTrue(location.getSubdivisionCodes().contains("CA"));
        assertTrue(location.getSubdivisionNames().contains("California"));
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);


        location = locator.locate(inetAddress);
        assertNotNull(location);
        assertEquals(location.getCity(), "San Jose");
        assertEquals(location.getTimeZone().getID(), "America/Los_Angeles");
        assertEquals(location.getAccuracyRadius().intValue(), 937);
        assertEquals(location.getMetroCode().intValue(), 807);
        assertEquals(location.getLatitude(), 37.3874, ERROR);
        assertEquals(location.getLongitude(), -121.9024, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertEquals(location.getPostalCode(), "95131");
        assertTrue(location.getSubdivisionCodes().contains("CA"));
        assertTrue(location.getSubdivisionNames().contains("California"));
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);

        // test with CITY level
        location = locator.locate(address, IPGeolocationLevel.CITY);
        assertNotNull(location);
        assertEquals(location.getCity(), "San Jose");
        assertEquals(location.getTimeZone().getID(), "America/Los_Angeles");
        assertEquals(location.getAccuracyRadius().intValue(), 937);
        assertEquals(location.getMetroCode().intValue(), 807);
        assertEquals(location.getLatitude(), 37.3874, ERROR);
        assertEquals(location.getLongitude(), -121.9024, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertEquals(location.getPostalCode(), "95131");
        assertTrue(location.getSubdivisionCodes().contains("CA"));
        assertTrue(location.getSubdivisionNames().contains("California"));
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);

        location = locator.locate(inetAddress, IPGeolocationLevel.CITY);
        assertNotNull(location);
        assertEquals(location.getCity(), "San Jose");
        assertEquals(location.getTimeZone().getID(), "America/Los_Angeles");
        assertEquals(location.getAccuracyRadius().intValue(), 937);
        assertEquals(location.getMetroCode().intValue(), 807);
        assertEquals(location.getLatitude(), 37.3874, ERROR);
        assertEquals(location.getLongitude(), -121.9024, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertEquals(location.getPostalCode(), "95131");
        assertTrue(location.getSubdivisionCodes().contains("CA"));
        assertTrue(location.getSubdivisionNames().contains("California"));
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);


        // test with COUNTRY level
        location = locator.locate(address, IPGeolocationLevel.COUNTRY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertNull(location.getAccuracyRadius());
        assertNull(location.getMetroCode());
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
        assertFalse(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertNull(location.getSubdivisionCodes());
        assertNull(location.getSubdivisionNames());
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);

        location = locator.locate(inetAddress, IPGeolocationLevel.COUNTRY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertNull(location.getAccuracyRadius());
        assertNull(location.getMetroCode());
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
        assertFalse(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertNull(location.getSubdivisionCodes());
        assertNull(location.getSubdivisionNames());
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);
    }

    @Test
    public void testLocateColombiaAddress() throws UnknownHostException,
            IPLocationNotFoundException, IPGeolocationDisabledException {

        final IPGeolocator locator = IPGeolocator.getInstance();

        final String address = "200.21.225.82";
        final InetAddress inetAddress = InetAddress.getByName(address);

        // test with default level
        IPLocation location = locator.locate(address);
        assertNotNull(location);
        assertEquals(location.getCity(), "Bogotá");
        assertEquals(location.getTimeZone().getID(), "America/Bogota");
        assertEquals(location.getAccuracyRadius().intValue(), 266);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 4.6492, ERROR);
        assertEquals(location.getLongitude(), -74.0628, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().contains("DC"));
        assertTrue(location.getSubdivisionNames().contains("Bogota D.C."));
        assertEquals(location.getCountryCode(), "CO");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Colombia");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Colombie");
        assertEquals(location.getRegisteredCountryCode(), "CO");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Colombia");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Colombie");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "SA");
        assertEquals(location.getContinentName(), "South America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);


        location = locator.locate(inetAddress);
        assertNotNull(location);
        assertEquals(location.getCity(), "Bogotá");
        assertEquals(location.getTimeZone().getID(), "America/Bogota");
        assertEquals(location.getAccuracyRadius().intValue(), 266);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 4.6492, ERROR);
        assertEquals(location.getLongitude(), -74.0628, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().contains("DC"));
        assertTrue(location.getSubdivisionNames().contains("Bogota D.C."));
        assertEquals(location.getCountryCode(), "CO");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Colombia");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Colombie");
        assertEquals(location.getRegisteredCountryCode(), "CO");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Colombia");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Colombie");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "SA");
        assertEquals(location.getContinentName(), "South America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);

        // test with CITY level
        location = locator.locate(address, IPGeolocationLevel.CITY);
        assertNotNull(location);
        assertEquals(location.getCity(), "Bogotá");
        assertEquals(location.getTimeZone().getID(), "America/Bogota");
        assertEquals(location.getAccuracyRadius().intValue(), 266);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 4.6492, ERROR);
        assertEquals(location.getLongitude(), -74.0628, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().contains("DC"));
        assertTrue(location.getSubdivisionNames().contains("Bogota D.C."));
        assertEquals(location.getCountryCode(), "CO");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Colombia");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Colombie");
        assertEquals(location.getRegisteredCountryCode(), "CO");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Colombia");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Colombie");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "SA");
        assertEquals(location.getContinentName(), "South America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);

        location = locator.locate(inetAddress, IPGeolocationLevel.CITY);
        assertNotNull(location);
        assertEquals(location.getCity(), "Bogotá");
        assertEquals(location.getTimeZone().getID(), "America/Bogota");
        assertEquals(location.getAccuracyRadius().intValue(), 266);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 4.6492, ERROR);
        assertEquals(location.getLongitude(), -74.0628, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().contains("DC"));
        assertTrue(location.getSubdivisionNames().contains("Bogota D.C."));
        assertEquals(location.getCountryCode(), "CO");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Colombia");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Colombie");
        assertEquals(location.getRegisteredCountryCode(), "CO");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Colombia");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Colombie");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "SA");
        assertEquals(location.getContinentName(), "South America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);


        // test with COUNTRY level
        location = locator.locate(address, IPGeolocationLevel.COUNTRY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertNull(location.getAccuracyRadius());
        assertNull(location.getMetroCode());
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
        assertFalse(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertNull(location.getSubdivisionCodes());
        assertNull(location.getSubdivisionNames());
        assertEquals(location.getCountryCode(), "CO");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Colombia");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Colombie");
        assertEquals(location.getRegisteredCountryCode(), "CO");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Colombia");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Colombie");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "SA");
        assertEquals(location.getContinentName(), "South America");
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);

        location = locator.locate(inetAddress, IPGeolocationLevel.COUNTRY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertNull(location.getAccuracyRadius());
        assertNull(location.getMetroCode());
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
        assertFalse(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertNull(location.getSubdivisionCodes());
        assertNull(location.getSubdivisionNames());
        assertEquals(location.getCountryCode(), "CO");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Colombia");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Colombie");
        assertEquals(location.getRegisteredCountryCode(), "CO");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Colombia");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Colombie");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "SA");
        assertEquals(location.getContinentName(), "South America");
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);
    }

    @Test
    public void testLocateDnsAddress() throws UnknownHostException,
            IPLocationNotFoundException, IPGeolocationDisabledException {

        final IPGeolocator locator = IPGeolocator.getInstance();

        final String address = "www.mobbio.com";
        final InetAddress inetAddress = InetAddress.getByName(address);

        // test with default level
        IPLocation location = locator.locate(address);
        assertNotNull(location);
        assertEquals(location.getCity(), "Scottsdale");
        assertEquals(location.getTimeZone().getID(), "America/Phoenix");
        assertEquals(location.getAccuracyRadius().intValue(), 937);
        assertEquals(location.getMetroCode().intValue(), 753);
        assertEquals(location.getLatitude(), 33.6119, ERROR);
        assertEquals(location.getLongitude(), -111.8906, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertEquals(location.getPostalCode(), "85260");
        assertTrue(location.getSubdivisionCodes().contains("AZ"));
        assertTrue(location.getSubdivisionNames().contains("Arizona"));
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);


        location = locator.locate(inetAddress);
        assertNotNull(location);
        assertEquals(location.getCity(), "Scottsdale");
        assertEquals(location.getTimeZone().getID(), "America/Phoenix");
        assertEquals(location.getAccuracyRadius().intValue(), 937);
        assertEquals(location.getMetroCode().intValue(), 753);
        assertEquals(location.getLatitude(), 33.6119, ERROR);
        assertEquals(location.getLongitude(), -111.8906, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertEquals(location.getPostalCode(), "85260");
        assertTrue(location.getSubdivisionCodes().contains("AZ"));
        assertTrue(location.getSubdivisionNames().contains("Arizona"));
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);

        // test with CITY level
        assertNotNull(location);
        assertEquals(location.getCity(), "Scottsdale");
        assertEquals(location.getTimeZone().getID(), "America/Phoenix");
        assertEquals(location.getAccuracyRadius().intValue(), 937);
        assertEquals(location.getMetroCode().intValue(), 753);
        assertEquals(location.getLatitude(), 33.6119, ERROR);
        assertEquals(location.getLongitude(), -111.8906, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertEquals(location.getPostalCode(), "85260");
        assertTrue(location.getSubdivisionCodes().contains("AZ"));
        assertTrue(location.getSubdivisionNames().contains("Arizona"));
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);

        location = locator.locate(inetAddress, IPGeolocationLevel.CITY);
        assertNotNull(location);
        assertEquals(location.getCity(), "Scottsdale");
        assertEquals(location.getTimeZone().getID(), "America/Phoenix");
        assertEquals(location.getAccuracyRadius().intValue(), 937);
        assertEquals(location.getMetroCode().intValue(), 753);
        assertEquals(location.getLatitude(), 33.6119, ERROR);
        assertEquals(location.getLongitude(), -111.8906, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertEquals(location.getPostalCode(), "85260");
        assertTrue(location.getSubdivisionCodes().contains("AZ"));
        assertTrue(location.getSubdivisionNames().contains("Arizona"));
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);


        // test with COUNTRY level
        location = locator.locate(address, IPGeolocationLevel.COUNTRY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertNull(location.getAccuracyRadius());
        assertNull(location.getMetroCode());
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
        assertFalse(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertNull(location.getSubdivisionCodes());
        assertNull(location.getSubdivisionNames());
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);

        location = locator.locate(inetAddress, IPGeolocationLevel.COUNTRY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertNull(location.getAccuracyRadius());
        assertNull(location.getMetroCode());
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
        assertFalse(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertNull(location.getSubdivisionCodes());
        assertNull(location.getSubdivisionNames());
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);
    }

    @Test
    public void testLocateIpv6Address() throws UnknownHostException,
            IPLocationNotFoundException, IPGeolocationDisabledException {

        final IPGeolocator locator = IPGeolocator.getInstance();

        final String address = "2001:4860:0:1001::68";
        final InetAddress inetAddress = InetAddress.getByName(address);

        // test with default level
        IPLocation location = locator.locate(address);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertEquals(location.getAccuracyRadius().intValue(), 100);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 39.76, ERROR);
        assertEquals(location.getLongitude(), -98.5, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);


        location = locator.locate(inetAddress);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertEquals(location.getAccuracyRadius().intValue(), 100);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 39.76, ERROR);
        assertEquals(location.getLongitude(), -98.5, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);

        // test with CITY level
        location = locator.locate(address, IPGeolocationLevel.CITY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertEquals(location.getAccuracyRadius().intValue(), 100);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 39.76, ERROR);
        assertEquals(location.getLongitude(), -98.5, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);

        location = locator.locate(inetAddress, IPGeolocationLevel.CITY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertEquals(location.getAccuracyRadius().intValue(), 100);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 39.76, ERROR);
        assertEquals(location.getLongitude(), -98.5, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);


        // test with COUNTRY level
        location = locator.locate(address, IPGeolocationLevel.COUNTRY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertNull(location.getAccuracyRadius());
        assertNull(location.getMetroCode());
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
        assertFalse(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertNull(location.getSubdivisionCodes());
        assertNull(location.getSubdivisionNames());
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);

        location = locator.locate(inetAddress, IPGeolocationLevel.COUNTRY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertNull(location.getAccuracyRadius());
        assertNull(location.getMetroCode());
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
        assertFalse(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertNull(location.getSubdivisionCodes());
        assertNull(location.getSubdivisionNames());
        assertEquals(location.getCountryCode(), "US");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "États-Unis");
        assertEquals(location.getRegisteredCountryCode(), "US");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "United States");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "États-Unis");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "NA");
        assertEquals(location.getContinentName(), "North America");
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);
    }

    @Test
    public void testLocateLocalAddress() throws UnknownHostException,
            IPGeolocationDisabledException {

        final IPGeolocator locator = IPGeolocator.getInstance();

        final String address = "127.0.0.1";
        final InetAddress inetAddress = InetAddress.getByName(address);

        // test with default level
        IPLocation location = null;
        try {
            location = locator.locate(address);
            fail("IPLocationNotFoundException expected but not thrown");
        } catch (final IPLocationNotFoundException ignore) {
        }

        try {
            location = locator.locate(inetAddress);
            fail("IPLocationNotFoundException expected but not thrown");
        } catch (final IPLocationNotFoundException ignore) {
        }

        // test with CITY level
        try {
            location = locator.locate(address, IPGeolocationLevel.CITY);
            fail("IPLocationNotFoundException expected but not thrown");
        } catch (final IPLocationNotFoundException ignore) {
        }

        try {
            location = locator.locate(inetAddress, IPGeolocationLevel.CITY);
            fail("IPLocationNotFoundException expected but not thrown");
        } catch (final IPLocationNotFoundException ignore) {
        }

        // test with COUNTRY level
        try {
            location = locator.locate(address, IPGeolocationLevel.COUNTRY);
            fail("IPLocationNotFoundException expected but not thrown");
        } catch (final IPLocationNotFoundException ignore) {
        }

        try {
            location = locator.locate(inetAddress, IPGeolocationLevel.COUNTRY);
            fail("IPLocationNotFoundException expected but not thrown");
        } catch (final IPLocationNotFoundException ignore) {
        }
        assertNull(location);
    }

    @Test
    public void testLocateAfterReset() throws IOException,
            IPLocationNotFoundException, IPGeolocationDisabledException {

        IPGeolocator locator1 = IPGeolocator.getInstance();

        final String address = "37.15.50.115";
        final InetAddress inetAddress = InetAddress.getByName(address);
        IPLocation location = locator1.locate(address);
        assertNotNull(location);

        // reset
        IPGeolocator.reset();

        // locate again
        IPGeolocator locator2 = IPGeolocator.getInstance();
        assertNotSame(locator1, locator2);


        // test with default level
        location = locator2.locate(address);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertEquals(location.getAccuracyRadius().intValue(), 225);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 40.4172, ERROR);
        assertEquals(location.getLongitude(), -3.684, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "ES");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Espagne");
        assertEquals(location.getRegisteredCountryCode(), "ES");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Espagne");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);


        location = locator2.locate(inetAddress);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertEquals(location.getAccuracyRadius().intValue(), 225);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 40.4172, ERROR);
        assertEquals(location.getLongitude(), -3.684, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "ES");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Espagne");
        assertEquals(location.getRegisteredCountryCode(), "ES");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Espagne");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);

        // test with CITY level
        location = locator2.locate(address, IPGeolocationLevel.CITY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertEquals(location.getAccuracyRadius().intValue(), 225);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 40.4172, ERROR);
        assertEquals(location.getLongitude(), -3.684, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "ES");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Espagne");
        assertEquals(location.getRegisteredCountryCode(), "ES");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Espagne");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);

        location = locator2.locate(inetAddress, IPGeolocationLevel.CITY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertEquals(location.getAccuracyRadius().intValue(), 225);
        assertNull(location.getMetroCode());
        assertEquals(location.getLatitude(), 40.4172, ERROR);
        assertEquals(location.getLongitude(), -3.684, ERROR);
        assertTrue(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertTrue(location.getSubdivisionCodes().isEmpty());
        assertTrue(location.getSubdivisionNames().isEmpty());
        assertEquals(location.getCountryCode(), "ES");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Espagne");
        assertEquals(location.getRegisteredCountryCode(), "ES");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Espagne");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);


        // test with COUNTRY level
        location = locator2.locate(address, IPGeolocationLevel.COUNTRY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertNull(location.getAccuracyRadius());
        assertNull(location.getMetroCode());
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
        assertFalse(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertNull(location.getSubdivisionCodes());
        assertNull(location.getSubdivisionNames());
        assertEquals(location.getCountryCode(), "ES");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Espagne");
        assertEquals(location.getRegisteredCountryCode(), "ES");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Espagne");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);

        location = locator2.locate(inetAddress, IPGeolocationLevel.COUNTRY);
        assertNotNull(location);
        assertNull(location.getCity());
        assertNull(location.getTimeZone());
        assertNull(location.getAccuracyRadius());
        assertNull(location.getMetroCode());
        assertNull(location.getLatitude());
        assertNull(location.getLongitude());
        assertFalse(location.areCoordinatesAvailable());
        assertNull(location.getPostalCode());
        assertNull(location.getSubdivisionCodes());
        assertNull(location.getSubdivisionNames());
        assertEquals(location.getCountryCode(), "ES");
        assertEquals(location.getCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getCountryName(Locale.FRENCH),
                "Espagne");
        assertEquals(location.getRegisteredCountryCode(), "ES");
        assertEquals(location.getRegisteredCountryName(Locale.ENGLISH),
                "Spain");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH),
                "Espagne");
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertEquals(location.getContinentCode(), "EU");
        assertEquals(location.getContinentName(), "Europe");
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);
    }

    @Test(expected = IPGeolocationDisabledException.class)
    public void testLocateAfterClose() throws IOException,
            IPLocationNotFoundException, IPGeolocationDisabledException {

        IPGeolocator locator = IPGeolocator.getInstance();

        final String address = "37.15.50.115";
        final InetAddress inetAddress = InetAddress.getByName(address);
        IPLocation location = locator.locate(address);
        assertNotNull(location);

        // close
        locator.close();

        // locate again
        assertSame(locator, IPGeolocator.getInstance());

        location = locator.locate(address);
    }

    @Test
    public void testLocateWhenDisabledLevel() throws ConfigurationException, IOException {
        final Properties props = new Properties();
        props.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_COUNTRY_DATABASE_FILE_PROPERTY, COUNTRY_FILE);
        props.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_CITY_DATABASE_FILE_PROPERTY, CITY_FILE);
        props.setProperty(GeolocationConfigurationFactory.IP_GEOLOCATION_LEVEL_PROPERTY,
                IPGeolocationLevel.DISABLED.getValue());

        GeolocationConfigurationFactory.getInstance().configure(props);

        final IPGeolocator locator = IPGeolocator.getInstance();
        final String address = "64.4.4.4";

        // Force IPLocationDisabledException
        try {
            locator.locate(address);
            fail("IPLocationDisabledException expected but not thrown");
        } catch (final IPGeolocationDisabledException | IPLocationNotFoundException ignore) {
        }
        try {
            locator.locate(InetAddress.getByName(address));
            fail("IPLocationDisabledException expected but not thrown");
        } catch (final IPGeolocationDisabledException | IPLocationNotFoundException ignore) {
        }

        try {
            locator.locate(address, IPGeolocationLevel.CITY);
            fail("IPLocationDisabledException expected but not thrown");
        } catch (final IPGeolocationDisabledException | IPLocationNotFoundException ignore) {
        }
        try {
            locator.locate(InetAddress.getByName(address),
                    IPGeolocationLevel.CITY);
            fail("IPLocationDisabledException expected but not thrown");
        } catch (final IPGeolocationDisabledException | IPLocationNotFoundException ignore) {
        }

        try {
            locator.locate(address, IPGeolocationLevel.COUNTRY);
            fail("IPLocationDisabledException expected but not thrown");
        } catch (final IPGeolocationDisabledException | IPLocationNotFoundException ignore) {
        }
        try {
            locator.locate(InetAddress.getByName(address),
                    IPGeolocationLevel.COUNTRY);
            fail("IPLocationDisabledException expected but not thrown");
        } catch (final IPGeolocationDisabledException | IPLocationNotFoundException ignore) {
        }

        IPGeolocator.reset();
    }

    @Test
    public void testForceIPLocationDisabledException()
            throws IOException, IPLocationNotFoundException {
        final IPGeolocator locator = IPGeolocator.getInstance();
        final String address = "64.4.4.4";
        locator.close();

        // Force IPLocationDisabledException
        try {
            locator.locate(address);
            fail("IPLocationDisabledException expected but not thrown");
        } catch (final IPGeolocationDisabledException ignore) {
        }
        try {
            locator.locate(InetAddress.getByName(address));
            fail("IPLocationDisabledException expected but not thrown");
        } catch (final IPGeolocationDisabledException ignore) {
        }

        try {
            locator.locate(address, IPGeolocationLevel.CITY);
            fail("IPLocationDisabledException expected but not thrown");
        } catch (final IPGeolocationDisabledException ignore) {
        }
        try {
            locator.locate(InetAddress.getByName(address),
                    IPGeolocationLevel.CITY);
            fail("IPLocationDisabledException expected but not thrown");
        } catch (final IPGeolocationDisabledException ignore) {
        }

        try {
            locator.locate(address, IPGeolocationLevel.COUNTRY);
            fail("IPLocationDisabledException expected but not thrown");
        } catch (final IPGeolocationDisabledException ignore) {
        }
        try {
            locator.locate(InetAddress.getByName(address),
                    IPGeolocationLevel.COUNTRY);
            fail("IPLocationDisabledException expected but not thrown");
        } catch (final IPGeolocationDisabledException ignore) {
        }

        IPGeolocator.reset();
    }

    @Test
    public void testCloseWhenNoErrorCityLevel() throws ConfigurationException,
            NoSuchFieldException, IllegalAccessException, IOException {
        final Properties props = new Properties();
        props.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_COUNTRY_DATABASE_FILE_PROPERTY, COUNTRY_FILE);
        props.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_CITY_DATABASE_FILE_PROPERTY, CITY_FILE);
        props.setProperty(GeolocationConfigurationFactory.IP_GEOLOCATION_LEVEL_PROPERTY,
                IPGeolocationLevel.CITY.getValue());

        GeolocationConfigurationFactory.getInstance().configure(props);

        final IPGeolocator locator = IPGeolocator.getInstance();

        // replace city reader by spy
        final Field field = IPGeolocator.class.getDeclaredField("mCityReader");
        field.setAccessible(true);
        final DatabaseReader cityReader = (DatabaseReader) field.get(locator);

        final DatabaseReader cityReaderSpy = spy(cityReader);

        field.set(locator, cityReaderSpy);

        locator.close();

        verify(cityReaderSpy, only()).close();
    }

    @Test
    public void testCloseWhenNoErrorCountryLevel() throws ConfigurationException,
            NoSuchFieldException, IllegalAccessException, IOException {
        final Properties props = new Properties();
        props.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_COUNTRY_DATABASE_FILE_PROPERTY, COUNTRY_FILE);
        props.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_CITY_DATABASE_FILE_PROPERTY, CITY_FILE);
        props.setProperty(GeolocationConfigurationFactory.IP_GEOLOCATION_LEVEL_PROPERTY,
                IPGeolocationLevel.COUNTRY.getValue());

        GeolocationConfigurationFactory.getInstance().configure(props);

        final IPGeolocator locator = IPGeolocator.getInstance();

        // replace country reader by spy
        final Field field = IPGeolocator.class.getDeclaredField("mCountryReader");
        field.setAccessible(true);
        final DatabaseReader countryReader = (DatabaseReader) field.get(locator);

        final DatabaseReader countryReaderSpy = spy(countryReader);

        field.set(locator, countryReaderSpy);

        locator.close();

        verify(countryReaderSpy, only()).close();

    }

    @Test
    public void testCloseErrorWhenCityLevel() throws ConfigurationException,
            IOException, NoSuchFieldException, IllegalAccessException {
        final Properties props = new Properties();
        props.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_COUNTRY_DATABASE_FILE_PROPERTY, COUNTRY_FILE);
        props.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_CITY_DATABASE_FILE_PROPERTY, CITY_FILE);
        props.setProperty(GeolocationConfigurationFactory.IP_GEOLOCATION_LEVEL_PROPERTY,
                IPGeolocationLevel.CITY.getValue());

        GeolocationConfigurationFactory.getInstance().configure(props);

        final IPGeolocator locator = IPGeolocator.getInstance();

        // replace country reader by mock
        final DatabaseReader cityReaderMock = mock(DatabaseReader.class);
        doThrow(IOException.class).when(cityReaderMock).close();

        final Field field = IPGeolocator.class.getDeclaredField("mCityReader");
        field.setAccessible(true);
        field.set(locator, cityReaderMock);

        locator.close();

        verify(cityReaderMock, only()).close();
    }

    @Test
    public void testCloseErrorWhenCountryLevel() throws ConfigurationException,
            IOException, NoSuchFieldException, IllegalAccessException {
        final Properties props = new Properties();
        props.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_COUNTRY_DATABASE_FILE_PROPERTY, COUNTRY_FILE);
        props.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_CITY_DATABASE_FILE_PROPERTY, CITY_FILE);
        props.setProperty(GeolocationConfigurationFactory.IP_GEOLOCATION_LEVEL_PROPERTY,
                IPGeolocationLevel.COUNTRY.getValue());

        GeolocationConfigurationFactory.getInstance().configure(props);

        final IPGeolocator locator = IPGeolocator.getInstance();

        // replace country reader by mock
        final DatabaseReader countryReaderMock = mock(DatabaseReader.class);
        doThrow(IOException.class).when(countryReaderMock).close();

        final Field field = IPGeolocator.class.getDeclaredField("mCountryReader");
        field.setAccessible(true);
        field.set(locator, countryReaderMock);

        locator.close();

        verify(countryReaderMock, only()).close();
    }
}
