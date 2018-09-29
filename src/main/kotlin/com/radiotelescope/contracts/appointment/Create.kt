package com.radiotelescope.contracts.appointment


import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.IUserRepository
import com.radiotelescope.repository.User
import com.radiotelescope.repository.IAppointmentRepository
import com.radiotelescope.repository.Appointment
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap


class Create(
    private val request: Request,
    private val responder: CreateResponder<ErrorTag>,
    private val userRepo: IUserRepository,
    private val appointmentRepo: IAppointmentRepository
) : Command{
    override fun execute(){
        val errors = valideRequest()
        if (!errors.isEmpty) {
            responder.onFailure(errors)
        } else {
            val newAppointment = appointmentRepo.save(request.toEntity())
        }
    }

    private fun validateRequest(): Multimap<ErrorTag, String>{
        val errors = HashMultimap.create<errorTag,String>()
        with(request){
            /**
             * TODO : add validation checking
             */
            if (startTime.isBlank())
                errors.put(ErrorTag.START_TIME, "Start time may not be blank")
            if (endTime.isBlank())
                errors.put(ErrorTag.END_TIME, "End time may not be blank")
            if (receiver.isBlank())
                errors.put(ErrorTag.RECEIVER, "Receiver may not be blank")

        }

        return errors
    }


    data class Request(
            val userId: Long,
            val type: String,
            val startTime: String,
            val endTime: String,
            val telescopeId: Int,
            val celestialBodyId: Int,
            val coordinates: Int,
            val receiver: String,
            val public: Boolean
    ) : BaseCreateRequest<Appointment>{
        override fun toEntity(): Appointment {
            return Appointment(
                    userId = userId,
                    type = type,
                    startTime = startTime,
                    endTime = endTime,
                    telescopeid = telescopeId,
                    celestialBodyId = celestialBodyId,
                    coordinates = coordinates,
                    receiver = receiver,
                    public = public
            )
        }
    }
}
