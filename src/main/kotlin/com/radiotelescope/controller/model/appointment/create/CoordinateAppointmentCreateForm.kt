package com.radiotelescope.controller.model.appointment.create

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.appointment.create.CoordinateAppointmentCreate
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.controller.model.BaseForm
import com.radiotelescope.repository.appointment.Appointment
import java.util.*

/**
 * Create form that takes nullable versions of the [CoordinateAppointmentCreate.Request] object.
 * It is in charge of making sure these values are not null before adapting it
 * to a [CoordinateAppointmentCreate.Request] object
 *
 * @param userId the User id
 * @param startTime the Appointment start time
 * @param endTime the Appointment end time
 * @param telescopeId the Appointment's telescope
 * @param isPublic whether the Appointment is public or not
 * @param priority the Appointment priority
 * @param hours the Right Ascension hours
 * @param minutes the Right Ascension minutes
 * @param declination the Declination
 */
data class CoordinateAppointmentCreateForm(
        override val userId: Long?,
        override val startTime: Date?,
        override val endTime: Date?,
        override val telescopeId: Long?,
        override val isPublic: Boolean?,
        override val priority: Appointment.Priority?,
        val hours: Int?,
        val minutes: Int?,
        val declination: Double?
) : CreateForm<CoordinateAppointmentCreate.Request>() {
    /**
     * Override of the [BaseForm.toRequest] method that
     * adapts the form into a [CoordinateAppointmentCreate.Request] object
     *
     * @return the [CoordinateAppointmentCreate.Request] object
     */
    override fun toRequest(): CoordinateAppointmentCreate.Request {
        return CoordinateAppointmentCreate.Request(
                userId = userId!!,
                startTime = startTime!!,
                endTime = endTime!!,
                telescopeId = telescopeId!!,
                isPublic = isPublic!!,
                hours = hours!!,
                minutes = minutes!!,
                declination = declination!!,
                priority = priority!!
        )
    }

    /**
     * Makes sure the required fields are not null
     *
     * @return a [HashMultimap] of errors or null
     */
    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        if (userId == null)
            errors.put(ErrorTag.USER_ID, "Invalid user id")
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
        if (declination == null)
            errors.put(ErrorTag.DECLINATION, "Required field")
        if(priority == null)
            errors.put(ErrorTag.PRIORITY, "Invalid priority")

        return if (errors.isEmpty) null else errors
    }
}