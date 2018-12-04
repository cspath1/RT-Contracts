package com.radiotelescope.repository.coordinate

import javax.persistence.*

/**
 * Entity Class representing an Coordinate for the web-application
 *
 * This Entity correlates to the Coordinate SQL Table
 */
@Entity
@Table(name = "coordinate")
data class Coordinate(
        @Column(name = "right_ascension")
        var rightAscension: Double,
        @Column(name = "declination")
        var declination: Double
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    companion object {
        fun hoursMinutesSecondsToDegrees(hours: Int, minutes: Int, seconds: Int): Double {
            return (hours.toDouble() * 15) + (minutes.toDouble() / 4) + (seconds.toDouble() / 240)
        }
    }
}