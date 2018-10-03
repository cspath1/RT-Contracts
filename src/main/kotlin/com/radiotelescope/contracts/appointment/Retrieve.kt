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

//To get Appointment(s) from the db

class Retrieve(
   private var a: Appointment,
   private var aI: AppointmentInfo,
   private val aRepo: IAppointmentRepository,
   private val aId: Long

): Command<Long, Multimap<ErrorTag,String>>
{
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {

        val errors = HashMultimap.create<ErrorTag, String>()

        //retrieve one appt by aId, retrieve all appts by uId

        //if we're trying to get an appt that does not exist


        if (!aRepo.existsById(aId)) {
            errors.put(ErrorTag.ID, "Attempted to retrieve appointment with id $aId that does not exist")
            //failure
            return SimpleResult(null, errors)
        }
        //if it does exist
        else {
            a = aRepo.findById(aId).get()
            //See if I can get secondary constructor here (just pass in appt)
            aI = AppointmentInfo(a, a.user, a.type, a.startTime, a.endTime, a.telescopeId, a.celestialBodyId, a.receiver, a.isPublic, a.date, a.assocUserId, a.uFirstName,
                    a.uLastName, a.id, a.status, a.state)

            //Is save the correct method to use for a retrieve?
            aRepo.save(a)

            //success
            return SimpleResult(a.id, null)
        }









    }



}