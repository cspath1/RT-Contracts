package com.radiotelescope.contracts.appointment

import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.user.User
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.appointment.Appointment
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import java.util.*

//Create Appointment
class Create(
    private val request: Request,
    private val appointmentRepo: IAppointmentRepository
) : Command<Long, Multimap<ErrorTag,String>>
{
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        val errors = validateRequest()
        if (!errors.isEmpty) {
            return SimpleResult(null, errors)
        } else {
            val newAppointment = appointmentRepo.save(request.toEntity())
            return SimpleResult(newAppointment.id, null)
        }
    }

    private fun validateRequest(): Multimap<ErrorTag, String>
    {
        val errors = HashMultimap.create<ErrorTag,String>()
        with(request){
            /**
             * TODO : add validation checking
             */

            if (startTime.toString().isBlank()) {
                errors.put(ErrorTag.START_TIME, "Start time may not be blank")
            }
            if (endTime.toString().isBlank()) {
                errors.put(ErrorTag.END_TIME, "End time may not be blank")
            }
            if (receiver.isBlank())
                errors.put(ErrorTag.RECEIVER, "Receiver may not be blank")

            //If the start time > end time (in regards to the new appointment itself), we cause an error.
            if (startTime.after(endTime))
                errors.put(ErrorTag.START_TIME, "Start time may not be greater than end time")

            //If the startTime is before the current date and time, we cannot schedule it
            if (startTime.before(Date()))
                errors.put(ErrorTag.DATE, "Date of appointment may not be before current date" )

            //Conflict scheduling avoidance algorithm here
        }

      return errors
    }


    data class Request(
            val user: User,
    //        val orientation: Orientation, //to implement
    //        val celestialBody: CelestialBody,
            val type: String,
            val startTime: Date,
            val endTime: Date,
            val telescopeId: Long,
            val celestialBodyId: Long,
            val receiver: String,
            val isPublic: Boolean,
            val userId: Long,
            val uFirstName: String,
            val uLastName: String,
            val apptId: Long,
            val status: Appointment.Status,
            val state: Int
    ) : BaseCreateRequest<Appointment>{
        override fun toEntity(): Appointment {
            return Appointment(
                    user= user,
         //           orientation = orientation,
         //           celestialBody = celestialBody,
                    type = type,
                    startTime = startTime,
                    endTime = endTime,
                    telescopeId = telescopeId,
                    celestialBodyId = celestialBodyId,
                    receiver = receiver,
                    isPublic = isPublic,
                    userId = userId,
                    uFirstName = uFirstName,
                    uLastName = uLastName,
                    state = state
                 // status = Appointment.Status.Scheduled //doesn't like this?
            )
        }
    }
}
