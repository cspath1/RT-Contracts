package com.radiotelescope.contracts.weatherData

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.weatherData.IWeatherDataRepository
import com.radiotelescope.repository.weatherData.WeatherData
/**
 * Base concrete implementation of the [WeatherDataFactory] interface
 *
 * @param weatherDataRepo the [IWeatherDataRepository] interface
 */
class BaseWeatherDataFactory (
        private val weatherDataRepo: IWeatherDataRepository
) : WeatherDataFactory {
    /**
     * Override of the [WeatherDataFactory.create] method that will return a [Create] command
     *
     * @param request the [Create.Request] object
     * @return a [Create] command object
     */
    override fun create(request: Create.Request): Command<Long, Multimap<ErrorTag, String>> {
        return Create(
                request = request,
                weatherDataRepo = weatherDataRepo
        )
    }

}