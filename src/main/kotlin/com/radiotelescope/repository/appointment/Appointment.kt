package com.radiotelescope.repository.appointment

import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.orientation.Orientation
import com.radiotelescope.repository.user.User
import java.util.*
import javax.persistence.*


/**
 * Entity Class representing an Appointment for the web-application
 *
 * This Entity correlates to the Appointment SQL Table
 */
@Entity
@Table(name = "appointment")
data class Appointment(
        @Column(name = "start_time", nullable = false)
        var startTime: Date,
        @Column(name = "end_time", nullable = false)
        var endTime: Date,
        @Column(name = "telescope_id", nullable = false)
        var telescopeId: Long,
        @Column(name = "public", nullable = false)
        var isPublic: Boolean
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User

    @OneToOne
    @JoinColumn(name = "coordinate_id")
    var coordinate: Coordinate? = null

    @OneToOne
    @JoinColumn(name = "orientation_id")
    var orientation: Orientation? = null

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    var status: Appointment.Status = Status.SCHEDULED

    enum class Status(val label: String) {
        REQUESTED("Requested"),
        SCHEDULED("Scheduled"),
        IN_PROGRESS("In Progress"),
        COMPLETED("Completed"),
        CANCELED("Canceled")
    }

    companion object {
        // 5 hours for guests
        const val GUEST_APPOINTMENT_TIME_CAP: Long = (5 * 60 * 60 * 1000)
        // 50 hours for others
        const val OTHER_USERS_APPOINTMENT_TIME_CAP: Long = (50 * 60 * 60 * 1000)
    }
}