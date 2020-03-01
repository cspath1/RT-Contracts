package com.radiotelescope.repository.sensorOverrides

import javax.persistence.*

/**
 * Entity Class representing a SensorOverrides for the web-application
 *
 * This Entity correlates to the sensor_overrides SQL table
 */
@Entity
@Table(name="sensor_overrides")
data class SensorOverrides (
        @Column(name="sensor_name", nullable=false)
        @Enumerated(value = EnumType.STRING)
        var sensorName: Name,
        @Column(name="overridden")
        var overridden: Boolean
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    enum class Name(val label: String) {
        GATE("Gate"),
        PROXIMITY("Proximity"),
        AZIMUTH_MOTOR("Azimuth Motor"),
        ELEVATION_MOTOR("Elevation Motor"),
        WEATHER_STATION("Weather Station")
    }
}