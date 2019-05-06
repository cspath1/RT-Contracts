package com.radiotelescope.repository.rfdata

import com.radiotelescope.repository.appointment.Appointment
import java.util.*
import javax.persistence.*

/**
 * Entity Class representing Radio Frequency Data gathered
 * by the radio-telescope during an observation.
 *
 * This Entity correlates to the rf_data SQL table
 */
@Entity
@Table(name = "rf_data")
class RFData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    lateinit var appointment: Appointment

    @Column(name = "intensity", nullable = false)
    var intensity: Long? = null

    @Column(name = "time_captured", nullable = false)
    var timeCaptured: Date? = null
}