package com.radiotelescope.contracts.weatherData

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.weatherData.Create
import com.radiotelescope.contracts.weatherData.ErrorTag
import com.radiotelescope.repository.weatherData.WeatherData

interface WeatherDataFactory {
    /**
     * Abstract command used to create a new [WeatherData] object
     *
     * @param request the [Create.Request] request
     * @return a [Command] object
     */
    fun create(request: Create.Request): Command<Long, Multimap<ErrorTag, String>>
}