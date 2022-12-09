package com.radiotelescope.repository.sensorStatus

import java.util.*
import javax.persistence.*

/**
 * Entity Class representing a Sensor Status for the web-application
 *
 * This Entity correlates to the sensor_status SQL table
 */

@Entity
@Table(name = "sensor_status")
data class SensorStatus(
        @Column(name = "gate", nullable = false)
        var gate: Int?,
        @Column(name = "weather_station", nullable = false)
        var weatherStation: Int?,
        @Column(name = "elevation_abs_encoder ", nullable = false)
        var elevation_abs_encoder : Int?,
        @Column(name = "azimuth_abs_encoder ", nullable = false)
        var azimuth_abs_encoder : Int?,
        @Column(name = "el_proximity_0  ", nullable = false)
        var el_proximity_0  : Int?,
        @Column(name = "el_proximity_90  ", nullable = false)
        var el_proximity_90  : Int?,
        @Column(name = "az_motor_temp_1  ", nullable = false)
        var az_motor_temp_1  : Int?,
        @Column(name = "az_motor_temp_2 ", nullable = false)
        var az_motor_temp_2 : Int?,
        @Column(name = "el_motor_temp_1  ", nullable = false)
        var el_motor_temp_1  : Int?,
        @Column(name = "el_motor_temp_2  ", nullable = false)
        var el_motor_temp_2  : Int?,
        @Column(name = "az_accel  ", nullable = false)
        var az_accel  : Int?,
        @Column(name = "el_accel   ", nullable = false)
        var el_accel   : Int?,
        @Column(name = "counter_balance_accel   ", nullable = false)
        var counter_balance_accel   : Int?

) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    @Column(name = "insert_timestamp", nullable = false)
    var recordCreatedTimestamp: Date = Date()

    @Column(name = "update_timestamp", nullable = true)
    var recordUpdatedTimestamp: Date = Date()
}