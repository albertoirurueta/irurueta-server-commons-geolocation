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

import com.irurueta.server.commons.configuration.Configuration;

/**
 * Interface defining parameters to configure geolocation and geocoding.
 */
public interface GeolocationConfiguration extends Configuration {

    /**
     * Default geolocation level to use when none is specified.
     *
     * @return geolocation level to use when none is specified.
     */
    IPGeolocationLevel getIPGeolocationLevel();

    /**
     * Indicates whether database must be cached in memory or not.
     * When caching is enabled, performance increases significantly at the
     * expense of an overhead in memory of approximately 2MB.
     *
     * @return true if database must be cached in memory, false otherwise.
     */
    boolean isCachingEnabled();


    /**
     * Indicates if IP geolocation country database is embedded within
     * application code. If true, then the configured embedded resource will be
     * copied into provided database file (erasing any previous data), otherwise
     * provided external database file will simply be used for geolocation.
     * By default the Maxmind geolite database will be used as an embedded
     * resource.
     *
     * @return true if IP geolocation country database is embedded and copied
     * into provided database file, false if database is simply read from
     * provided file.
     */
    boolean isIPGeolocationCountryDatabaseEmbedded();

    /**
     * If provided, the embedded resource will be used to obtain the database
     * and copy it into the file system on provided location (and erasing any
     * previous data).
     * If not provided, but an embedded database is required, then the default
     * embedded resource will be used instead.
     * By default no embedded resource will be specified, and since an embedded
     * database will be used, it will be use the maxmind geolite database
     * embeddded in the code.
     *
     * @return embedded resource containing IP geolocation country database.
     */
    String getIPGeolocationCountryEmbeddedResource();

    /**
     * File where country IP geolocation database is stored locally.
     * If no embedded database is used, then this file will be used to read the
     * database, if an embedded database is used, then the embedded resource
     * will be copied into this location and the database will be read from
     * there.
     * Notice that Country IP geolocation will only be enabled if a location is
     * available.
     *
     * @return location where country IP geolocation database will be stored
     * locally.
     */
    String getIPGeolocationCountryDatabaseFile();

    /**
     * Indicates if IP geolocation city database is embedded within
     * application code. If true, then the configured embedded resource will be
     * copied into provided database file (erasing any previous data), otherwise
     * provided external database file will simply be used for geolocation.
     * By default the Maxmind geolite database will be used as an embedded
     * resource.
     *
     * @return true if IP geolocation city database is embedded and copied
     * into provided database file, false if database is simply read from
     * provided file.
     */
    boolean isIPGeolocationCityDatabaseEmbedded();

    /**
     * If provided, the embedded resource will be used to obtain the database
     * and copy it into the file system on provided location (and erasing any
     * previous data).
     * If not provided, but an embedded database is required, then the default
     * embedded resource will be used instead.
     * By default no embedded resource will be specified, and since an embedded
     * database will be used, it will be use the maxmind geolite database
     * embeddded in the code.
     *
     * @return embedded resource containing IP geolocation city database.
     */
    String getIPGeolocationCityEmbeddedResource();

    /**
     * File where city IP geolocation database is stored locally.
     * If no embedded database is used, then this file will be used to read the
     * database, if an embedded database is used, then the embedded resource
     * will be copied into this location and the database will be read from
     * there.
     * Notice that City IP geolocation will only be enabled if a location is
     * available.
     *
     * @return location where city IP geolocation database will be stored
     * locally.
     */
    String getIPGeolocationCityDatabaseFile();
}
