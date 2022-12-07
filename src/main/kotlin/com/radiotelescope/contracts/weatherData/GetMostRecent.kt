package com.radiotelescope.contracts.weatherData

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.weatherData.IWeatherDataRepository
import com.radiotelescope.repository.weatherData.WeatherData

class GetMostRecent (
    private val weatherDataRepo: IWeatherDataRepository
): Command<WeatherData, Multimap<ErrorTag, String>> {

    override fun execute(): SimpleResult<WeatherData, Multimap<ErrorTag, String>> {
        return  SimpleResult(weatherDataRepo.getMostRecentWeatherData(), null)
    }
}