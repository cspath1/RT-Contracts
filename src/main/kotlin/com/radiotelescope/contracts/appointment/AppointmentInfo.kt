package com.radiotelescope.contracts.appointment
import com.radiotelescope.repository.appointment.Appointment
import java.util.*
//View class for Appointment Entity

data class AppointmentInfo(
        val id: Long,
        val startTime: Date,
        val endTime: Date,
        val telescopeId: Long,
        val celestialBodyId: Long,
        val isPublic: Boolean,
        val userId: Long,
        val uFirstName: String,
        val uLastName: String,
        val statusI: Appointment.Status
)
{
//Secondary constructor
    constructor(a: Appointment): this(
            id = a.id,
            startTime = a.startTime,
            endTime = a.endTime,
            telescopeId = a.telescopeId,
            celestialBodyId = a.celestialBodyId,
            isPublic = a.isPublic,
            userId = a.user.id,
            uFirstName = a.uFirstName,
            uLastName = a.uLastName,
            statusI = a.status
    )
}
