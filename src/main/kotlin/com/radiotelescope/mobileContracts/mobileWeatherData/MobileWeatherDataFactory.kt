package com.radiotelescope.mobileContracts.mobileWeatherData

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.weatherData.ErrorTag
import com.radiotelescope.repository.weatherData.WeatherData
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable


interface MobileWeatherDataFactory {

fun MobileCreateWeatherData(request: MobileCreateWeatherData.Request): Command<Long, Multimap<ErrorTag, String>>


fun MobileWeatherDataRetrieveList(pageable: Pageable): Command<Page<WeatherData>, Multimap<ErrorTag, String>>

fun MobileListBetweenCreationDates(request: MobileListBetweenCreationDates.Request): Command<List<WeatherData>, Multimap<ErrorTag, String>>

}