package com.radiotelescope.contracts.appointment

import com.radiotelescope.contracts.Command
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository

//Delete an appointment
class Delete
(private var apptId:Long,
 private val apptRepo: IAppointmentRepository
): Command<Long, Multimap<ErrorTag, String>>
{
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        //Failure case: Appointment does not exist
        if (!apptRepo.existsById(apptId)) {
            val errors = HashMultimap.create<ErrorTag, String>()
            errors.put(ErrorTag.ID, "appt with ID ${apptId} does not exist (attempted deletion)")
            return SimpleResult(null, errors)
        }
        //Success case: Found appointment to delete
        else
        {
            val appt:Appointment = apptRepo.findById(apptId).get()
            apptRepo.delete(appt)
            return SimpleResult(apptId, null)
        }
    }
}