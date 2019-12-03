package com.radiotelescope.repository.weatherData

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface IWeatherDataRepository: PagingAndSortingRepository<WeatherData, Long> {
}