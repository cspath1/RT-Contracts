package com.radiotelescope.repository.vibration

import java.sql.Blob
import java.util.*
import javax.persistence.*

/**
 * Entity Class representing a Vibration record stored by the control room
 *
 * This Entity correlates to the vibration SQL table
 */
@Entity
@Table(name = "vibration")
data class Vibration (
        @Column(name = "vibration_data", nullable = true)
        var vibrationData: Blob,
        @Column(name = "FFT_start_time", nullable = true)
        var FFTStartTime: String,
        @Column(name = "FFT_end_time", nullable = true)
        var FFTEndTime: String,
        @Column(name = "start_frequency", nullable = true)
        var startFrequency: Double,
        @Column(name = "frequency_step_per_division", nullable = true)
        var frequencyStepPerDivision: Double,
        @Column(name = "number_points", nullable = true)
        var numberPoints: Int,
        @Column(name = "location", nullable = true)
        var location: Int
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