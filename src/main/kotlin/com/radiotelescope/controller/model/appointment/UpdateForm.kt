package com.radiotelescope.controller.model.appointment

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.Update
import com.radiotelescope.controller.model.BaseForm
import java.util.*

data class UpdateForm (
        val appointmentId: Long?,
        val startTime: Date?,
        val endTime: Date?,
        val telescopeId: Long?,
        val isPublic: Boolean?
) : BaseForm<Update.Request> {
    override fun toRequest(): Update.Request {
        return Update.Request(
                id = appointmentId!!,
                startTime = startTime!!,
                endTime = endTime!!,
                telescopeId = telescopeId!!,
                isPublic = isPublic!!
        )
    }

    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        if (appointmentId == null)
            errors.put(ErrorTag.ID, "Invalid appointment id")
        if (startTime == null)
            errors.put(ErrorTag.START_TIME, "Required field")
        if (endTime == null)
            errors.put(ErrorTag.END_TIME, "Required field")
        if (telescopeId == null)
            errors.put(ErrorTag.TELESCOPE_ID, "Required field")
        if(isPublic == null)
            errors.put(ErrorTag.PUBLIC, "Required field")

        return if (errors.isEmpty) null else errors
    }
}