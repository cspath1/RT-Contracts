package com.radiotelescope.contracts.appointment.info

import com.radiotelescope.repository.appointment.Appointment
import java.util.*

/**
 * Data class representing a read-only model of the
 * [Appointment] Entity, with some additional information
 * to display on the client-side
 *
 * @param id the Appointment's id
 * @param startTime the Appointment's start time
 * @param endTime the Appointment's end time
 * @param telescopeId the Appointment's associated telescope id
 * @param isPublic the Appointment's flag indicating if it is public or not
 * @param userId the Appointment's associated user id
 * @param userFirstName the Appointment owner's first name
 * @param userLastName the Appointment owner's last name
 * @param userEmail the Appointment owner's email address
 * @param status the Appointment's Status
 * @param type the Appointment's Type
 */
abstract class AppointmentInfo(
        open val id: Long,
        open val startTime: Date,
        open val endTime: Date,
        open val telescopeId: Long,
        open val isPublic: Boolean,
        open val userId: Long,
        open val userFirstName: String,
        open val userLastName: String,
        open val userEmail: String,
        open val status: String,
        open val type: String
)
