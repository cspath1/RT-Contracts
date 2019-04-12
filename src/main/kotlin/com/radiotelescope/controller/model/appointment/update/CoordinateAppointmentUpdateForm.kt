package com.radiotelescope.controller.model.appointment.update

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.update.CoordinateAppointmentUpdate
import com.radiotelescope.controller.model.BaseForm
import com.radiotelescope.repository.appointment.Appointment
import java.util.*

/**
 * Update form that takes nullable versions of the [CoordinateAppointmentUpdate.Request] object.
 * It is in charge of making sure these values are not null before adapting it
 * to a [CoordinateAppointmentUpdate.Request] object
 *
 * @param startTime the Appointment's new start time
 * @param endTime the Appointment's new end time
 * @param telescopeId the Appointment's new telescope id
 * @param isPublic whether the Appointment is to be public or not
 * @param priority the Appointment's new priority
 * @param hours the right ascension hours
 * @param minutes the right ascension minutes
 * @param seconds the right ascension seconds
 * @param declination the declination
 */
data class CoordinateAppointmentUpdateForm (
        override val startTime: Date?,
        override val endTime: Date?,
        override val telescopeId: Long?,
        override val isPublic: Boolean?,
        override val priority: Appointment.Priority?,
        val hours: Int?,
        val minutes: Int?,
        val seconds: Int?,
        val declination: Double?
) : UpdateForm<CoordinateAppointmentUpdate.Request>() {
    /**
     * Override of the [BaseForm.toRequest] method that
     * adapts the form into a [CoordinateAppointmentUpdate.Request] object
     */
    override fun toRequest(): CoordinateAppointmentUpdate.Request {
        return CoordinateAppointmentUpdate.Request(
                id = -1L,
                startTime = startTime!!,
                endTime = endTime!!,
                telescopeId = telescopeId!!,
                isPublic = isPublic!!,
                hours = hours!!,
                minutes = minutes!!,
                seconds = seconds!!,
                declination = declination!!,
                priority = priority!!
        )
    }

    /**
     * Makes sure all required fields are not null
     *
     * @return a [HashMultimap] of errors or null
     */
    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (startTime == null)
            errors.put(ErrorTag.START_TIME, "Required field")
        if (endTime == null)
            errors.put(ErrorTag.END_TIME, "Required field")
        if (telescopeId == null)
            errors.put(ErrorTag.TELESCOPE_ID, "Required field")
        if (isPublic == null)
            errors.put(ErrorTag.PUBLIC, "Required field")
        if (hours == null)
            errors.put(ErrorTag.HOURS, "Required field")
        else if (hours >= 24 || hours < 0)
            errors.put(ErrorTag.HOURS, "Hours must be between 0 and 24")
        if (minutes == null)
            errors.put(ErrorTag.MINUTES, "Required field")
        else if (minutes >= 60 || minutes < 0)
            errors.put(ErrorTag.MINUTES, "Minutes must be between 0 and 60")
        if (seconds == null)
            errors.put(ErrorTag.SECONDS, "Required field")
        else if (seconds >= 60 || seconds < 0)
            errors.put(ErrorTag.SECONDS, "Seconds must be between 0 and 60")
        if (declination == null)
            errors.put(ErrorTag.DECLINATION, "Required field")
        if(priority == null)
            errors.put(ErrorTag.PRIORITY, "Invalid priority")

        return if (errors.isEmpty) null else errors
    }
}