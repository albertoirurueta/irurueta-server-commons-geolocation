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

/**
 * Enumerator defining geolocation level.
 */
public enum IPGeolocationLevel {
    /**
     * Geolocation disabled.
     */
    DISABLED("disabled"),
    
    /**
     * Country level geolocation.
     */
    COUNTRY("country"),
    
    /**
     * City level geolocation.
     */
    CITY("city");
    
    /**
     * String representation of this enumerator.
     */    
    private String mValue;
    
    /**
     * Constructor.
     * @param value string representation.
     */    
    private IPGeolocationLevel(String value){
        mValue = value;
    }
    
    /**
     * Returns string representation.
     * @return string representation.
     */
    public String getValue(){
        return mValue;
    }
    
    /**
     * Factory method to create an enumerator value from its string 
     * representation.
     * @param value string representation.
     * @return enumerator value.
     */
    public static IPGeolocationLevel fromValue(String value) {
        if(value != null){
            if(value.equalsIgnoreCase("disabled")) return DISABLED;
            if(value.equalsIgnoreCase("country")) return COUNTRY;
            if(value.equalsIgnoreCase("city")) return CITY;
        }
        return IPGeolocationLevel.DISABLED;
    }    
}
