package com.radiotelescope.contracts.weatherData

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.weatherData.WeatherData
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface WeatherDataFactory {
    /**
     * Abstract command used to create a new [WeatherData] object
     *
     * @param request the [Create.Request] request
     * @return a [Command] object
     */
    fun create(request: Create.Request): Command<Long, Multimap<ErrorTag, String>>

    /**
     * Abstract command user to retrieve a list of [WeatherData] objects as a [Page]
     *
     * @param pageable the [Pageable] object, that has the page number and page size
     * @return a [Command] object
     */
    fun retrieveList(pageable: Pageable): Command<Page<WeatherData>, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to list [WeatherData] objects between two creations dates
     *
     * @param request the [ListBetweenCreationDates.Request] request
     * @return a [Command] object
     */
    fun listBetweenCreationDates(request: ListBetweenCreationDates.Request): Command<List<WeatherData>, Multimap<ErrorTag, String>>
}