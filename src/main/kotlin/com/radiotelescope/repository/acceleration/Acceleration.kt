package com.radiotelescope.repository.acceleration

import java.util.*
import javax.persistence.*

/**
 * Entity Class representing a Acceleration record stored by the control room
 *
 * This Entity correlates to the acceleration SQL table
 */
@Entity
@Table(name = "acceleration")
data class Acceleration (
        @Column(name = "acceleration_magnitude", nullable = true)
        var accelerationMagnitude: Double,
        @Column(name = "acceleration_x", nullable = true)
        var accelerationX: Double,
        @Column(name = "acceleration_y", nullable = true)
        var accelerationY: Double,
        @Column(name = "acceleration_z", nullable = true)
        var accelerationZ: Double,
        @Column(name = "location", nullable = true)
        var location: Int,
        @Column(name = "time_captured", nullable = true)
        var timeCaptured: Int
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