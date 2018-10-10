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

//To edit an appt
class Update(private val a_id: Long,
             private val apptRepo: IAppointmentRepository
             ):  Command<Long, Multimap<ErrorTag,String>>
{
override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>>
{
    if (!apptRepo.existsById(a_id)) {
        val errors = HashMultimap.create<ErrorTag, String>()
        errors.put(ErrorTag.ID, "Attempted to update appointment with id {$a_id} that does not exist")
        return SimpleResult(null, errors)
    }

    else {
        var appt: Appointment = apptRepo.findById(a_id).get()
        apptRepo.updateSingleAppointmentTimes(appt.startTime.time, appt.endTime.time, a_id)
        apptRepo.save(appt)
        return SimpleResult(a_id, null)
    }


}




}

