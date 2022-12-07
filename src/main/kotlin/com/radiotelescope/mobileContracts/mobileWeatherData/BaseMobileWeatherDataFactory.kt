package com.radiotelescope.mobileContracts.mobileWeatherData

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.weatherData.*
import com.radiotelescope.repository.weatherData.IWeatherDataRepository
import com.radiotelescope.repository.weatherData.WeatherData
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class BaseMobileWeatherDataFactory(
    private val weatherDataRepo: IWeatherDataRepository
) : MobileWeatherDataFactory {

    /**
     * Override of the [WeatherDataFactory.create] method that will return a [Create] command
     *
     * @param request the [Create.Request] object
     * @return a [Create] command object
     */

    override fun MobileCreateWeatherData(request: MobileCreateWeatherData.Request): Command<Long, Multimap<ErrorTag, String>> {
        return MobileCreateWeatherData(
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
    override fun MobileWeatherDataRetrieveList(pageable: Pageable): Command<Page<WeatherData>, Multimap<ErrorTag, String>> {
        return MobileWeatherDataRetrieveList(
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

    override fun MobileListBetweenCreationDates(request: MobileListBetweenCreationDates.Request): Command<List<WeatherData>, Multimap<ErrorTag, String>> {
        return MobileListBetweenCreationDates(
            request = request,
            weatherDataRepo = weatherDataRepo)
    }



}