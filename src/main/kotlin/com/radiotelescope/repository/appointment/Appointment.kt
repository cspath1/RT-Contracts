package com.radiotelescope.repository.appointment

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
        @Column(name = "type", nullable = false)
        var startTime: Date,
        @Column(name = "end_time", nullable = false, unique = true)
        var endTime: Date,
        @Column(name = "telescope_id", nullable = false)
        var telescopeId: Long,
        @Column(name = "isPublic", nullable = false)
        var isPublic: Boolean
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    var status: Appointment.Status = Status.Scheduled

    enum class Status {
        Requested,
        Scheduled,
        InProgress,
        Completed,
        Canceled
    }
}