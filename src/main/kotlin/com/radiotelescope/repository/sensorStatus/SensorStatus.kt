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
        @Column(name = "proximity", nullable = false)
        var proximity: Int?,
        @Column(name = "azimuth_motor", nullable = false)
        var azimuthMotor: Int?,
        @Column(name = "elevation_motor", nullable = false)
        var elevationMotor: Int?,
        @Column(name = "weather_station", nullable = false)
        var weatherStation: Int?
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