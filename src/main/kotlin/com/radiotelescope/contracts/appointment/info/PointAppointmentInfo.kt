package com.radiotelescope.contracts.appointment.info

import com.radiotelescope.repository.appointment.Appointment
import java.util.*

/**
 * Subclass of [AppointmentInfo] for the "Point" Appointment type
 *
 * @param hours the right ascension hours
 * @param minutes the right ascension minutes
 * @param rightAscension the right ascension in degrees
 * @param declination the declination
 */
data class PointAppointmentInfo(
        override val id: Long,
        override val startTime: Date,
        override val endTime: Date,
        override val telescopeId: Long,
        override val isPublic: Boolean,
        override val userId: Long,
        override val userFirstName: String,
        override val userLastName: String,
        override val userEmail: String,
        override val status: String,
        override val type: String,
        override val priority: String,
        override val spectracyberConfigId: Long,
        val hours: Int,
        val minutes: Int,
        val rightAscension: Double,
        val declination: Double
) : AppointmentInfo(
        id = id,
        startTime = startTime,
        endTime = endTime,
        telescopeId = telescopeId,
        isPublic = isPublic,
        userId = userId,
        userFirstName = userFirstName,
        userLastName = userLastName,
        userEmail = userEmail,
        status = status,
        type = type,
        priority = priority,
        spectracyberConfigId = spectracyberConfigId
) {
    /**
     * Secondary constructor that takes an appointment object
     * to set all fields
     *
     * @param appointment the Appointment
     */
    constructor(appointment: Appointment) : this(
            id = appointment.id,
            startTime = appointment.startTime,
            endTime = appointment.endTime,
            telescopeId = appointment.telescopeId,
            isPublic = appointment.isPublic,
            userId = appointment.user.id,
            userFirstName = appointment.user.firstName,
            userLastName = appointment.user.lastName,
            userEmail = appointment.user.email,
            status = appointment.status.label,
            type = appointment.type.label,
            priority = appointment.priority.label,
            hours = appointment.coordinateList[0].hours,
            minutes = appointment.coordinateList[0].minutes,
            rightAscension = appointment.coordinateList[0].rightAscension,
            declination = appointment.coordinateList[0].declination,
            spectracyberConfigId = appointment.spectracyberConfig!!.id
    )
}