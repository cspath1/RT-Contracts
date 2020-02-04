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
        @Column(name = "wind", nullable = false)
        var wind: Int?,
        @Column(name = "az_motor_temp", nullable = false)
        var azMotorTemp: Int?,
        @Column(name = "elev_motor_temp", nullable = false)
        var elevMotorTemp: Int?,
        @Column(name = "az_motor_vibration", nullable = false)
        var azMotorVibration: Int?,
        @Column(name = "elev_motor_vibration", nullable = false)
        var elevMotorVibration: Int?,
        @Column(name = "az_motor_current", nullable = false)
        var azMotorCurrent: Int?,
        @Column(name = "elev_motor_current", nullable = false)
        var elevMotorCurrent: Int?,
        @Column(name = "counter_balance_vibration", nullable = false)
        var counterBalanceVibration: Int?
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0
}