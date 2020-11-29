package com.radiotelescope.contracts.weatherData

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.weatherData.IWeatherDataRepository
import com.radiotelescope.repository.weatherData.WeatherData
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

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
    /**
     * Override of the [WeatherData.retrieveList] method that will return a [RetrieveList] command
     *
     * @param pageable the [Pageable] object that has the page number and page size
     * @return a [RetrieveList] command object
     */
    override fun retrieveList(pageable: Pageable): Command<Page<WeatherData>, Multimap<ErrorTag, String>> {
        return RetrieveList(
                pageable = pageable,
                weatherDataRepo = weatherDataRepo
        )
    }

    /**
     * Override of the [WeatherDataFactory.listBetweenCreationDates] method that will return a [ListBetweenCreationDates] command
     *
     * @param request the [ListBetweenCreationDates.Request] object
     * @return a [ListBetweenCreationDates] command object
     */
    override fun listBetweenCreationDates(request: ListBetweenCreationDates.Request): Command<List<WeatherData>, Multimap<ErrorTag, String>> {
        return ListBetweenCreationDates(
                request = request,
                weatherDataRepo = weatherDataRepo
        )
    }
}