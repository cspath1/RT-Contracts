package com.radiotelescope.repository.viewer

import com.radiotelescope.repository.appointment.Appointment
import javax.persistence.*

//For a researcher

@Entity
@Table(name = "viewer")
data class Viewer
(
        @Id
        @JoinColumn(name = "recipient_user_id", nullable = false)
        private val viewer_id:Long,
        @Column(name = "sharing_user_id", nullable = false)
        private val sharing_user_id:Long,
        @ManyToOne
        @JoinColumn(name = "shared_appointment_id", nullable = false)
        //  var appointment: Appointment? = null
        var appointmentId: Long
)

{
    //ultimate goal is to share the correct AppointmentInfo
}