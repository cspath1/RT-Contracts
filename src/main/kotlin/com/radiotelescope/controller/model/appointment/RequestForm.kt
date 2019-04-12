package com.radiotelescope.controller.model.appointment

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.Request
import com.radiotelescope.controller.model.BaseForm
import com.radiotelescope.repository.appointment.Appointment
import java.util.*

/**
 * Request form that takes nullable versions of the [Request.Request] object.
 * It is in charge of making sure these values are not null before adapting it
 * to a [Request.Request] object
 *
 * @param userId the User id
 * @param startTime the Appointment start time
 * @param endTime the Appointment end time
 * @param telescopeId the Appointment's telescope
 */
data class RequestForm(
        val userId: Long?,
        val startTime: Date?,
        val endTime: Date?,
        val telescopeId: Long?,
        val isPublic: Boolean?,
        val hours: Int?,
        val minutes: Int?,
        val seconds: Int?,
        val declination: Double?,
        val priority: Appointment.Priority?
) : BaseForm<Request.Request> {
    /**
     * Override of the [BaseForm.toRequest] method that
     * adapts the form into a [Request.Request] object
     *
     * @return the [Request.Request] object
     */
    override fun toRequest(): Request.Request {
        return Request.Request(
                userId = userId!!,
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
        if(priority == null)
            errors.put(ErrorTag.PRIORITY, "Invalid Priority")

        return if (errors.isEmpty) null else errors
    }
}