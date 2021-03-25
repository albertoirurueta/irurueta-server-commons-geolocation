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

import com.irurueta.navigation.utils.LocationUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class IPLocationTest {

    @Test
    public void testConstructor() {
        final IPLocation location = new IPLocation(IPGeolocationLevel.CITY);
        
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
        assertNull(location.getCountryCode());
        assertNull(location.getCountryName());
        assertNull(location.getCountryName(Locale.ENGLISH));
        assertNull(location.getRegisteredCountryCode());
        assertNull(location.getRegisteredCountryName());
        assertNull(location.getRegisteredCountryName(Locale.ENGLISH));
        assertNull(location.getAutonomousSystemNumber());
        assertNull(location.getDomain());
        assertNull(location.getIsp());
        assertNull(location.getOrganization());
        assertNull(location.getContinentCode());
        assertNull(location.getContinentName());
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);
    }
    
    @Test
    public void testGetCity() {
        final IPLocation location = new IPLocation(IPGeolocationLevel.CITY);
        
        assertNull(location.getCity());
        
        location.mCity = "Barcelona";
        
        assertEquals(location.getCity(), "Barcelona");
    }
    
    @Test
    public void testGetTimeZone() {
        final IPLocation location = new IPLocation(IPGeolocationLevel.CITY);
        
        assertNull(location.getTimeZone());
        
        location.mTimeZone = TimeZone.getTimeZone("Europe/Madrid");
        
        assertEquals(location.getTimeZone(), 
                TimeZone.getTimeZone("Europe/Madrid"));
    }
    
    @Test
    public void testGetAccuracyRadius() {
        final IPLocation location = new IPLocation(IPGeolocationLevel.CITY);
        
        assertNull(location.getAccuracyRadius());
        
        location.mAccuracyRadius = 100;
        
        assertEquals(location.getAccuracyRadius().intValue(), 100);
    }
    
    @Test
    public void testGetMetroCode() {
        final IPLocation location = new IPLocation(IPGeolocationLevel.CITY);
        
        assertNull(location.getMetroCode());
        
        location.mMetroCode = 123;
        
        assertEquals(location.getMetroCode().intValue(), 123);
    }
    
    @Test
    public void testGetLatitude() {
        final IPLocation location = new IPLocation(IPGeolocationLevel.CITY);
        
        assertNull(location.getLatitude());
        
        location.mLatitude = 41.0;
        
        assertEquals(location.getLatitude(), 41.0, 0.0);
    }
    
    @Test
    public void testGetLongitude() {
        final IPLocation location = new IPLocation(IPGeolocationLevel.CITY);
        
        assertNull(location.getLongitude());
        
        location.mLongitude = 2.0;
        
        assertEquals(location.getLongitude(), 2.0, 0.0);
    }
    
    @Test
    public void testAreCoordinatesAvailable() {
        final IPLocation location = new IPLocation(IPGeolocationLevel.CITY);
        
        assertFalse(location.areCoordinatesAvailable());
        
        location.mLatitude = 41.0;
        assertFalse(location.areCoordinatesAvailable());
        
        location.mLongitude = 2.0;
        assertTrue(location.areCoordinatesAvailable());
    }
    
    @Test
    public void testGetPostalCode() {
        final IPLocation location = new IPLocation(IPGeolocationLevel.CITY);
        
        assertNull(location.getPostalCode());
        
        location.mPostalCode = "08008";
        
        assertEquals(location.getPostalCode(), "08008");
    }
    
    @Test
    public void testGetSubdivisionCodes() {
        final IPLocation location = new IPLocation(IPGeolocationLevel.CITY);
        
        assertNull(location.getSubdivisionCodes());
        
        final List<String> subdivisionCodes = new ArrayList<>();
        subdivisionCodes.add("CAT");
        
        location.mSubdivisionCodes = subdivisionCodes;
        
        assertSame(location.getSubdivisionCodes(), subdivisionCodes);
    }
    
    @Test
    public void testGetSubdivisionNames() {
        final IPLocation location = new IPLocation(IPGeolocationLevel.CITY);
        
        assertNull(location.getSubdivisionNames());
        
        final List<String> subdivisionNames = new ArrayList<>();
        subdivisionNames.add("Catalonia");
        
        location.mSubdivisionNames = subdivisionNames;
        
        assertSame(location.getSubdivisionNames(), subdivisionNames);
    }
    
    @Test
    public void testGetCountryCodeAndGetCountryName() {
        final IPLocation location = new IPLocation(IPGeolocationLevel.CITY);
        
        assertNull(location.getCountryCode());
        assertNull(location.getCountryName());
        assertNull(location.getCountryName(Locale.ENGLISH));
        
        location.mCountryCode = "ES";
        
        assertEquals(location.getCountryCode(), "ES");        
        assertEquals(location.getCountryName(), location.getCountryName(
                Locale.getDefault()));
        assertEquals(location.getCountryName(Locale.FRENCH), "Espagne");
        
        // sets wrong country code
        location.mCountryCode = "WRONG";
        assertNull(location.getCountryName());
        assertNull(location.getCountryName(Locale.FRENCH));        
        
        // fallback country name
        location.mCountryName = "Spain";
        
        assertEquals(location.getCountryName(), "Spain");
        assertEquals(location.getCountryName(Locale.FRENCH), "Spain");
        
        // set null country code
        location.mCountryCode = null;
        
        assertEquals(location.getCountryName(), "Spain");
        assertEquals(location.getCountryName(Locale.FRENCH), "Spain");        
    }
    
    @Test
    public void testGetRegisteredCountryCodeAndGetRegisteredCountryName() {
        final IPLocation location = new IPLocation(IPGeolocationLevel.CITY);
        
        assertNull(location.getRegisteredCountryCode());
        assertNull(location.getRegisteredCountryName());
        assertNull(location.getRegisteredCountryName(Locale.ENGLISH));
        
        location.mRegisteredCountryCode = "ES";
        
        assertEquals(location.getRegisteredCountryCode(), "ES");        
        assertEquals(location.getRegisteredCountryName(), 
                location.getRegisteredCountryName(Locale.getDefault()));
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH), 
                "Espagne");
        
        // sets wrong country code
        location.mRegisteredCountryCode = "WRONG";
        assertNull(location.getRegisteredCountryName());
        assertNull(location.getRegisteredCountryName(Locale.FRENCH));        
        
        // fallback country name
        location.mRegisteredCountryName = "Spain";
        
        assertEquals(location.getRegisteredCountryName(), "Spain");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH), "Spain");
        
        // set null country code
        location.mRegisteredCountryCode = null;
        
        assertEquals(location.getRegisteredCountryName(), "Spain");
        assertEquals(location.getRegisteredCountryName(Locale.FRENCH), "Spain");          
    }
    
    @Test
    public void testGetAutonomousSystemNumber() {
        final IPLocation location = new IPLocation(IPGeolocationLevel.CITY);
        
        assertNull(location.getAutonomousSystemNumber());
        
        // new value
        location.mAutonomousSystemNumber = 123;
        
        assertEquals(location.getAutonomousSystemNumber().intValue(), 123);
    }
    
    @Test
    public void testGetDomain() {
        final IPLocation location = new IPLocation(IPGeolocationLevel.CITY);
        
        assertNull(location.getDomain());
        
        location.mDomain = "irurueta.com";
        
        assertEquals(location.getDomain(), "irurueta.com");
    }
    
    @Test
    public void testGetIsp() {
        final IPLocation location = new IPLocation(IPGeolocationLevel.CITY);
        
        assertNull(location.getIsp());
        
        location.mIsp = "Vodafone";
        
        assertEquals(location.getIsp(), "Vodafone");
    }
    
    @Test
    public void testGetOrganization() {
        final IPLocation location = new IPLocation(IPGeolocationLevel.CITY);
        
        assertNull(location.getOrganization());
        
        location.mOrganization = "Inditex";
        
        assertEquals(location.getOrganization(), "Inditex");
    }
    
    @Test
    public void testGetContinentCode() {
        final IPLocation location = new IPLocation(IPGeolocationLevel.CITY);
        
        assertNull(location.getContinentCode());
        
        location.mContinentCode = "EU";
        
        assertEquals(location.getContinentCode(), "EU");
    }
    
    @Test
    public void testGetContinentName() {
        final IPLocation location = new IPLocation(IPGeolocationLevel.CITY);
        
        assertNull(location.getContinentName());
        
        location.mContinentName = "Europe";
        
        assertEquals(location.getContinentName(), "Europe");
    }
    
    @Test
    public void testGetLevel(){
        IPLocation location = new IPLocation(IPGeolocationLevel.CITY);
        assertEquals(location.getLevel(), IPGeolocationLevel.CITY);
        
        location = new IPLocation(IPGeolocationLevel.COUNTRY);
        assertEquals(location.getLevel(), IPGeolocationLevel.COUNTRY);
        
        location = new IPLocation(IPGeolocationLevel.DISABLED);
        assertEquals(location.getLevel(), IPGeolocationLevel.DISABLED);
        
        location = new IPLocation(null);
        assertNull(location.getLevel());
    }
    
    @Test
    public void testDistance(){
        final double latitude1 = 41.0;
        final double longitude1 = 2.0;
        
        final double latitude2 = 41.5;
        final double longitude2 = 2.0;
        
        final IPLocation location1 = new IPLocation(IPGeolocationLevel.CITY);
        location1.mLatitude = latitude1;
        location1.mLongitude = longitude1;
        
        final IPLocation location2 = new IPLocation(IPGeolocationLevel.CITY);
        location2.mLatitude = latitude2;
        location2.mLongitude = longitude2;
        
        assertEquals(location1.distance(location2),
                LocationUtils.distanceBetweenMeters(
                        latitude1, longitude1, latitude2, longitude2), 0.0);
    }    
}
