package com.radiotelescope.controller.model.weatherData

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.weatherData.ListBetweenCreationDates
import com.radiotelescope.contracts.weatherData.ErrorTag
import com.radiotelescope.controller.model.BaseForm
import java.util.*

data class ListBetweenCreationDatesForm(
        val lowerDate: Date?,
        val upperDate: Date?
) : BaseForm<ListBetweenCreationDates.Request> {

    override fun toRequest(): ListBetweenCreationDates.Request {
        return ListBetweenCreationDates.Request(
                lowerDate = lowerDate!!,
                upperDate = upperDate!!
        )
    }

    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (lowerDate == null)
            errors.put(ErrorTag.INSERT_TIMESTAMP, "Required field: Lower Date")
        if (upperDate == null)
            errors.put(ErrorTag.INSERT_TIMESTAMP, "Required field: Upper Date")

        if (!errors.isEmpty)
            return errors

        if (lowerDate!!.after(upperDate))
            errors.put(ErrorTag.INSERT_TIMESTAMP, "Start Time must be after End Time")

        return if (errors.isEmpty) null else errors
    }
}