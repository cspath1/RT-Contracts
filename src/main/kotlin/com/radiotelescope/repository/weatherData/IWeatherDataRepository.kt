package com.radiotelescope.repository.weatherData

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.*
@Repository
interface IWeatherDataRepository: PagingAndSortingRepository<WeatherData, Long> {
    /**
     * Spring Repository method that will return all [WeatherData] records between
     * the lower and upper date ranges
     *
     * @param lowerDate the start time of when to start grabbing the appointment
     * @param upperDate the end time of when to stop grabbing the appointment
     * @return a [List] of [WeatherData]
     */
    @Query(value = "SELECT * " +
            "FROM weather_data " +
            "WHERE insert_timestamp BETWEEN ?1 AND ?2",
            nativeQuery = true
    )
    fun findVideosCreatedBetweenDates(lowerDate: Date, upperDate: Date): List<WeatherData>

    @Query(value = "SELECT * FROM weather_data ORDER BY id DESC LIMIT 1",
            nativeQuery = true)
    fun getMostRecentWeatherData(): WeatherData
}