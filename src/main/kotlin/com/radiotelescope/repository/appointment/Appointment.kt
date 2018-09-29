package com.radiotelescope.repository.appointment

import javax.persistence.*

/**
 * Entity Class representing an Appointment for the web-application
 *
 * This Entity correlates to the Appointment SQL table
 */
@Entity
@Table(name = "appointment")
data class Appointment(
        @Column(name = "user_id", nullable = false)
        var userId: Long,
        @Column(name = "type", nullable = false)
        var type: String,
        @Column(name = "start_time", nullable = false, unique = true)
        var startTime: String,
        @Column(name = "end_time", nullable = false, unique = true)
        var endTime: String,
        @Column(name = "telescope_id", nullable = false)
        var telescopeId: Int,
        @Column(name = "celestial_body_id", nullable = false)
        var celestialBodyId: Int,
        @Column(name = "coordinates", nullable = false)
        var coordinates: Int,
        @Column(name = "receiver", nullable = false)
        var receiver: String,
        @Column(name = "public", nullable = false)
        var startTime: Boolean

) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    var status: Appointment.Status = Status.Requested

    @Column(name = "state")
    @Enumerated(value = EnumType.STRING)
    var state: Appointment.State = State.Waiting

    enum class Status {
        Requested,
        Scheduled,
        InProgress,
        Completed,
        Canceled
    }
        /*
        Not sure on the possible values for the state enum - JM
         */
    enum class State{
        Waiting
    }
}