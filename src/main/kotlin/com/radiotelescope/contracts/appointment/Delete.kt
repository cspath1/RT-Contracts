package com.radiotelescope.contracts.appointment

import com.radiotelescope.contracts.Command

import com.radiotelescope.repository.user.IUserRepository
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.User
import org.springframework.data.jpa.repository.Query

//Delete an appointment
class Delete
(private var appt: Appointment,
 private val apptRepo: IAppointmentRepository,
 private val apptId: Long, //id of the appointment
private val user:User //for findByUser


): Command<Long, Multimap<ErrorTag, String>>
{
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {

        //failure case-- appointment is not there to be deleted
        if (!apptRepo.existsById(apptId)) {
            val errors = HashMultimap.create<ErrorTag, String>()
            errors.put(ErrorTag.ID, "appt with ID $apptId does not exist (attempted deletion)")

            return SimpleResult(null, errors)

        } else //success case
        {
           appt = apptRepo.findByApptId(apptId)
            //the delete method actually cancels the appointment
            apptRepo.delete(appt)

            return SimpleResult(apptId, null)

        }

       // @Query("select first_name from users")
      //  public LfindByFirstName ();


    }
}