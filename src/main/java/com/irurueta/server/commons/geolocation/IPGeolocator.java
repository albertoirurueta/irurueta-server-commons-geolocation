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

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.AbstractCountryResponse;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Continent;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;
import com.maxmind.geoip2.record.Postal;
import com.maxmind.geoip2.record.Subdivision;
import com.maxmind.geoip2.record.Traits;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to locate an IP address.
 */
public class IPGeolocator implements Closeable {

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
     * City database reader.
     */
    private DatabaseReader mCityReader;

    /**
     * Indicates whether city database has already been prepared.
     */
    private boolean mCityDatabasePrepared;

    /**
     * Country database reader.
     */
    private DatabaseReader mCountryReader;

    /**
     * Indicates whether country database has already been prepared.
     */
    private boolean mCountryDatabasePrepared;

    /**
     * Constructor.
     * Creates and configures an IPGeolocator instance.
     */
    private IPGeolocator() {
        mEnabled = false;
        try {
            mConfiguration = GeolocationConfigurationFactory.getInstance().
                    configure();


            // copy embedded databases to destination if needed
            prepareDatabases();

            // configure lookup services
            final IPGeolocationLevel level = mConfiguration.getIPGeolocationLevel();

            if (level != null) {
                if (level == IPGeolocationLevel.CITY) {
                    mCityReader = createCityReader();
                } else if (level == IPGeolocationLevel.COUNTRY) {
                    mCountryReader = createCountryReader();
                }

                if (mCityReader != null || mCountryReader != null) {
                    mEnabled = true;
                }
            }

        } catch (final Exception e) {
            LOGGER.log(Level.WARNING, "IP geolocation configuration not completed", e);
        } finally {
            if (mEnabled) {
                LOGGER.log(Level.INFO, "IP geolocation configured");
            } else {
                LOGGER.log(Level.INFO, "IP geolocation is disabled");
            }
        }
    }

    /**
     * Factory method to return the singleton instance of IPGeolocator based
     * on current configuration.
     *
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
     *
     * @param address IP address to evaluate.
     * @param level   level of accuracy of geolocation.
     * @return location of IP address.
     * @throws IPGeolocationDisabledException if IP geolocation is disabled or
     *                                        was not properly configured.
     * @throws IPLocationNotFoundException    if IP address couldn't be geolocated
     *                                        because it wasn't found in database.
     */
    public synchronized IPLocation locate(
            final InetAddress address,
            final IPGeolocationLevel level) throws IPGeolocationDisabledException,
            IPLocationNotFoundException {
        if (!mEnabled || level == IPGeolocationLevel.DISABLED) {
            throw new IPGeolocationDisabledException();
        }

        try {
            final IPLocation location = new IPLocation(level);
            DatabaseReader reader;

            // city level
            if (level == IPGeolocationLevel.CITY) {
                reader = getOrCreateCityReader();

                // search at city level
                final CityResponse response = reader.city(address);

                // city level
                final City city = response.getCity();
                if (city != null) {
                    location.mCity = city.getName();
                }

                final Location loc = response.getLocation();
                if (loc != null) {
                    location.mTimeZone = loc.getTimeZone() != null ?
                            TimeZone.getTimeZone(loc.getTimeZone()) : null;
                    location.mAccuracyRadius = loc.getAccuracyRadius();
                    location.mMetroCode = loc.getMetroCode();
                    location.mLatitude = loc.getLatitude();
                    location.mLongitude = loc.getLongitude();
                }

                final Postal postal = response.getPostal();
                if (postal != null) {
                    location.mPostalCode = postal.getCode();
                }

                final List<Subdivision> subdivisions = response.getSubdivisions();
                if (subdivisions != null) {
                    location.mSubdivisionCodes = new ArrayList<>();
                    location.mSubdivisionNames = new ArrayList<>();
                    for (final Subdivision s : subdivisions) {
                        if (s.getIsoCode() != null && s.getName() != null) {
                            location.mSubdivisionCodes.add(s.getIsoCode());
                            location.mSubdivisionNames.add(s.getName());
                        }
                    }
                }


                // country level
                processCountryResponse(response, location);
            }

            // country or city level (if nothing has been found yet)
            if (level == IPGeolocationLevel.COUNTRY) {
                reader = getOrCreateCountryReader();
                final AbstractCountryResponse response = reader.country(address);
                processCountryResponse(response, location);
            }

            return location;
        } catch (final GeoIp2Exception | IOException e) {
            throw new IPLocationNotFoundException(e);
        }
    }

    /**
     * Locates provided IP or IPv6 address using requested level.
     * Notice that geolocation levels are: Country, City (which includes country
     * information) and Organization (which includes both City and Country
     * information)
     *
     * @param address IP address or DNS host name to evaluate in string form.
     * @param level   level of accuracy of geolocation.
     * @return location of IP address.
     * @throws UnknownHostException           if provided textual form of IP address is
     *                                        not valid, or if DNS host name couldn't be resolved into an IP address.
     * @throws IPGeolocationDisabledException if IP geolocation is disabled or
     *                                        was not properly configured.
     * @throws IPLocationNotFoundException    if IP address couldn't be geolocated
     *                                        because it wasn't found in database.
     */
    public IPLocation locate(final String address, final IPGeolocationLevel level)
            throws UnknownHostException, IPGeolocationDisabledException,
            IPLocationNotFoundException {
        return locate(InetAddress.getByName(address), level);
    }

    /**
     * Locates provided IP or IPv6 address using default configured geolocation
     * level of accuracy.
     *
     * @param address IP address to evaluate.
     * @return location of IP address.
     * @throws IPGeolocationDisabledException if IP geolocation is disabled or
     *                                        was not properly configured.
     * @throws IPLocationNotFoundException    if IP address couldn't be geolocated
     *                                        because it wasn't found in database.
     */
    public IPLocation locate(final InetAddress address)
            throws IPGeolocationDisabledException, IPLocationNotFoundException {
        return locate(address, mConfiguration.getIPGeolocationLevel());
    }

    /**
     * Locates provided IP or IPv6 address using default configured geolocation
     * level of accuracy.
     *
     * @param address IP address or DNS host name to evaluate in string form
     * @return location of IP address.
     * @throws UnknownHostException           if provided textual form of IP address is
     *                                        not valid, or if DNS host name couldn't be resolved into an IP address.
     * @throws IPGeolocationDisabledException if IP geolocation is disabled or
     *                                        was not properly configured.
     * @throws IPLocationNotFoundException    if IP address couldn't be geolocated
     *                                        because it wasn't found in database.
     */
    public IPLocation locate(final String address) throws UnknownHostException,
            IPGeolocationDisabledException, IPLocationNotFoundException {
        return locate(InetAddress.getByName(address));
    }

    /**
     * Closes location services. Once closed, geolocation will not be available.
     * If databases where embedded, then their destination local files will also
     * be deleted when closing location services.
     * This method should be called at server shutdown or when application is
     * undeployed.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public synchronized void close() throws IOException {
        // close location services
        if (mCityReader != null) {
            try {
                mCityReader.close();
            } catch (final IOException e) {
                LOGGER.log(Level.WARNING, "Could not close city database", e);
            }
        }
        if (mCountryReader != null) {
            try {
                mCountryReader.close();
            } catch (final IOException e) {
                LOGGER.log(Level.WARNING, "Could not close country database", e);
            }
        }

        mEnabled = false;

        // delete database files if they were copied from embedded resources, as
        // this method will usually be called on server shutdown or application
        // undeployment
        boolean failed = false;
        if (mCountryDatabasePrepared) {
            // delete country database file
            final File f = new File(
                    mConfiguration.getIPGeolocationCountryDatabaseFile());
            if (f.exists()) {
                try {
                    Files.delete(f.toPath());
                } catch (final IOException e) {
                    failed = true;
                }
            }
        }

        //city database
        if (mCityDatabasePrepared) {
            //delete city database file
            final File f = new File(
                    mConfiguration.getIPGeolocationCityDatabaseFile());
            if (f.exists()) {
                try {
                    Files.delete(f.toPath());
                } catch (final IOException e) {
                    failed = true;
                }
            }
        }

        mCityReader = mCountryReader = null;

        if (failed) {
            throw new IOException();
        }
    }

    /**
     * Closes geolocation services if a configured IPGeolocator exists, and
     * resets the current singleton instance so a new one can be acquired having
     * a new configuration if required.
     *
     * @throws IOException if an I/O error occurs.
     */
    protected static synchronized void reset() throws IOException {
        final IPGeolocator singleton;
        if (mReference != null && (singleton = mReference.get()) != null) {
            singleton.close();
        }
        mReference = null;
    }

    /**
     * Gets or create city database reader.
     *
     * @return city database reader.
     * @throws IOException if an I/O error occurs.
     */
    private DatabaseReader getOrCreateCityReader() throws IOException {
        if (mCityReader != null) {
            return mCityReader;
        } else {
            mCityReader = createCityReader();
        }

        return mCityReader;
    }

    /**
     * Gets or creates country database reader.
     *
     * @return country database reader.
     * @throws IOException if an I/O error occurs.
     */
    private DatabaseReader getOrCreateCountryReader() throws IOException {
        if (mCountryReader != null) {
            return mCountryReader;
        } else {
            mCountryReader = createCountryReader();
        }

        return mCountryReader;
    }

    /**
     * Creates a city database reader.
     *
     * @return a city database reader.
     * @throws IOException if an I/O error occurs.
     */
    private DatabaseReader createCityReader() throws IOException {
        if (!mCityDatabasePrepared) {
            mCityDatabasePrepared = prepareCityDatabase();
        }
        final File f = new File(mConfiguration.getIPGeolocationCityDatabaseFile());
        return createReader(f);
    }

    /**
     * Creates a country database reader.
     *
     * @return a country database reader.
     * @throws IOException if an I/O error occurs.
     */
    private DatabaseReader createCountryReader() throws IOException {
        if (!mCountryDatabasePrepared) {
            mCountryDatabasePrepared = prepareCountryDatabase();
        }
        final File f = new File(mConfiguration.getIPGeolocationCountryDatabaseFile());
        return createReader(f);
    }

    /**
     * Creates a database reader.
     *
     * @param file file to read database from.
     * @return a database reader.
     * @throws IOException if an I/O error occurs.
     */
    private DatabaseReader createReader(final File file) throws IOException {
        final boolean cachingEnabled = mConfiguration.isCachingEnabled();

        final DatabaseReader.Builder builder =
                new DatabaseReader.Builder(file);
        if (cachingEnabled) {
            builder.withCache(new CHMCache());
        }
        return builder.build();
    }

    /**
     * Processes country level location data.
     *
     * @param response a response being processed.
     * @param location location where result will be stored.
     */
    private void processCountryResponse(final AbstractCountryResponse response,
                                        final IPLocation location) {
        final Continent continent = response.getContinent();
        if (continent != null) {
            location.mContinentCode = continent.getCode();
            location.mContinentName = continent.getName();
        }

        Country country = response.getCountry();
        if (country != null) {
            location.mCountryCode = country.getIsoCode();
            location.mCountryName = country.getName();
        }
        country = response.getRegisteredCountry();
        if (country != null) {
            location.mRegisteredCountryCode = country.getIsoCode();
            location.mRegisteredCountryName = country.getName();
        }

        final Traits traits = response.getTraits();
        if (traits != null) {
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
     *
     * @throws IOException if an I/O error occurs.
     */
    private void prepareDatabases() throws IOException {

        // city database
        final IPGeolocationLevel level = mConfiguration.getIPGeolocationLevel();
        if (level == IPGeolocationLevel.CITY &&
                mConfiguration.isIPGeolocationCityDatabaseEmbedded() &&
                mConfiguration.getIPGeolocationCityEmbeddedResource() != null &&
                mConfiguration.getIPGeolocationCityDatabaseFile() != null) {
            mCityDatabasePrepared = prepareCityDatabase();
        }

        // country database
        if (!mCityDatabasePrepared &&
                mConfiguration.isIPGeolocationCountryDatabaseEmbedded() &&
                mConfiguration.getIPGeolocationCountryEmbeddedResource() != null &&
                mConfiguration.getIPGeolocationCountryDatabaseFile() != null) {
            mCountryDatabasePrepared = prepareCountryDatabase();
        }
    }

    /**
     * Copies embedded city resource into destination file where database will
     * be stored locally.
     *
     * @return true if database was prepared, false otherwise.
     * @throws IOException if an I/O error occurs.
     */
    private boolean prepareCityDatabase() throws IOException {
        if (mConfiguration.isIPGeolocationCityDatabaseEmbedded() &&
                mConfiguration.getIPGeolocationCityEmbeddedResource() != null &&
                mConfiguration.getIPGeolocationCityDatabaseFile() != null) {
            // copy embedded resource to destination file
            copyResource(mConfiguration.getIPGeolocationCityEmbeddedResource(),
                    mConfiguration.getIPGeolocationCityDatabaseFile());
            return true;
        }

        return false;
    }

    /**
     * Copies country city resource into destination file where database will
     * be stored locally.
     *
     * @return true if database was prepared, false otherwise.
     * @throws IOException if an I/O error occurs.
     */
    private boolean prepareCountryDatabase() throws IOException {
        if (mConfiguration.isIPGeolocationCountryDatabaseEmbedded() &&
                mConfiguration.getIPGeolocationCountryEmbeddedResource() != null &&
                mConfiguration.getIPGeolocationCountryDatabaseFile() != null) {
            // copy embedded resource to destination file
            copyResource(mConfiguration.getIPGeolocationCountryEmbeddedResource(),
                    mConfiguration.getIPGeolocationCountryDatabaseFile());
            return true;
        }

        return false;
    }

    /**
     * Copies a given resource embedded in code into provided destination file.
     * If file where data is to be stored does not exist, a new one will be
     * created. If it already exists, it will be overwritten.
     *
     * @param resource resource to read data from.
     * @param file     file to store data.
     * @throws IOException .
     */
    private void copyResource(final String resource, final String file) throws IOException {
        final File f = new File(file);
        final File parent = f.getParentFile();
        // attempt to create parent folders if they don't exist
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException();
        }

        if (!f.exists()) {
            try (final InputStream inStream = IPGeolocator.class.getResourceAsStream(resource)) {
                try (final OutputStream outStream = new FileOutputStream(file)) {
                    LOGGER.log(Level.INFO, "Copying resource: {0}", resource);


                    final byte[] buffer = new byte[BUFFER_SIZE];
                    int n;
                    while ((n = inStream.read(buffer)) > 0) {
                        outStream.write(buffer, 0, n);
                    }
                    LOGGER.log(Level.INFO, "Resource: {0} copied to {1}",
                            new Object[]{resource, f.getAbsolutePath()});
                }
            }
        }
    }
}
