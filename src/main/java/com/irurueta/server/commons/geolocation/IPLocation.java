/*
 * Copyright (C) 2016 Alberto Irurueta Carro (alberto@irurueta.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.irurueta.server.commons.geolocation;

import com.irurueta.navigation.utils.LocationUtils;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Class containing location information obtained for a given IP address
 */
public class IPLocation {

    /**
     * If available, city name where IP address is located.
     */
    protected String mCity;

    /**
     * If available, time zone assigned to location where this IP address is 
     * located as specified by the IANA Time Zone database.
     * @see <a href="http://www.iana.org/time-zones">http://www.iana.org/time-zones</a>
     */
    protected TimeZone mTimeZone;

    /**
     * The radius in kilometers around the specified location where the IP
     * address is likely to be.
     */
    protected Integer mAccuracyRadius;

    /**
     * Metro code of the location if the location is in the US as specified
     * by the Google Adwords API.
     * @see <a href="https://developers.google.com/adwords/api/docs/appendix/cities-DMAregions">
     * https://developers.google.com/adwords/api/docs/appendix/cities-DMAregions</a>
     */
    protected Integer mMetroCode;

    /**
     * The approximate latitude of the location associated with the IP address. 
     * This value is not precise and should not be used to identify a particular 
     * address or household.
     */
    protected Double mLatitude;

    /**
     * The approximate longitude of the location associated with the IP address.
     * This value is not precise and should not be used to identify a particular
     * address or household.
     */
    protected Double mLongitude;

    /**
     * The postal code of the location. Postal codes are not available for all
     * countries. In some countries, this will only contain part of the postal 
     * code.
     */
    protected String mPostalCode;

    /**
     * List of subdivisions where location is likely to be.
     * A subdivision is a tring of up to three characters long defining a
     * subdivision (i.e. district, province, autonomous community, region, etc).
     * Order of subdivision codes is the same as subdivisions' names.
     * @see <a href="http://en.wikipedia.org/wiki/ISO_3166-2">
     * http://en.wikipedia.org/wiki/ISO_3166-2</a>
     */
    protected List<String> mSubdivisionCodes;

    /**
     * List of subdivisions' names where location is likely to be.
     * Order of subdivision names is the same as subdivisions' codes.
     */
    protected List<String> mSubdivisionNames;

    /**
     * The two-character ISO 3166-1 alpha code for the country where the IP 
     * address is likely to be.
     * @see <a href="http://en.wikipedia.org/wiki/ISO_3166-1">
     * http://en.wikipedia.org/wiki/ISO_3166-1</a>
     */
    protected String mCountryCode;

    /**
     * Name of country where the IP address is likely to be.
     */
    protected String mCountryName;

    /**
     * Two-character ISO 3166-1 alpha code for the country where the ISP has 
     * registered a given IP block. This might differ the actual user's country.
     */
    protected String mRegisteredCountryCode;

    /**
     * Name of country where the ISP has registered a given IP block. This might
     * differ the actual user's country.
     */
    protected String mRegisteredCountryName;

    /**
     * Autonomous system number associated with the IP address.
     * @see <a href="http://en.wikipedia.org/wiki/Autonomous_system_(Internet)">
     * http://en.wikipedia.org/wiki/Autonomous_system_(Internet)</a>
     */
    protected Integer mAutonomousSystemNumber;

    /**
     * The second level domain associated with the IP address. This will be
     * something like "example.com" or "example.co.uk", not "foo.example.com"
     */
    protected String mDomain;

    /**
     * The name of the ISP associated with the IP address.
     */
    protected String mIsp;

    /**
     * The name of the organization associated with the IP address.
     */
    protected String mOrganization;

    /**
     * A two character continent code like "NA" (North America) or "OC" 
     * (Oceania).
     */
    protected String mContinentCode;

    /**
     * Name of continent where request originates from.
     */
    protected String mContinentName;

    /**
     * Assigned geolocation level to this instance.
     * This is the value assigned when requesting location, it can be either
     * Organization, City or Country level
     */
    protected IPGeolocationLevel mLevel;

    /**
     * Constructor with required geolocation level
     * @param level accuracy or level of geolocation. Possible values are:
     * country, city (includes country data) and organization (includes both
     * city and country data)
     */
    public IPLocation(IPGeolocationLevel level) {
        mLevel = level;
    }

    /**
     * Gets city name where IP address is located.
     * @return city name where IP address is located.
     */
    public String getCity() {
        return mCity;
    }

    /**
     * Gets the time zone assigned to location where this IP address is located
     * as specified by the IANA Time Zone database.
     * @see <a href="http://www.iana.org/time-zones">
     * http://www.iana.org/time-zones</a>
     * @return time zone where this IP address is located.
     */
    public TimeZone getTimeZone() {
        return mTimeZone;
    }

    /**
     * Gets the radius in kilometers around the specified location where the IP
     * address is likely to be.
     * @return radius in kilometers around the location where the IP address is
     * likely to be.
     */
    public Integer getAccuracyRadius() {
        return mAccuracyRadius;
    }

    /**
     * Gets the metro code of the location if the location is in the US as 
     * specified by the Google Adwords API.
     * @see <a href="https://developers.google.com/adwords/api/docs/appendix/cities-DMAregions">
     * https://developers.google.com/adwords/api/docs/appendix/cities-DMAregions</a>
     * @return metro code of the location if the location is in the US.
     */
    public Integer getMetroCode() {
        return mMetroCode;
    }

    /**
     * Gets the approximate latitude of the location associated with the IP 
     * address.
     * This value is not precise and should not be used to identify a particular
     * address or household.
     * @return approximate latitude of the location associated with the IP
     * address.
     */
    public Double getLatitude() {
        return mLatitude;
    }

    /**
     * Gets the approximate longitude of the location associated with the IP 
     * address.
     * This value is not precise and should not be used to identify a particular
     * address or household.
     * @return approximate longitude of the location associated with the IP
     * address.
     */
    public Double getLongitude() {
        return mLongitude;
    }

    /**
     * Indicates if GPS coordinates are available for a given IP address.
     * @return true if GPS coordinates are available, false otherwise
     */
    public boolean areCoordinatesAvailable() {
        return mLatitude != null && mLongitude != null;
    }

    /**
     * Gets the postal code of the location. Postal codes are not available for 
     * all countries. In some countries, this will only contain part of the 
     * postal code.
     * @return postal code of the location.
     */
    public String getPostalCode() {
        return mPostalCode;
    }

    /**
     * Gets list of subdivisions where location is likely to be.
     * A subdivision is a tring of up to three characters long defining a
     * subdivision (i.e. district, province, autonomous community, region, etc).
     * Order of subdivision codes is the same as subdivisions' names.
     * @see <a href="http://en.wikipedia.org/wiki/ISO_3166-2">
     * http://en.wikipedia.org/wiki/ISO_3166-2</a>
     * @return list of subdivisions where location is likely to be.
     */
    public List<String> getSubdivisionCodes() {
        return mSubdivisionCodes;
    }

    /**
     * Gets list of subdivisions' names where location is likely to be.
     * Order of subdivision names is the same as subdivisions' codes.
     * @return list of subdivisions' names where location is likely to be.
     */
    public List<String> getSubdivisionNames() {
        return mSubdivisionNames;
    }

    /**
     * Gets the two-character ISO 3166-1 alpha code for the country where the IP 
     * address is likely to be.
     * @see <a href="http://en.wikipedia.org/wiki/ISO_3166-1">
     * http://en.wikipedia.org/wiki/ISO_3166-1</a>
     * @return ISO country code.
     */
    public String getCountryCode() {
        return mCountryCode;
    }

    /**
     * If available, returns country name where IP address is located using 
     * default system locale. If no value is found, the fallback non-localized 
     * name obtained from database will be returned
     * @return country name where IP address is located, or null if not 
     * available
     */
    public String getCountryName() {
        return getCountryName(null);
    }

    /**
     * If available, returns country name where IP address is located using 
     * provided locale.
     * If no value is found, the fallback non-localized name obtained from 
     * database will be returned
     * @param locale language to return localized country name
     * @return country name where IP address is located, or null if not 
     * available
     */
    public String getCountryName(final Locale locale) {
        if (mCountryCode != null) {
            final Locale l = new Locale(Locale.getDefault().getLanguage(),
                    mCountryCode);
            final String displayCountry;
            if (locale != null) {
                displayCountry = l.getDisplayCountry(locale);
            } else {
                displayCountry = l.getDisplayCountry();
            }
            if (displayCountry.equals(mCountryCode)) {
                // country code is invalid and it is returned as display country
                // name
                return mCountryName;
            } else {
                return displayCountry;
            }
        } else {
            return mCountryName;
        }
    }

    /**
     * Gets two-character ISO 3166-1 alpha code for the country where the ISP 
     * has registered a given IP block. This might differ the actual user's 
     * country.
     * @return registered ISO country code.
     */
    public String getRegisteredCountryCode() {
        return mRegisteredCountryCode;
    }

    /**
     * If available, returns registered country name where IP address is 
     * registered by the ISP using default system locale. If no value is found, 
     * the fallback non-localized name obtained from database will be returned
     * @return country name where IP address is located, or null if not 
     * available
     */
    public String getRegisteredCountryName() {
        return getRegisteredCountryName(null);
    }

    /**
     * If available, returns registered country name where IP address is 
     * registered by the ISP using provided locale.
     * If no value is found, the fallback non-localized name obtained from 
     * database will be returned
     * @param locale language to return localized country name
     * @return country name where IP address is located, or null if not 
     * available
     */
    public String getRegisteredCountryName(final Locale locale) {
        if (mRegisteredCountryCode != null) {
            final Locale l = new Locale(Locale.getDefault().getLanguage(),
                    mRegisteredCountryCode);
            final String displayCountry;
            if (locale != null) {
                displayCountry = l.getDisplayCountry(locale);
            } else {
                displayCountry = l.getDisplayCountry();
            }
            if (displayCountry.equals(mRegisteredCountryCode)) {
                // country code is invalid and it is returned as display country
                // name
                return mRegisteredCountryName;
            } else {
                return displayCountry;
            }
        } else {
            return mRegisteredCountryName;
        }
    }

    /**
     * Gets autonomous system number associated with the IP address.
     * @see <a href="http://en.wikipedia.org/wiki/Autonomous_system_(Internet)">
     * http://en.wikipedia.org/wiki/Autonomous_system_(Internet)</a>
     * @return autonomous system number associated with the IP address.
     */
    public Integer getAutonomousSystemNumber() {
        return mAutonomousSystemNumber;
    }

    /**
     * Gets the second level domain associated with the IP address. This will be
     * something like "eample.com" or "example.co.uk", not "foo.example.com".
     * @return second level domain associated with the IP address.
     */
    public String getDomain() {
        return mDomain;
    }

    /**
     * Gets the name of the ISP associated with the IP address.
     * @return name of the ISP associated with the IP address.
     */
    public String getIsp() {
        return mIsp;
    }

    /**
     * Gets the name of the organization associated with the IP address.
     * @return name of the organization associated with the IP address.
     */
    public String getOrganization() {
        return mOrganization;
    }

    /**
     * Gets two character continent code like "NA" (North America) or "OC" 
     * (Oceania).
     * @return continent code.
     */
    public String getContinentCode() {
        return mContinentCode;
    }

    /**
     * Gets name of continent where request originates from.
     * @return name of continent.
     */
    public String getContinentName() {
        return mContinentName;
    }

    /**
     * Returns geolocation level or accuracy that was requested to locate an
     * IP address.
     * Notice that available levels are: country, city (includes country data)
     * and organization (includes city and country data)
     * @return requested geolocation level
     */
    public IPGeolocationLevel getLevel() {
        return mLevel;
    }

    /**
     * Returns distance between current and provided location expressed in 
     * meters.
     * @param otherLocation other location to compare against this one
     * @return distance between locations expressed in meters
     */
    public double distance(final IPLocation otherLocation) {
        return LocationUtils.distanceBetweenMeters(mLatitude, mLongitude,
                otherLocation.mLatitude, otherLocation.mLongitude);
    }
}
