package com.radiotelescope.controller.model.appointment

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.appointment.Create
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.controller.model.BaseForm
import java.util.*

/**
 * Create form that takes nullable versions of the [Create.Request] object.
 * It is in charge of making sure these values are not null before adapting it
 * to a [Create.Request] object
 *
 * @param userId the User id
 * @param startTime the Appointment start time
 * @param endTime the Appointment end time
 * @param telescopeId the Appointment's telescope
 */
data class CreateForm(
        val userId: Long?,
        val startTime: Date?,
        val endTime: Date?,
        val telescopeId: Long?,
        val isPublic: Boolean?,
        val rightAscension: Double?,
        val declination: Double?
) : BaseForm<Create.Request> {
    /**
     * Override of the [BaseForm.toRequest] method that
     * adapts the form into a [Create.Request] object
     *
     * @return the [Create.Request] object
     */
    override fun toRequest(): Create.Request {
        return Create.Request(
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
            errors.put(ErrorTag.START_TIME, "Required field: Start Time")
        if (endTime == null)
            errors.put(ErrorTag.END_TIME, "Required field: End Time")
        if (telescopeId == null)
            errors.put(ErrorTag.TELESCOPE_ID, "Required field: Telescope Id")
        if (isPublic == null)
            errors.put(ErrorTag.PUBLIC, "Required field: isPublic")
        if (rightAscension == null)
            errors.put(ErrorTag.RIGHT_ASCENSION, "Required field: Right Ascension")
        if (declination == null)
            errors.put(ErrorTag.DECLINATION, "Required field: Declination")

        return if (errors.isEmpty) null else errors
    }
}