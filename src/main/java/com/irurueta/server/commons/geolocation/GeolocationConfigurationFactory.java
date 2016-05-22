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

import com.irurueta.server.commons.configuration.BaseConfigurationFactory;
import com.irurueta.server.commons.configuration.ConfigurationException;
import java.util.Properties;

/**
 * Class in charge of loading and configuring geolocation services.
 */
public class GeolocationConfigurationFactory extends 
        BaseConfigurationFactory<GeolocationConfiguration>{
    
    /**
     * Property indicating which geolocation level must be used if none is 
     * provided. When looking at City level Country information is also
     * included.
     */
    public static final String IP_GEOLOCATION_LEVEL_PROPERTY =
            "com.irurueta.server.commons.geolocation.IP_GEOLOCATION_LEVEL";
    
    /**
     * Default IP geolocation level.
     */
    public static final IPGeolocationLevel DEFAULT_IP_GEOLOCATION_LEVEL =
            IPGeolocationLevel.CITY;
    
    /**
     * Indicates whether geolocation database must be cached in memory or not.
     * When caching is enabled, performance increases significantly at the 
     * expense of an overhead in memory of approximately 2MB.
     */
    public static final String CACHING_ENABLED_PROPERTY = 
            "com.irurueta.server.commons.geolocation.CACHING_ENABLED";
    
    /**
     * Default value for geolocation caching.
     */
    public static final boolean DEFAULT_CACHING_ENABLED = true;
                
    /**
     * Property indicating if country IP geolocation database is embedded.
     * By default the embedded free database in the code will be used.
     */
    public static final String IP_GEOLOCATION_COUNTRY_DATABASE_EMBEDDED_PROPERTY = 
            "com.irurueta.server.commons.geolocation.IP_GEOLOCATION_COUNTRY_DATABASE_EMBEDDED";
    
    /**
     * By default the embedded free database in the code will be used for 
     * country IP geolocation.
     */
    public static final boolean DEFAULT_IP_GEOLOCATION_COUNTRY_DATABASE_EMBEDDED = 
            true;
    
    /**
     * Property indicating resource where IP country geolocation database is 
     * embedded.
     * This value should rarely be modified. Only if other embedded databases 
     * are required in the code.
     */
    public static final String IP_GEOLOCATION_COUNTRY_EMBEDDED_RESOURCE_PROPERTY = 
            "com.irurueta.server.commons.geolocation.IP_GEOLOCATION_COUNTRY_EMBEDDED_RESOURCE";
    
    /**
     * Default resource where IP country geolocation database is located.
     */
    public static final String DEFAULT_IP_GEOLOCATION_COUNTRY_EMBEDDED_RESOURCE = 
            "GeoLite2-Country.mmdb";    
        
    /**
     * Property indicating where the IP country geolocation database file can
     * be found, or if such database is embedded, then the destination file 
     * where if will be copied while the IPGeolocator is running.
     */
    public static final String IP_GEOLOCATION_COUNTRY_DATABASE_FILE_PROPERTY =
            "com.irurueta.server.commons.geolocation.IP_GEOLOCATION_COUNTRY_DATABASE_FILE";
    
    /**
     * Default file location where IP country geolocation database can be found
     * or where it will be copied if it is embedded in the code.
     */
    public static final String DEFAULT_IP_GEOLOCATION_COUNTRY_DATABASE_FILE =
            "./GeoLite2-Country.mmdb";
    
    /**
     * Property indicating if city IP geolocation database is embedded.
     * By default the embedded free database in the code will be used.
     */        
    public static final String IP_GEOLOCATION_CITY_DATABASE_EMBEDDED_PROPERTY = 
            "com.irurueta.server.commons.geolocation.IP_GEOLOCATION_CITY_DATABASE_EMBEDDED";
    
    /**
     * By default the embedded free database in the code will be used for 
     * city IP geolocation.
     */        
    public static final boolean DEFAULT_IP_GEOLOCATION_CITY_DATABASE_EMBEDDED = 
            true;
    
    /**
     * Property indicating resource where IP city geolocation database is 
     * embedded.
     * This value should rarely be modified. Only if other embedded databases 
     * are required in the code.
     */        
    public static final String IP_GEOLOCATION_CITY_EMBEDDED_RESOURCE_PROPERTY = 
            "com.irurueta.server.commons.geolocation.IP_GEOLOCATION_CITY_EMBEDDED_RESOURCE";

    /**
     * Default resource where IP city geolocation database is located.
     */        
    public static final String DEFAULT_IP_GEOLOCATION_CITY_EMBEDDED_RESOURCE = 
            "GeoLite2-City.mmdb";

    /**
     * Property indicating where the IP city geolocation database file can
     * be found, or if such database is embedded, then the destination file 
     * where if will be copied while the IPGeolocator is running.
     */        
    public static final String IP_GEOLOCATION_CITY_DATABASE_FILE_PROPERTY =
            "com.irurueta.server.commons.geolocation.IP_GEOLOCATION_CITY_DATABASE_FILE";
    
    /**
     * Default file location where IP city geolocation database can be 
     * found or where it will be copied if it is embedded in the code.
     */        
    public static final String DEFAULT_IP_GEOLOCATION_CITY_DATABASE_FILE =
            "./GeoLite2-City.mmdb";
    
    /**
     * Reference to factory singleton.
     */
    private static GeolocationConfigurationFactory mSingleton = null;
    
    /**
     * Constructor.
     */
    private GeolocationConfigurationFactory(){}
    
    /**
     * Factory method to create or return singleton instance.
     * @return factory singleton.
     */
    public synchronized static GeolocationConfigurationFactory getInstance(){
        if(mSingleton == null) mSingleton = new GeolocationConfigurationFactory();
        return mSingleton;
    }
    
    /**
     * Configures the geolocation layer using provided properties.
     * @param properties properties containing geolocation configuration
     * @return geolocation configuration.
     * @throws ConfigurationException if any property value was invalid.
     */    
    @Override
    public GeolocationConfiguration configure(Properties properties) 
            throws ConfigurationException {
        if(mConfiguration != null) return mConfiguration;
        
        if(properties == null){
            //use default configuration
            mConfiguration = new GeolocationConfigurationImpl();
        }else{
            mConfiguration = new GeolocationConfigurationImpl(properties);
        }
        
        //configure IPGeolocator
        IPGeolocator.getInstance();
        
        return mConfiguration;
    }
    
    /**
     * Resets configuration by removing any existing configuration if already
     * defined.
     * @return returns this instance so that this method can be chained .
     * @throws ConfigurationException if something fails during configuration
     * reset.
     */
    @Override
    public synchronized 
            BaseConfigurationFactory<GeolocationConfiguration> reset() 
            throws ConfigurationException{
        IPGeolocator.reset();
        mConfiguration = null;
        return super.reset();
    }    
}
