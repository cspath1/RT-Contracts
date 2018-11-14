package com.radiotelescope.repository.viewer

import com.radiotelescope.contracts.appointment.ListBetweenDates
import com.radiotelescope.contracts.role.Validate
import com.radiotelescope.contracts.viewer.Create
import com.radiotelescope.repository.appointment.Appointment
import javax.persistence.*

/**
Entity for Viewer table.
A Viewer is a User who can view another user's Appointment.
 */

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
        var appointment: Appointment? = null
)
{
}