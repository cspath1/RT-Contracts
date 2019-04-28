package com.radiotelescope.contracts.appointment

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.subscribedAppointment.ISubscribedAppointmentRepository

class UnsubscribeAppointment(
        private val appointmentId: Long,
        private val subscribedAppointmentRepo: ISubscribedAppointmentRepository
) : Command<Long, Multimap<ErrorTag, String>> {

    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            subscribedAppointmentRepo.deleteById(appointmentId)

            return SimpleResult(appointmentId, null)
        }

    }

    private fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (!subscribedAppointmentRepo.existsById(appointmentId)) {
            errors.put(ErrorTag.ID, "Appointment Id #ID not found")
        }

        return if (errors.isEmpty) null else errors
    }
}