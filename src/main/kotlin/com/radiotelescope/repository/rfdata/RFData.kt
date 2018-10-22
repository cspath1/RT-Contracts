package com.radiotelescope.repository.rfdata

import com.radiotelescope.repository.appointment.Appointment
import javax.persistence.*

/**
 * Entity Class representing Radio Frequency Data gathered
 * by the radio-telescope during an observation. This information
 * will be updated by the control room software, and will not be
 * modified by our application.
 *
 * This Entity correlates to the rf_data SQL table
 */
@Entity
@Table(name = "rf_data")
class RFData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private var id: Long = 0

    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private var appointment: Appointment? = null

    @Column(name = "intensity", nullable = false)
    private var intensity: Long? = null

    fun getId(): Long {
        return id
    }

    fun getAppointment(): Appointment? {
        return appointment!!
    }

    fun getIntensity(): Long? {
        return intensity!!
    }
}