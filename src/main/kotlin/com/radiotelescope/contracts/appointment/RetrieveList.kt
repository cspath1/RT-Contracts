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

class RetrieveList(
       private val aRepo: IAppointmentRepository,
        private val user:User,
        private val userRepo: IUserRepository


):Command<Long, Multimap<ErrorTag,String>> {
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        val errors = HashMultimap.create<ErrorTag, String>()

        //if an appointment is attempted to be retrieved that does not exist, then we have errors

        //check if the user exists
        if (!userRepo.existsById(user.id)) {
            errors.put(ErrorTag.USER_ID, "User with id ${user.id} does not exist")
            return SimpleResult(user.id, errors)
        } else {

           val aL = userRepo.findByUser()

            //val aRepo: IAppointmentRepository
       //     IAppointmentRepository()

            for (a: Appointment in aL) {
                //Again, use secondary constructor
              val aI = AppointmentInfo(a, a.user, a.type, a.startTime, a.endTime, a.telescopeId, a.celestialBodyId, a.receiver, a.isPublic, a.date, a.assocUserId, a.uFirstName,
                        a.uLastName, a.id, a.status, a.state)

                //This right?
             val aa:Appointment = aRepo.save(a)

            }
//If everything is successful:
// last index of the final appointment in the list as first param?
            return SimpleResult(aL.lastIndex.toLong(), null)

        }

    }
}