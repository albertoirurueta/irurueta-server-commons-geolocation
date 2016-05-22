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

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.AbstractCountryResponse;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Continent;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;
import com.maxmind.geoip2.record.Postal;
import com.maxmind.geoip2.record.Subdivision;
import com.maxmind.geoip2.record.Traits;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to locate an IP address.
 */
public class IPGeolocator {
    
    /**
     * Logger of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(
            IPGeolocator.class.getName());
    
    /**
     * Buffer size to copy embedded databases into final locations.
     */
    private static final int BUFFER_SIZE = 1024;
    
    /**
     * Singleton instance of IPGeolocator.
     */
    private static SoftReference<IPGeolocator> mReference;
    
    /**
     * Indicates if geolocation is enabled.
     */
    private boolean mEnabled;        
    
    /**
     * Reference to geolocation configuration.
     */
    private GeolocationConfiguration mConfiguration;
    
    /**
     * Database reader.
     */
    private DatabaseReader mReader;
        
    /**
     * Constructor.
     * Creates and configures an IPGeolocator instance.
     */
    private IPGeolocator() {
        mEnabled = false;
        try{
            mConfiguration = GeolocationConfigurationFactory.getInstance().
                    configure();
            
            
            //copy embedded databases to destination if needed
            prepareDatabases();
            
            //configure lookup services
            IPGeolocationLevel level = mConfiguration.getIPGeolocationLevel();
            
            if(level != null){
                boolean cachingEnabled = mConfiguration.isCachingEnabled();
                        
                File file = null;                
                switch(level){
                    case CITY:
                        file = new File(mConfiguration.
                                getIPGeolocationCityDatabaseFile());
                        break;
                    case COUNTRY:
                        file = new File(mConfiguration.
                                getIPGeolocationCountryDatabaseFile());
                        break;
                }
                
                if(file != null){
                    DatabaseReader.Builder builder = 
                            new DatabaseReader.Builder(file);
                    if(cachingEnabled){
                        builder.withCache(new CHMCache());
                    }
                    mReader = builder.build();
                    mEnabled = true;
                }
                
            }
                        
            if(mEnabled){
                LOGGER.log(Level.INFO, "IP geolocation configured");            
            }else{
                LOGGER.log(Level.INFO, "IP geolocation is disabled");                
            }
        }catch(Exception e){
            if(mEnabled){
                LOGGER.log(Level.INFO, "IP geolocation configured", e);
            }else{
                LOGGER.log(Level.INFO, "IP geolocation is disabled", e);                
            }
        }
    }
    
    /**
     * Factory method to return the singleton instance of IPGeolocator based
     * on current configuration.
     * @return singleton instance.
     */
    public static synchronized IPGeolocator getInstance() {
        IPGeolocator singleton;
        if (mReference == null || (singleton = mReference.get()) == null) {
            singleton = new IPGeolocator();
            mReference = new SoftReference<>(singleton);
        }
        return singleton;
    }
    
    /**
     * Locates provided IP or IPv6 address using requested level.
     * Notice that geolocation levels are: Country, City (which includes country
     * information) and Disabled, which throws an exception.
     * @param address IP address to evaluate.
     * @param level level of accuracy of geolocation.
     * @return location of IP address.
     * @throws IPGeolocationDisabledException if IP geolocation is disabled or
     * was not properly configured.
     * @throws IPLocationNotFoundException if IP address couldn't be geolocated
     * because it wasn't found in database.
     */
    public IPLocation locate(InetAddress address, IPGeolocationLevel level) 
            throws IPGeolocationDisabledException, IPLocationNotFoundException {
        if (!mEnabled || level == IPGeolocationLevel.DISABLED) {
            throw new IPGeolocationDisabledException();
        }
        
        try{
            IPLocation location = new IPLocation(level);
            boolean found = false;
                               
            //city level
            if (level == IPGeolocationLevel.CITY && mReader != null) {
                //search at city level
                CityResponse response = mReader.city(address);
                if(response != null){
                    found = true;
                    
                    //city level
                    City city = response.getCity();
                    if (city != null) {
                        location.mCity = city.getName();
                    }
                                        
                    Location loc = response.getLocation();
                    if(loc != null){
                        location.mTimeZone = TimeZone.getTimeZone(
                                loc.getTimeZone());
                        location.mAccuracyRadius = loc.getAccuracyRadius();
                        location.mMetroCode = loc.getMetroCode();
                        location.mLatitude = loc.getLatitude();
                        location.mLongitude = loc.getLongitude();
                    }   
                    
                    Postal postal = response.getPostal();
                    if (postal != null) {
                        location.mPostalCode = postal.getCode();
                    }
                    
                    List<Subdivision> subdivisions = response.getSubdivisions();
                    if(subdivisions != null){
                        location.mSubdivisionCodes = new ArrayList<>();
                        location.mSubdivisionNames = new ArrayList<>();
                        for (Subdivision s : subdivisions) {
                            if(s.getIsoCode() != null && s.getName() != null){
                                location.mSubdivisionCodes.add(s.getIsoCode());
                                location.mSubdivisionNames.add(s.getName());
                            }
                        }
                    }
                    
                    
                    //country level
                    processCountryResponse(response, location);
                }
            }
            
            //country or city level (if nothing has been found yet)
            if((level == IPGeolocationLevel.COUNTRY || 
                    (level == IPGeolocationLevel.CITY) && !found)){
                AbstractCountryResponse response = mReader.country(address);
                found = response != null;
                processCountryResponse(response, location);
            }
            
            if(!found) throw new IPLocationNotFoundException();
        
            return location;
        }catch(Exception e){
            throw new IPLocationNotFoundException(e);
        }
    }
        
    /**
     * Locates provided IP or IPv6 address using requested level.
     * Notice that geolocation levels are: Country, City (which includes country
     * information) and Organization (which includes both City and Country 
     * information)
     * @param address IP address or DNS host name to evaluate in string form.
     * @param level level of accuracy of geolocation.
     * @return location of IP address.
     * @throws UnknownHostException if provided textual form of IP address is
     * not valid, or if DNS host name couldn't be resolved into an IP address.
     * @throws IPGeolocationDisabledException if IP geolocation is disabled or
     * was not properly configured.
     * @throws IPLocationNotFoundException if IP address couldn't be geolocated
     * because it wasn't found in database.
     */
    public IPLocation locate(String address, IPGeolocationLevel level) 
            throws UnknownHostException, IPGeolocationDisabledException, 
            IPLocationNotFoundException {
        return locate(InetAddress.getByName(address), level);
    }
    
    /**
     * Locates provided IP or IPv6 address using default configured geolocation 
     * level of accuracy.
     * @param address IP address to evaluate.
     * @return location of IP address.
     * @throws IPGeolocationDisabledException if IP geolocation is disabled or
     * was not properly configured.
     * @throws IPLocationNotFoundException if IP address couldn't be geolocated
     * because it wasn't found in database.
     */    
    public IPLocation locate(InetAddress address) 
            throws IPGeolocationDisabledException, IPLocationNotFoundException {
        return locate(address, mConfiguration.getIPGeolocationLevel());
    }
    
    /**
     * Locates provided IP or IPv6 address using default configured geolocation 
     * level of accuracy.
     * @param address IP address or DNS host name to evaluate in string form
     * @return location of IP address.
     * @throws UnknownHostException if provided textual form of IP address is
     * not valid, or if DNS host name couldn't be resolved into an IP address.
     * @throws IPGeolocationDisabledException if IP geolocation is disabled or
     * was not properly configured.
     * @throws IPLocationNotFoundException if IP address couldn't be geolocated
     * because it wasn't found in database.
     */    
    public IPLocation locate(String address) throws UnknownHostException, 
            IPGeolocationDisabledException, IPLocationNotFoundException {
        return locate(InetAddress.getByName(address));
    }
    
    /**
     * Closes location services. Once closed, geolocation will not be available.
     * If databases where embedded, then their destination local files will also
     * be deleted when closing location services.
     * This method should be called at server shutdown or when application is
     * undeployed.
     */
    public void close() {
        //close location services
        if (mReader != null) {
            try {
                mReader.close();
            } catch(IOException e) {
                LOGGER.log(Level.WARNING, "Could not close database", e);
            }
        }
                
        mEnabled = false;
        
        //delete database files if they were copied from embedded resources, as
        //this method will usually be called on server shutdown or application
        //undeployment
        IPGeolocationLevel level = mConfiguration.getIPGeolocationLevel();
        if (level == IPGeolocationLevel.COUNTRY &&
                mConfiguration.isIPGeolocationCountryDatabaseEmbedded() &&
                mConfiguration.getIPGeolocationCountryEmbeddedResource() != null &&
                mConfiguration.getIPGeolocationCountryDatabaseFile() != null) {
            //delete country database file
            File f = new File(
                    mConfiguration.getIPGeolocationCountryDatabaseFile());
            if (f.exists()) {
                f.delete();
            }
        }
        
        //city database
        if(level == IPGeolocationLevel.CITY &&
                mConfiguration.isIPGeolocationCityDatabaseEmbedded() &&
                mConfiguration.getIPGeolocationCityEmbeddedResource() != null &&
                mConfiguration.getIPGeolocationCityDatabaseFile() != null){
            //delete city database file
            File f = new File(
                    mConfiguration.getIPGeolocationCityDatabaseFile());
            if (f.exists()) {
                f.delete();
            }            
        }

        mReader = null;
    }
    
    /**
     * Closes geolocation services if a configured IPGeolocator exists, and
     * resets the current singleton instance so a new one can be acquired having
     * a new configuration if required.
     */
    protected static synchronized void reset() {
        IPGeolocator singleton;
        if(mReference != null && (singleton = mReference.get()) != null){
            singleton.close();
        }
        mReference = null;
    }
    
    /**
     * This method is called on garbage collection.
     * When this method is called, all the location services of this instance
     * will be closed, and the files where embedded databases were copied will
     * be deleted.
     * @throws Throwable if anything fails.
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        }catch(Throwable ignore){
        } finally {
            super.finalize();
        }
    }  
    
    /**
     * Processes country level location data.
     * @param response a response being processed.
     * @param location location where result will be stored.
     */
    private void processCountryResponse(AbstractCountryResponse response, 
            IPLocation location){
        if(response == null || location == null) return;
        
        Continent continent = response.getContinent();
        if (continent != null) {
            location.mContinentCode = continent.getCode();
            location.mContinentName = continent.getName();
        }

        Country country = response.getCountry();
        if(country != null){
            location.mCountryCode = country.getIsoCode();
            location.mCountryName = country.getName();
        }
        country = response.getRegisteredCountry();
        if(country != null){
            location.mRegisteredCountryCode = country.getIsoCode();
            location.mRegisteredCountryName = country.getName();
        }

        Traits traits = response.getTraits();
        if(traits != null){
            location.mAutonomousSystemNumber = 
                    traits.getAutonomousSystemNumber();
            location.mDomain = traits.getDomain();
            location.mIsp = traits.getIsp();
            location.mOrganization = traits.getOrganization();
        }        
    }    
    
    /**
     * Copies embedded resources into destination files where databases will be
     * stored locally.
     * @throws IOException if an I/O error occurs.
     */
    private void prepareDatabases() throws IOException {

        boolean cityPrepared = false;
        
        //city database
        IPGeolocationLevel level = mConfiguration.getIPGeolocationLevel();
        if (level == IPGeolocationLevel.CITY &&
                mConfiguration.isIPGeolocationCityDatabaseEmbedded() &&
                mConfiguration.getIPGeolocationCityEmbeddedResource() != null &&
                mConfiguration.getIPGeolocationCityDatabaseFile() != null) {
            //copy embedded resource to destination file
            copyResource(mConfiguration.getIPGeolocationCityEmbeddedResource(),
                    mConfiguration.getIPGeolocationCityDatabaseFile());
            cityPrepared = true;
        }
        
        //country database
        if (!cityPrepared && mConfiguration.isIPGeolocationCountryDatabaseEmbedded() &&
                mConfiguration.getIPGeolocationCountryEmbeddedResource() != null && 
                mConfiguration.getIPGeolocationCountryDatabaseFile() != null) {
            //copy embedded resource to destination file
            copyResource(mConfiguration.getIPGeolocationCountryEmbeddedResource(),
                    mConfiguration.getIPGeolocationCountryDatabaseFile());
        }
    }
    
    /**
     * Copies a given resource embedded in code into provided destination file.
     * If file where data is to be stored does not exist, a new one will be 
     * created. If it already exists, it will be overwritten.
     * @param resource resource to read data from.
     * @param file file to store data.
     * @throws IOException .
     */
    private void copyResource(String resource, String file) throws IOException {
        InputStream inStream = null;
        OutputStream outStream = null;
        try{
            inStream = IPGeolocator.class.getResourceAsStream(
                    resource);
            File f = new File(file);
            File parent = f.getParentFile();
            if(parent != null){
                //attempt to create parent folders if they don't exist
                if(!parent.exists()) parent.mkdirs();
            }
            outStream = new FileOutputStream(file);
            byte[] buffer = new byte[BUFFER_SIZE];
            int n;
            while((n = inStream.read(buffer)) > 0){
                outStream.write(buffer, 0, n);
            }
        }finally{
            if(inStream != null) inStream.close();
            if(outStream != null) outStream.close();
        }
    }
}
