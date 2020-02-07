package com.radiotelescope.repository.temperature

import java.util.*
import javax.persistence.*

/**
 * Entity Class representing a Temperature record stored by the control room
 *
 * This Entity correlates to the temperature SQL table
 */
@Entity
@Table(name = "temperature")
data class Temperature (
        @Column(name = "temperature", nullable = true)
        var temperature: Double,
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