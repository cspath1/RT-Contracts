package com.radiotelescope.contracts.appointment

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
 * @param status the Appointment's Status
 */
data class AppointmentInfo(
        val id: Long,
        val startTime: Date,
        val endTime: Date,
        val telescopeId: Long,
        val isPublic: Boolean,
        val userId: Long,
        val userFirstName: String,
        val userLastName: String,
        val status: String
) {
    /**
     * Secondary constructor that takes an appointment object
     * to set all fields
     *
     * @param appointment the Appointment
     */
    constructor(appointment: Appointment): this(
            id = appointment.id,
            startTime = appointment.startTime,
            endTime = appointment.endTime,
            telescopeId = appointment.telescopeId,
            isPublic = appointment.isPublic,
            userId = appointment.user!!.id,
            userFirstName = appointment.user!!.firstName,
            userLastName = appointment.user!!.lastName,
            status = appointment.status.label
    )
}
