package com.radiotelescope.contracts.appointment

import com.radiotelescope.contracts.Command
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository

//Cancel an appointment
class Cancel
(private var apptId:Long,
 private val apptRepo: IAppointmentRepository
): Command<Long, Multimap<ErrorTag, String>>
{
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        //Failure case: Appointment does not exist
        val errors = HashMultimap.create<ErrorTag, String>()
        if (!apptRepo.existsById(apptId))
        {
            errors.put(ErrorTag.ID, "appt with ID ${apptId} does not exist (attempted cancellation)")
            return SimpleResult(null, errors)
        }
        //Success case: Found appointment to cancel
        else
        {
            val appt:Appointment = apptRepo.findById(apptId).get()

            if (appt.status != Appointment.Status.Canceled)
            appt.status = Appointment.Status.Canceled
            else {
                errors.put(ErrorTag.STATUS, "Cannot cancel an already canceled appointment: appointment id is ${apptId}")
                return SimpleResult(null, errors)
            }

            apptRepo.save(appt)
            return SimpleResult(apptId, null)
        }
    }
}