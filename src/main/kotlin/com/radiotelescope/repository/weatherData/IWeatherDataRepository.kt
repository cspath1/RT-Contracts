package com.radiotelescope.repository.weatherData

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface IWeatherDataRepository: CrudRepository<WeatherData, Long> {
}