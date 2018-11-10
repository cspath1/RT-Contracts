package com.radiotelescope.controller.model.appointment

import com.radiotelescope.contracts.appointment.ListBetweenDates
import com.radiotelescope.controller.model.BaseForm
import java.util.*

data class ListBetweenDatesForm(
        val startTime: Date?,
        val endTime: Date?,
        val telescopeId: Long?,
        val isPublic: Boolean?
):BaseForm<ListBetweenDates.Request> {
    override fun toRequest(): ListBetweenDates.Request  {
        return ListBetweenDates.Request(
                start_Time = startTime!!,
                end_Time = endTime!!,
                telescope_Id = telescopeId!!,
                isPublic = isPublic!!
        )
    }
}