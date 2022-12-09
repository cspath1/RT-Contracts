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
        WEATHER_STATION("WEATHER STATION"),
        ELEVATION_ABS_ENCODER("ELEVATION ABS ENCODER"),
        AZIMUTH_ABS_ENCODER("AZIMUTH ABS ENCODER"),
        AZ_MOTOR_VIBRATION("AZ MOTOR VIBRATION"),
        ELEV_MOTOR_VIBRATION("ELEV MOTOR VIBRATION"),
        COUNTER_BALANCE_VIBRATION("COUNTER BALANCE VIBRATION"),
        EL_PROXIMITY_0("EL PROXIMITY 0"),
        EL_PROXIMITY_90("EL PROXIMITY 90"),
        AMBIENT_TEMP_HUMIDITY("AMBIENT_TEMP_HUMIDITY")
    }
}