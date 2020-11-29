package com.radiotelescope.repository.weatherData
import java.util.*
import javax.persistence.*

/**
 * Entity Class representing a Weather Data record for the web-application
 *
 * This Entity correlates to the weather_data SQL table
 */
@Entity
@Table(name = "weather_data")
data class WeatherData (
        @Column(name = "wind_speed", nullable = false)
        var windSpeed: Float?,
        @Column(name = "wind_direction_deg", nullable = false)
        var windDirectionDeg: Float?,
        @Column(name = "wind_direction_str", nullable = false)
        var windDirectionStr: String?,
        @Column(name = "outside_temperature_degF", nullable = false)
        var outsideTemperatureDegF: Float?,
        @Column(name = "inside_temperature_degF", nullable = false)
        var insideTemperatureDegF: Float?,
        @Column(name = "rain_rate", nullable = false)
        var rainRate: Float?,
        @Column(name = "rain_total", nullable = false)
        var rainTotal: Float?,
        @Column(name = "rain_day", nullable = false)
        var rainDay: Float?,
        @Column(name = "rain_month", nullable = false)
        var rainMonth: Float?,
        @Column(name = "barometric_pressure", nullable = false)
        var barometricPressure: Float?,
        @Column(name = "dew_point", nullable = false)
        var dewPoint: Float?,
        @Column(name = "wind_chill", nullable = false)
        var windChill: Float?,
        @Column(name = "humidity", nullable = false)
        var humidity: Float?,
        @Column(name = "heat_index", nullable = false)
        var heatIndex: Float?,
        @Column(name = "time_captured", nullable = false)
        var timeCaptured: Int?
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    @Column(name = "insert_timestamp", nullable = true)
    var insertTimestamp: Date = Date()

    @Column(name = "update_timestamp", nullable = true)
    var updateTimestamp: Date = Date()
}