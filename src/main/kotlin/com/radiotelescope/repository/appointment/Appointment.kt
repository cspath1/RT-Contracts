package com.radiotelescope.repository.appointment

import com.radiotelescope.repository.user.User
import java.util.*
import javax.persistence.*


/**
 * Entity Class representing an Appointment for the web-application
 *
 * This Entity correlates to the Appointment SQL table
 */
@Entity
@Table(name = "appointment")
data class Appointment(
        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        var user: User,
        @Column(name = "type", nullable = false)
        /*
        This is to be implemented
        @OneToMany
         @JoinColumn(name="orientation_id", nullable= false)
        var orientation: Orientation,
        */
        /*
        @OneToMany
        @JoinColumn(name="celestialBody_id", nullable = false)
       var celestialBody: CelestialBody,
       */
        var type: String,
        @Column(name = "start_time", nullable = false, unique = true)
        var startTime: Date,
        @Column(name = "end_time", nullable = false, unique = true)
        var endTime: Date,
        @Column(name = "telescope_id", nullable = false)
        var telescopeId: Long,
        @Column(name = "celestial_body_id", nullable = false)
        var celestialBodyId: Long,
        @Column(name = "receiver", nullable = false)
        var receiver: String,
        @Column(name = "isPublic", nullable = false)
        var isPublic: Boolean,
        @Column(name = "date", nullable = false)
        var userId: Long,
        @Column(name = "uFirstName", nullable= false)
        var uFirstName: String,
        @Column(name = "uLastName", nullable = false)
        var uLastName: String,
        @Column(name = "state", nullable = false)
        var state: Int
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    var status: Appointment.Status = Status.Requested

    enum class Status {
        Requested,
        Scheduled,
        InProgress,
        Completed,
        Canceled
    }
}