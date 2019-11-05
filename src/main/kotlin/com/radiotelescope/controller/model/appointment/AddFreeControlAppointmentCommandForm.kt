package com.radiotelescope.controller.model.appointment

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.manual.AddFreeControlAppointmentCommand
import com.radiotelescope.controller.model.BaseForm

data class AddFreeControlAppointmentCommandForm(
        val hours: Int?,
        val minutes: Int?,
        val declination: Double?
) : BaseForm<AddFreeControlAppointmentCommand.Request> {
    override fun toRequest(): AddFreeControlAppointmentCommand.Request {
        return AddFreeControlAppointmentCommand.Request(
                appointmentId = -1L,
                hours = hours!!,
                minutes = minutes!!,
                declination = declination!!
        )
    }

    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (hours == null)
            errors.put(ErrorTag.HOURS, "Required field")
        if (minutes == null)
            errors.put(ErrorTag.MINUTES, "Required field")
        if (declination == null)
            errors.put(ErrorTag.DECLINATION, "Required field")

        return if (errors.isEmpty) null else errors
    }
}