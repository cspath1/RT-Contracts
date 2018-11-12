package com.radiotelescope.controller.model.appointment

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.ListBetweenDates
import com.radiotelescope.controller.model.BaseForm
import java.util.*

/**
 * ListBetweenDates form that takes nullable versions of the [ListBetweenDates.Request] object.
 * It is in charge of making sure these values are not null before adapting it
 * to a [ListBetweenDates.Request] object
 *
 * @param startTime the Appointment start time
 * @param endTime the Appointment end time
 */
data class ListBetweenDatesForm(
        val startTime: Date?,
        val endTime: Date?
) : BaseForm<ListBetweenDates.Request> {
    /**
     * Override of the [BaseForm.toRequest] method that
     * adapts the form into a [ListBetweenDates.Request] object
     *
     * @return the [ListBetweenDates.Request] object
     */
    override fun toRequest(): ListBetweenDates.Request {
        return ListBetweenDates.Request(
                startTime = startTime!!,
                endTime = endTime!!,
                telescopeId = -1L
        )
    }

    /**
     * Makes sure the required fields are not null
     *
     * @return a [HashMultimap] of errors or null
     */
    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        if (startTime == null)
            errors.put(ErrorTag.START_TIME, "Required field")
        if (endTime == null)
            errors.put(ErrorTag.END_TIME, "Required field")

        return if (errors.isEmpty) null else errors
    }
}