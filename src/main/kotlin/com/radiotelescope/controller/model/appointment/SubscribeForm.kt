package com.radiotelescope.controller.model.appointment

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.SubscribeAppointment
import com.radiotelescope.controller.model.BaseForm


data class SubscribeForm(
        val appointmentId: Long?,
        val userId: Long?

) :BaseForm<SubscribeAppointment.Request> {
    override fun toRequest(): SubscribeAppointment.Request {
        return SubscribeAppointment.Request(
                appointmentId = appointmentId!!,
                userId = userId!!
        )
    }

    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag,String>()

        if (appointmentId == null)
            errors.put(ErrorTag.ID, "Invalid appointment ID")
        if (userId == null)
            errors.put(ErrorTag.USER_ID, "Invalid user ID")

        return if (errors.isEmpty) null else errors
    }
}