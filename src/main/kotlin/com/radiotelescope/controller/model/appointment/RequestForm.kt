package com.radiotelescope.controller.model.appointment

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.Request
import com.radiotelescope.controller.model.BaseForm
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
        val rightAscension: Double?,
        val declination: Double?
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
                rightAscension = rightAscension!!,
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
        if (rightAscension == null)
            errors.put(ErrorTag.RIGHT_ASCENSION, "Required field")
        if (declination == null)
            errors.put(ErrorTag.DECLINATION, "Required field")

        return if (errors.isEmpty) null else errors
    }
}