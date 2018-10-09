package com.radiotelescope.contracts.appointment

import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.appointment.Appointment
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult

//This class retrieves one specific appointment, by its appointment ID.
class Retrieve(
   private var appt: Appointment,
   private var apptInfo: AppointmentInfo,
   private val apptRepo: IAppointmentRepository,
   private val appt_id: Long
): Command<Long, Multimap<ErrorTag,String>>
{
        override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        val errors = HashMultimap.create<ErrorTag, String>()
        //Failure case
        if (!apptRepo.existsById(appt_id)) {
            errors.put(ErrorTag.ID, "Attempted to retrieve appointment with id $appt_id that does not exist")
            return SimpleResult(null, errors)
        }
        //Success case
        else {
            appt = apptRepo.findById(appt_id).get()
            apptInfo = AppointmentInfo(appt)
            return SimpleResult(appt_id, null)
        }
    }
}