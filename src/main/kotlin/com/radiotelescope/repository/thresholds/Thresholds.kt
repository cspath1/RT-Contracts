package com.radiotelescope.repository.thresholds

import javax.persistence.*

/**
 * Entity Class representing a Thresholds for the web-application
 *
 * This Entity correlates to the thresholds SQL table
 */
@Entity
@Table(name = "thresholds")
data class Thresholds (
        @Column(name = "sensor_name", nullable = false)
        @Enumerated(value = EnumType.STRING)
        var sensorName: Name,
        @Column(name = "maximum", nullable = false)
        var maximum: Double
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    enum class Name(val label: String) {
        WIND("Wind"),
        AZ_MOTOR_TEMP("Azimuth Motor Temperature"),
        ELEV_MOTOR_TEMP("Elevation Motor Temperature"),
        AZ_MOTOR_VIBRATION("Azimuth Motor Vibration"),
        ELEV_MOTOR_VIBRATION("Elevation Motor Vibration"),
        AZ_MOTOR_CURRENT("Azimuth Motor Current"),
        ELEV_MOTOR_CURRENT("Elevation Motor Current"),
        COUNTER_BALANCE_VIBRATION("Counter Balance Vibration")
    }
}