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

import com.irurueta.server.commons.ServerCommonsException;

/**
 * Base class for this package. All exception in this package will inherit from
 * this one.
 */
public class GeolocationException extends ServerCommonsException {
    /**
     * Constructor.
     */
    public GeolocationException() {
        super();
    }

    /**
     * Constructor with message.
     *
     * @param message message describing the cause of the exception.
     */
    public GeolocationException(final String message) {
        super(message);
    }

    /**
     * Constructor with message and cause.
     *
     * @param message message describing the cause of the exception.
     * @param cause   originating exception that caused this exception. This can
     *                be used to obtain stack traces.
     */
    public GeolocationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with cause.
     *
     * @param cause originating exception that caused this exception. This can
     *              be used to obtain stack traces.
     */
    public GeolocationException(final Throwable cause) {
        super(cause);
    }
}
