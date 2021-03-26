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
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GeolocationConfigurationImplTest {

    @Test
    public void testDefaultConstructorAndGetters() {
        final GeolocationConfiguration cfg = new GeolocationConfigurationImpl();

        assertEquals(cfg.getIPGeolocationLevel(),
                GeolocationConfigurationFactory.DEFAULT_IP_GEOLOCATION_LEVEL);
        assertEquals(cfg.isCachingEnabled(), GeolocationConfigurationFactory.
                DEFAULT_CACHING_ENABLED);

        assertEquals(cfg.isIPGeolocationCountryDatabaseEmbedded(),
                GeolocationConfigurationFactory.
                        DEFAULT_IP_GEOLOCATION_COUNTRY_DATABASE_EMBEDDED);
        assertEquals(cfg.getIPGeolocationCountryEmbeddedResource(),
                GeolocationConfigurationFactory.
                        DEFAULT_IP_GEOLOCATION_COUNTRY_EMBEDDED_RESOURCE);
        assertEquals(cfg.getIPGeolocationCountryDatabaseFile(),
                GeolocationConfigurationFactory.
                        DEFAULT_IP_GEOLOCATION_COUNTRY_DATABASE_FILE);

        assertEquals(cfg.isIPGeolocationCityDatabaseEmbedded(),
                GeolocationConfigurationFactory.
                        DEFAULT_IP_GEOLOCATION_CITY_DATABASE_EMBEDDED);
        assertEquals(cfg.getIPGeolocationCityEmbeddedResource(),
                GeolocationConfigurationFactory.
                        DEFAULT_IP_GEOLOCATION_CITY_EMBEDDED_RESOURCE);
        assertEquals(cfg.getIPGeolocationCityDatabaseFile(),
                GeolocationConfigurationFactory.
                        DEFAULT_IP_GEOLOCATION_CITY_DATABASE_FILE);
    }

    @Test
    public void testConstructorWithPropertiesAndGetters()
            throws ConfigurationException {
        // build properties
        final Properties props = buildProperties();

        final GeolocationConfiguration cfg = new GeolocationConfigurationImpl(props);
        assertEquals(cfg.getIPGeolocationLevel(),
                IPGeolocationLevel.COUNTRY);
        assertFalse(cfg.isCachingEnabled());

        assertFalse(cfg.isIPGeolocationCountryDatabaseEmbedded());
        assertEquals(cfg.getIPGeolocationCountryEmbeddedResource(),
                "resource_country");
        assertEquals(cfg.getIPGeolocationCountryDatabaseFile(), "country.mmdb");

        assertFalse(cfg.isIPGeolocationCityDatabaseEmbedded());
        assertEquals(cfg.getIPGeolocationCityEmbeddedResource(),
                "resource_city");
        assertEquals(cfg.getIPGeolocationCityDatabaseFile(), "city.mmdb");
    }

    @Test
    public void testFromProperties() throws ConfigurationException {
        // build properties
        final Properties props = buildProperties();

        final GeolocationConfiguration cfg = new GeolocationConfigurationImpl();

        // load configuration from properties
        cfg.fromProperties(props);

        // check correctness
        assertEquals(cfg.getIPGeolocationLevel(),
                IPGeolocationLevel.COUNTRY);
        assertFalse(cfg.isCachingEnabled());

        assertFalse(cfg.isIPGeolocationCountryDatabaseEmbedded());
        assertEquals(cfg.getIPGeolocationCountryEmbeddedResource(),
                "resource_country");
        assertEquals(cfg.getIPGeolocationCountryDatabaseFile(), "country.mmdb");

        assertFalse(cfg.isIPGeolocationCityDatabaseEmbedded());
        assertEquals(cfg.getIPGeolocationCityEmbeddedResource(),
                "resource_city");
        assertEquals(cfg.getIPGeolocationCityDatabaseFile(), "city.mmdb");
    }

    @Test(expected = ConfigurationException.class)
    public void testFromPropertiesWhenError() throws ConfigurationException {
        final Properties props = mock(Properties.class);
        when(props.getProperty(anyString(), anyString())).thenThrow(IllegalArgumentException.class);

        final GeolocationConfiguration cfg = new GeolocationConfigurationImpl();
        cfg.fromProperties(props);
    }

    @Test
    public void testToProperties() throws ConfigurationException {
        final Properties props = buildProperties();
        final GeolocationConfiguration cfg = new GeolocationConfigurationImpl(props);

        // convert back to properties
        final Properties props2 = cfg.toProperties();

        assertEquals(props.getProperty(GeolocationConfigurationFactory.
                        IP_GEOLOCATION_LEVEL_PROPERTY),
                props2.getProperty(GeolocationConfigurationFactory.
                        IP_GEOLOCATION_LEVEL_PROPERTY));
        assertEquals(props.getProperty(GeolocationConfigurationFactory.
                CACHING_ENABLED_PROPERTY), props2.getProperty(
                GeolocationConfigurationFactory.CACHING_ENABLED_PROPERTY));

        assertEquals(props.getProperty(GeolocationConfigurationFactory.
                        IP_GEOLOCATION_COUNTRY_DATABASE_EMBEDDED_PROPERTY),
                props2.getProperty(GeolocationConfigurationFactory.
                        IP_GEOLOCATION_COUNTRY_DATABASE_EMBEDDED_PROPERTY));
        assertEquals(props.getProperty(GeolocationConfigurationFactory.
                        IP_GEOLOCATION_COUNTRY_EMBEDDED_RESOURCE_PROPERTY),
                props2.getProperty(GeolocationConfigurationFactory.
                        IP_GEOLOCATION_COUNTRY_EMBEDDED_RESOURCE_PROPERTY));
        assertEquals(props.getProperty(GeolocationConfigurationFactory.
                        IP_GEOLOCATION_COUNTRY_DATABASE_FILE_PROPERTY),
                props2.getProperty(GeolocationConfigurationFactory.
                        IP_GEOLOCATION_COUNTRY_DATABASE_FILE_PROPERTY));

        assertEquals(props.getProperty(GeolocationConfigurationFactory.
                        IP_GEOLOCATION_CITY_DATABASE_EMBEDDED_PROPERTY),
                props2.getProperty(GeolocationConfigurationFactory.
                        IP_GEOLOCATION_CITY_DATABASE_EMBEDDED_PROPERTY));
        assertEquals(props.getProperty(GeolocationConfigurationFactory.
                        IP_GEOLOCATION_CITY_EMBEDDED_RESOURCE_PROPERTY),
                props2.getProperty(GeolocationConfigurationFactory.
                        IP_GEOLOCATION_CITY_EMBEDDED_RESOURCE_PROPERTY));
        assertEquals(props.getProperty(GeolocationConfigurationFactory.
                        IP_GEOLOCATION_CITY_DATABASE_FILE_PROPERTY),
                props2.getProperty(GeolocationConfigurationFactory.
                        IP_GEOLOCATION_CITY_DATABASE_FILE_PROPERTY));
    }

    private Properties buildProperties() {
        final Properties props = new Properties();
        props.setProperty(GeolocationConfigurationFactory.
                        IP_GEOLOCATION_LEVEL_PROPERTY,
                IPGeolocationLevel.COUNTRY.getValue());
        props.setProperty(GeolocationConfigurationFactory.
                CACHING_ENABLED_PROPERTY, "false");

        props.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_COUNTRY_DATABASE_EMBEDDED_PROPERTY, "false");
        props.setProperty(GeolocationConfigurationFactory.
                        IP_GEOLOCATION_COUNTRY_EMBEDDED_RESOURCE_PROPERTY,
                "resource_country");
        props.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_COUNTRY_DATABASE_FILE_PROPERTY, "country.mmdb");

        props.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_CITY_DATABASE_EMBEDDED_PROPERTY, "false");
        props.setProperty(GeolocationConfigurationFactory.
                        IP_GEOLOCATION_CITY_EMBEDDED_RESOURCE_PROPERTY,
                "resource_city");
        props.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_CITY_DATABASE_FILE_PROPERTY, "city.mmdb");

        return props;
    }
}
