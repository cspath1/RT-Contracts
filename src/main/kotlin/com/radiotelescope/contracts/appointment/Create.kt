package com.radiotelescope.contracts.appointment


import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.appointment.Appointment
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import org.springframework.data.jpa.repository.Query
import java.util.*


//create an appointment
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



    interface getAppts
    {
        @Query("select startTime, endTime from appointment")
        fun getTimesOfAppts():Appointment
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

            //If the start time > end time, we cause an error.
            if (startTime.after(endTime))
                errors.put(ErrorTag.START_TIME, "Start time may not be greater than end time")

            //if that date scheduled is before the current date
            if (date.before(Date()))
                errors.put(ErrorTag.DATE, "Date of appointment may not be before current date" )



            //if someone's trying to schedule an appointment that overlaps an existing scheduled appointment, cause an entry in the errors multimap
            //iterate through all other appointments in the appointment table, and if there's a conflict... enter into errors map

          /*  val aa:getAppts? = null
            aa?.getTimesOfAppts()
*/










        }

      return errors
    }


    data class Request(
            val user: User,
    //        val orientation: Orientation, //to implement-- uncomment once implemented
    //        val celestialBody: CelestialBody,
            val type: String,
            val startTime: Date,
            val endTime: Date,
            val telescopeId: Long,
            val celestialBodyId: Long,
            val receiver: String,
            val isPublic: Boolean,
            val date: Date, //date being the day/month/year of the appt?
            val assocUserId: Long,
            val uFirstName: String,
            val uLastName: String,
            val apptId: Long,
            val status: Appointment.Status,
            val state: Int
    ) : BaseCreateRequest<Appointment>{
        override fun toEntity(): Appointment {
            return Appointment(
                    user= user,
         //           orientation = orientation,  //uncomment once implemented
         //           celestialBody = celestialBody,
                    type = type,
                    startTime = startTime,
                    endTime = endTime,
                    telescopeId = telescopeId,
                    celestialBodyId = celestialBodyId,
                    receiver = receiver,
                    isPublic = isPublic,
                    date = date,
                    assocUserId = assocUserId,
                    uFirstName = uFirstName,
                    uLastName = uLastName,
                 //   status = Appointment.Status.Scheduled //doesn't like this?
                    state = state
            )
        }
    }
}
