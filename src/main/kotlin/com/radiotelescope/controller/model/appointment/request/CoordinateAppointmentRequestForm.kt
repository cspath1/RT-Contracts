package com.radiotelescope.controller.model.appointment.request

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.request.CoordinateAppointmentRequest
import com.radiotelescope.controller.model.BaseForm
import java.util.*

/**
 * Request form that takes nullable versions of the [CoordinateAppointmentRequest.Request] object.
 * It is in charge of making sure these values are not null before adapting it
 * to a [CoordinateAppointmentRequest.Request] object
 *
 * @param userId the User id
 * @param startTime the Appointment start time
 * @param endTime the Appointment end time
 * @param telescopeId the Appointment's telescope
 */
data class CoordinateAppointmentRequestForm(
        override val userId: Long?,
        override val startTime: Date?,
        override val endTime: Date?,
        override val telescopeId: Long?,
        override val isPublic: Boolean?,
        val hours: Int?,
        val minutes: Int?,
        val seconds: Int?,
        val declination: Double?
) : RequestForm<CoordinateAppointmentRequest.Request>() {
    /**
     * Override of the [BaseForm.toRequest] method that
     * adapts the form into a [CoordinateAppointmentRequest.Request] object
     *
     * @return the [CoordinateAppointmentRequest.Request] object
     */
    override fun toRequest(): CoordinateAppointmentRequest.Request {
        return CoordinateAppointmentRequest.Request(
                userId = userId!!,
                startTime = startTime!!,
                endTime = endTime!!,
                telescopeId = telescopeId!!,
                isPublic = isPublic!!,
                hours = hours!!,
                minutes = minutes!!,
                seconds = seconds!!,
                declination = declination!!
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
        if (seconds == null)
            errors.put(ErrorTag.SECONDS, "Required field")
        else if (seconds >= 60 || seconds < 0)
            errors.put(ErrorTag.SECONDS, "Seconds must be between 0 and 60")
        if (declination == null)
            errors.put(ErrorTag.DECLINATION, "Required field")

        return if (errors.isEmpty) null else errors
    }
}