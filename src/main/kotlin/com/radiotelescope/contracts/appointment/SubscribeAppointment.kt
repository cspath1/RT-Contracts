package com.radiotelescope.contracts.appointment

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.subscribedAppointment.ISubscribedAppointmentRepository
import com.radiotelescope.repository.subscribedAppointment.subscribedAppointment

class SubscribeAppointment (
        private val request: Request,
        private val appointmentRepo: IAppointmentRepository,
        private val subscribedAppointmentRepo: ISubscribedAppointmentRepository
): Command<Long, Multimap<ErrorTag, String>> {


    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val subscribedAppointment = request.toEntity()

            subscribedAppointmentRepo.save(subscribedAppointment)

            return SimpleResult(subscribedAppointment.id, null)
        }
    }

    private fun validateRequest(): Multimap<ErrorTag, String>? {
        var errors = HashMultimap.create<ErrorTag,String>()
        with(request){
            if (!appointmentRepo.existsById(appointmentId)){
                errors.put(ErrorTag.ID, "Appointment #$appointmentId could not be found")
            }

            if (!errors.isEmpty)
                return errors
        }
        return if (errors.isEmpty)
            null
        else
            errors
    }

    data class Request(
            val appointmentId: Long
    ) : BaseCreateRequest<subscribedAppointment>{
        override fun toEntity(): subscribedAppointment {
            return subscribedAppointment(
                    appointmentId = appointmentId
            )
        }
    }

}