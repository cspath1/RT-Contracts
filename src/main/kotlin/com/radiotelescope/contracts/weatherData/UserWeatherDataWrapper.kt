package com.radiotelescope.contracts.weatherData

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command

/**
 * Wrapper that takes a [WeatherDataFactory] and is responsible for all
 * user role validations for the Feedback Entity
 *
 * @param factory the [WeatherDataFactory] interface
 */

class UserWeatherDataWrapper (
        private val factory: WeatherDataFactory
){
    /**
     * Wrapper method for the [WeatherDataFactory.create] method.
     *
     * @param request the [Create.Request] object
     */
    fun create(request: Create.Request): Command<Long, Multimap<ErrorTag, String>> {
        return factory.create(request)
    }
}