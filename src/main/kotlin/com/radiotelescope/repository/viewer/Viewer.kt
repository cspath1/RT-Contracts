package com.radiotelescope.repository.viewer

import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.user.User
import javax.persistence.Entity
import javax.persistence.*

/**
 * Entity Class representing a Viewer for the web-application
 *
 * This Entity correlates to the Viewer SQL table
 */
@Entity
@Table(name = "viewer")
class Viewer{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    lateinit var appointment: Appointment

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User
}
