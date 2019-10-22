package com.radiotelescope.repository.coordinate

import com.radiotelescope.repository.appointment.Appointment
import javax.persistence.*

/**
 * Entity Class representing a Coordinate for the web-application
 *
 * This Entity correlates to the Coordinate SQL Table
 */
@Entity
@Table(name = "coordinate")
data class Coordinate(
        @Column(name = "hours")
        var hours: Int,
        @Column(name = "minutes")
        var minutes: Int,
        @Column(name = "right_ascension")
        var rightAscension: Double,
        @Column(name = "declination")
        var declination: Double
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    var appointment: Appointment? = null

    companion object {
        fun hoursMinutesSecondsToDegrees(hours: Int, minutes: Int): Double {
            return (hours.toDouble() * 15) + (minutes.toDouble() / 4)
        }
    }
}