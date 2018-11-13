package com.radiotelescope.controller.model.appointment

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.Update
import com.radiotelescope.controller.model.BaseForm
import java.util.*

/**
 * Update form that takes nullable versions of the [Update.Request] object.
 * It is in charge of making sure these values are not null before adapting it
 * to a [Update.Request] object
 *
 * @param startTime the Appointment's new start time
 * @param endTime the Appointment's new end time
 * @param telescopeId the Appointment's new telescope id
 * @param isPublic whether the Appointment is to be public or not
 */
data class UpdateForm (
        val startTime: Date?,
        val endTime: Date?,
        val telescopeId: Long?,
        val isPublic: Boolean?,
        val rightAscension: Double?,
        val declination: Double?
) : BaseForm<Update.Request> {
    /**
     * Override of the [BaseForm.toRequest] method that
     * adapts the form into a [Update.Request] object
     */
    override fun toRequest(): Update.Request {
        return Update.Request(
                id = -1L,
                startTime = startTime!!,
                endTime = endTime!!,
                telescopeId = telescopeId!!,
                isPublic = isPublic!!,
                rightAscension = rightAscension!!,
                declination = declination!!
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
        if(isPublic == null)
            errors.put(ErrorTag.PUBLIC, "Required field")
        if (rightAscension == null)
            errors.put(ErrorTag.RIGHT_ASCENSION, "Required field")
        if (declination == null)
            errors.put(ErrorTag.DECLINATION, "Required field")

        return if (errors.isEmpty) null else errors
    }
}