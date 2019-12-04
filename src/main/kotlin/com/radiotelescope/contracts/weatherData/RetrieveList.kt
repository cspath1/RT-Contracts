package com.radiotelescope.contracts.weatherData

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.weatherData.IWeatherDataRepository
import com.radiotelescope.repository.weatherData.WeatherData
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Override of the [Command] interface method used to retrieve [WeatherData]
 * information
 *
 * @param pageable the [Pageable] interface
 * @param weatherDataRepo the [IWeatherDataRepository] interface
 */
class RetrieveList (
        private val pageable: Pageable,
        private val weatherDataRepo: IWeatherDataRepository
) : Command<Page<WeatherData>, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command] execute method.
     *
     * It will create a [Page] of [WeatherData] objects and
     * return this in the [SimpleResult.success] value.
     */
    override fun execute(): SimpleResult<Page<WeatherData>, Multimap<ErrorTag, String>> {
        val page = weatherDataRepo.findAll(pageable)
        return SimpleResult(page, null)
    }
}