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

import com.irurueta.server.commons.configuration.ConfigurationException;
import java.util.Properties;

/**
 * Class containing configuration for geolocation services.
 */
public class GeolocationConfigurationImpl implements GeolocationConfiguration{

    /**
     * Default geolocation level to use when none is specified.
     */    
    private IPGeolocationLevel mIpGeolocationLevel;
    
    /**
     * Indicates whether database must be cached in memory or not.
     * When caching is enabled, performance increases significantly at the
     * expense of an overhea din memory usage of approximately 2MB.
     */
    private boolean mCachingEnabled;
    
    /**
     * Indicates if IP geolocation country database is embedded within 
     * application code. If true, then the configured embedded resource will be
     * copied into provided database file (erasing any previous data), otherwise
     * provided external database file will simply be used for geolocation.
     * By default the Maxmind geolite database will be used as an embedded 
     * resource.
     */    
    private boolean mIpGeolocationCountryDatabaseEmbedded;
    
    /**
     * If provided, the embedded resource will be used to obtain the database
     * and copy it into the file system on provided location (and erasing any
     * previous data).
     * If not provided, but an embedded database is required, then the default
     * embedded resource will be used instead.
     * By default no embedded resource will be specified, and since an embedded
     * database will be used, it will be use the maxmind geolite database 
     * embeddded in the code.
     */    
    private String mIpGeolocationCountryEmbeddedResource;    
    
    /**
     * File where country IP geolocation database is stored locally.
     * If no embedded database is used, then this file will be used to read the
     * database, if an embedded database is used, then the embedded resource
     * will be copied into this location and the database will be read from
     * there.
     * Notice that Country IP geolocation will only be enabled if a location is
     * available.
     */    
    private String mIpGeolocationCountryDatabaseFile;    
    
    /**
     * Indicates license key to use for country IP geolocation database.
     * This parameter is optional and only needed for the non-free Maxmind 
     * databases.
     */    
    private String mMaxmindIPGeolocationCountryDatabaseLicenseKey;
            
    /**
     * Indicates if IP geolocation city database is embedded within 
     * application code. If true, then the configured embedded resource will be
     * copied into provided database file (erasing any previous data), otherwise
     * provided external database file will simply be used for geolocation.
     * By default the Maxmind geolite database will be used as an embedded 
     * resource.
     */    
    private boolean mIpGeolocationCityDatabaseEmbedded;    
    
    /**
     * If provided, the embedded resource will be used to obtain the database
     * and copy it into the file system on provided location (and erasing any
     * previous data).
     * If not provided, but an embedded database is required, then the default
     * embedded resource will be used instead.
     * By default no embedded resource will be specified, and since an embedded
     * database will be used, it will be use the maxmind geolite database 
     * embeddded in the code.
     */    
    private String mIpGeolocationCityEmbeddedResource;    
    
    /**
     * File where city IP geolocation database is stored locally.
     * If no embedded database is used, then this file will be used to read the
     * database, if an embedded database is used, then the embedded resource
     * will be copied into this location and the database will be read from
     * there.
     * Notice that City IP geolocation will only be enabled if a location is
     * available.
     */    
    private String mIpGeolocationCityDatabaseFile;
    
    /**
     * Indicates license key to use for city IP geolocation database.
     * This parameter is optional and only needed for the non-free Maxmind 
     * databases.
     */    
    private String mMaxmindIPGeolocationCityDatabaseLicenseKey;
    
    /**
     * Constructor.
     */
    public GeolocationConfigurationImpl() {
        mIpGeolocationLevel = GeolocationConfigurationFactory.
                DEFAULT_IP_GEOLOCATION_LEVEL;
        mCachingEnabled = GeolocationConfigurationFactory.
                DEFAULT_CACHING_ENABLED;
        mIpGeolocationCountryDatabaseEmbedded = GeolocationConfigurationFactory.
                DEFAULT_IP_GEOLOCATION_COUNTRY_DATABASE_EMBEDDED;
        mIpGeolocationCountryEmbeddedResource = GeolocationConfigurationFactory.
                DEFAULT_IP_GEOLOCATION_COUNTRY_EMBEDDED_RESOURCE;
        mIpGeolocationCountryDatabaseFile = GeolocationConfigurationFactory.
                DEFAULT_IP_GEOLOCATION_COUNTRY_DATABASE_FILE;
        mMaxmindIPGeolocationCountryDatabaseLicenseKey = null;
        
        mIpGeolocationCityDatabaseEmbedded = GeolocationConfigurationFactory.
                DEFAULT_IP_GEOLOCATION_CITY_DATABASE_EMBEDDED;
        mIpGeolocationCityEmbeddedResource = GeolocationConfigurationFactory.
                DEFAULT_IP_GEOLOCATION_CITY_EMBEDDED_RESOURCE;
        mIpGeolocationCityDatabaseFile = GeolocationConfigurationFactory.
                DEFAULT_IP_GEOLOCATION_CITY_DATABASE_FILE;
        mMaxmindIPGeolocationCityDatabaseLicenseKey = null;        
    }
    
    /**
     * Constructor from properties.
     * @param properties properties containing configuration.
     * @throws ConfigurationException if any property value is invalid.
     */
    public GeolocationConfigurationImpl(Properties properties) 
            throws ConfigurationException {
        fromProperties(properties);
    }    
    
    /**
     * Default geolocation level to use when none is specified.
     * @return geolocation level to use when none is specified.
     */    
    @Override
    public IPGeolocationLevel getIPGeolocationLevel() {
        return mIpGeolocationLevel;
    }

    /**
     * Indicates whether database must be cached in memory or not.
     * When caching is enabled, performance increases significantly at the
     * expense of an overhead in memory of approximately 2MB.
     * @return true if database must be cached in memory, false otherwise.
     */
    @Override
    public boolean isCachingEnabled() {
        return mCachingEnabled;
    }    
    
    /**
     * Indicates if IP geolocation country database is embedded within 
     * application code. If true, then the configured embedded resource will be
     * copied into provided database file (erasing any previous data), otherwise
     * provided external database file will simply be used for geolocation.
     * By default the Maxmind geolite database will be used as an embedded 
     * resource.
     * @return true if IP geolocation country database is embedded and copied
     * into provided database file, false if database is simply read from
     * provided file.
     */    
    @Override
    public boolean isIPGeolocationCountryDatabaseEmbedded() {
        return mIpGeolocationCountryDatabaseEmbedded;
    }

    /**
     * If provided, the embedded resource will be used to obtain the database
     * and copy it into the file system on provided location (and erasing any
     * previous data).
     * If not provided, but an embedded database is required, then the default
     * embedded resource will be used instead.
     * By default no embedded resource will be specified, and since an embedded
     * database will be used, it will be use the maxmind geolite database 
     * embeddded in the code.
     * @return embedded resource containing IP geolocation country database.
     */    
    @Override
    public String getIPGeolocationCountryEmbeddedResource() {
        return mIpGeolocationCountryEmbeddedResource;
    }

    /**
     * File where country IP geolocation database is stored locally.
     * If no embedded database is used, then this file will be used to read the
     * database, if an embedded database is used, then the embedded resource
     * will be copied into this location and the database will be read from
     * there.
     * Notice that Country IP geolocation will only be enabled if a location is
     * available.
     * @return location where country IP geolocation database will be stored
     * locally.
     */    
    @Override
    public String getIPGeolocationCountryDatabaseFile() {
        return mIpGeolocationCountryDatabaseFile;
    }

    /**
     * Indicates license key to use for country IP geolocation database.
     * This parameter is optional and only needed for the non-free Maxmind 
     * databases.
     * @return license key to use for country IP geolocation.
     */    
    @Override
    public String getMaxmindIPGeolocationCountryDatabaseLicenseKey() {
        return mMaxmindIPGeolocationCountryDatabaseLicenseKey;
    }

    /**
     * Indicates if IP geolocation city database is embedded within 
     * application code. If true, then the configured embedded resource will be
     * copied into provided database file (erasing any previous data), otherwise
     * provided external database file will simply be used for geolocation.
     * By default the Maxmind geolite database will be used as an embedded 
     * resource.
     * @return true if IP geolocation city database is embedded and copied
     * into provided database file, false if database is simply read from
     * provided file.
     */    
    @Override
    public boolean isIPGeolocationCityDatabaseEmbedded() {
        return mIpGeolocationCityDatabaseEmbedded;
    }

    /**
     * If provided, the embedded resource will be used to obtain the database
     * and copy it into the file system on provided location (and erasing any
     * previous data).
     * If not provided, but an embedded database is required, then the default
     * embedded resource will be used instead.
     * By default no embedded resource will be specified, and since an embedded
     * database will be used, it will be use the maxmind geolite database 
     * embeddded in the code.
     * @return embedded resource containing IP geolocation city database.
     */    
    @Override
    public String getIPGeolocationCityEmbeddedResource() {
        return mIpGeolocationCityEmbeddedResource;
    }

    /**
     * File where city IP geolocation database is stored locally.
     * If no embedded database is used, then this file will be used to read the
     * database, if an embedded database is used, then the embedded resource
     * will be copied into this location and the database will be read from
     * there.
     * Notice that City IP geolocation will only be enabled if a location is
     * available.
     * @return location where city IP geolocation database will be stored
     * locally.
     */    
    @Override
    public String getIPGeolocationCityDatabaseFile() {
        return mIpGeolocationCityDatabaseFile;
    }

    /**
     * Indicates license key to use for city IP geolocation database.
     * This parameter is optional and only needed for the non-free Maxmind 
     * databases.
     * @return license key to use for city IP geolocation.
     */    
    @Override
    public String getMaxmindIPGeolocationCityDatabaseLicenseKey() {
        return mMaxmindIPGeolocationCityDatabaseLicenseKey;
    }

    /**
     * Loads configuration from provided properties.
     * @param properties properties containing configuration.
     * @throws ConfigurationException if any properties value is invalid.
     */    
    @Override
    public final void fromProperties(Properties properties) 
            throws ConfigurationException {
        try{
            mIpGeolocationLevel = IPGeolocationLevel.fromValue(
                    properties.getProperty(GeolocationConfigurationFactory.
                    IP_GEOLOCATION_LEVEL_PROPERTY, 
                    GeolocationConfigurationFactory.
                    DEFAULT_IP_GEOLOCATION_LEVEL.toString()));
            mCachingEnabled = Boolean.valueOf(properties.getProperty(
                    GeolocationConfigurationFactory.CACHING_ENABLED_PROPERTY, 
                    Boolean.toString(GeolocationConfigurationFactory.
                    DEFAULT_CACHING_ENABLED)));

            mIpGeolocationCountryDatabaseEmbedded = Boolean.valueOf(properties.
                    getProperty(GeolocationConfigurationFactory.
                    IP_GEOLOCATION_COUNTRY_DATABASE_EMBEDDED_PROPERTY, 
                    Boolean.toString(GeolocationConfigurationFactory.
                    DEFAULT_IP_GEOLOCATION_COUNTRY_DATABASE_EMBEDDED)));
            mIpGeolocationCountryEmbeddedResource = properties.getProperty(
                    GeolocationConfigurationFactory.
                    IP_GEOLOCATION_COUNTRY_EMBEDDED_RESOURCE_PROPERTY, 
                    GeolocationConfigurationFactory.
                    DEFAULT_IP_GEOLOCATION_COUNTRY_EMBEDDED_RESOURCE);
            mIpGeolocationCountryDatabaseFile = properties.getProperty(
                    GeolocationConfigurationFactory.
                    IP_GEOLOCATION_COUNTRY_DATABASE_FILE_PROPERTY, 
                    GeolocationConfigurationFactory.
                    DEFAULT_IP_GEOLOCATION_COUNTRY_DATABASE_FILE);
            mMaxmindIPGeolocationCountryDatabaseLicenseKey = properties.
                    getProperty(GeolocationConfigurationFactory.
                    MAXMIND_IP_GEOLOCATION_COUNTRY_DATABASE_LICENSE_KEY_PROPERTY);
        
            mIpGeolocationCityDatabaseEmbedded = Boolean.valueOf(properties.
                    getProperty(GeolocationConfigurationFactory.
                    IP_GEOLOCATION_CITY_DATABASE_EMBEDDED_PROPERTY, 
                    Boolean.toString(
                    GeolocationConfigurationFactory.
                    DEFAULT_IP_GEOLOCATION_CITY_DATABASE_EMBEDDED)));
            mIpGeolocationCityEmbeddedResource = properties.getProperty(
                    GeolocationConfigurationFactory.
                    IP_GEOLOCATION_CITY_EMBEDDED_RESOURCE_PROPERTY, 
                    GeolocationConfigurationFactory.
                    DEFAULT_IP_GEOLOCATION_CITY_EMBEDDED_RESOURCE);
            mIpGeolocationCityDatabaseFile = properties.getProperty(
                    GeolocationConfigurationFactory.
                    IP_GEOLOCATION_CITY_DATABASE_FILE_PROPERTY, 
                    GeolocationConfigurationFactory.
                    DEFAULT_IP_GEOLOCATION_CITY_DATABASE_FILE);
            mMaxmindIPGeolocationCityDatabaseLicenseKey = 
                    properties.getProperty(GeolocationConfigurationFactory.
                    MAXMIND_IP_GEOLOCATION_CITY_DATABASE_LICENSE_KEY_PROPERTY);        
        }catch(Throwable t){
            throw new ConfigurationException(t);
        }
    }

    /**
     * Converts current configuration into properties.
     * @return properties containing configuration.
     */    
    @Override
    public Properties toProperties() {
        Properties properties = new Properties();
        if(mIpGeolocationLevel != null){
            properties.setProperty(GeolocationConfigurationFactory.
                    IP_GEOLOCATION_LEVEL_PROPERTY, mIpGeolocationLevel.
                    getValue());
        }
        properties.setProperty(
                GeolocationConfigurationFactory.CACHING_ENABLED_PROPERTY, 
                Boolean.toString(mCachingEnabled));
        properties.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_COUNTRY_DATABASE_EMBEDDED_PROPERTY, 
                Boolean.toString(mIpGeolocationCountryDatabaseEmbedded));
        if(mIpGeolocationCountryEmbeddedResource != null){
            properties.setProperty(GeolocationConfigurationFactory.
                    IP_GEOLOCATION_COUNTRY_EMBEDDED_RESOURCE_PROPERTY, 
                    mIpGeolocationCountryEmbeddedResource);
        }
        if(mIpGeolocationCountryDatabaseFile != null){
            properties.setProperty(GeolocationConfigurationFactory.
                    IP_GEOLOCATION_COUNTRY_DATABASE_FILE_PROPERTY, 
                    mIpGeolocationCountryDatabaseFile);
        }
        if(mMaxmindIPGeolocationCountryDatabaseLicenseKey != null){
            properties.setProperty(GeolocationConfigurationFactory.
                    MAXMIND_IP_GEOLOCATION_COUNTRY_DATABASE_LICENSE_KEY_PROPERTY, 
                    mMaxmindIPGeolocationCountryDatabaseLicenseKey);
        }
                
        properties.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_CITY_DATABASE_EMBEDDED_PROPERTY, 
                Boolean.toString(mIpGeolocationCityDatabaseEmbedded));
        properties.setProperty(GeolocationConfigurationFactory.
                IP_GEOLOCATION_CITY_EMBEDDED_RESOURCE_PROPERTY, 
                mIpGeolocationCityEmbeddedResource);
        if(mIpGeolocationCityDatabaseFile != null){
            properties.setProperty(GeolocationConfigurationFactory.
                    IP_GEOLOCATION_CITY_DATABASE_FILE_PROPERTY, 
                    mIpGeolocationCityDatabaseFile);
        }
        if(mMaxmindIPGeolocationCityDatabaseLicenseKey != null){
            properties.setProperty(GeolocationConfigurationFactory.
                    MAXMIND_IP_GEOLOCATION_CITY_DATABASE_LICENSE_KEY_PROPERTY, 
                    mMaxmindIPGeolocationCityDatabaseLicenseKey);
        }
        
        return properties;
    }
    
}
