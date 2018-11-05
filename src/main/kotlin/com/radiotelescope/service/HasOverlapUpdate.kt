package com.radiotelescope.service

import com.radiotelescope.contracts.appointment.Update
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository


public class HasOverlapUpdate(val appointmentRepo:IAppointmentRepository)
{
    public fun hasOverlap(request: Update.Request):Boolean {
    var isOverlap = false
    val listAppts:List<Appointment> = appointmentRepo.selectAppointmentsWithinPotentialAppointmentTimeRange(request.endTime, request.startTime, request.telescopeId)
    if (!listAppts.isEmpty())
    { //if it's not empty,  there IS an overlap, cannot schedule
        isOverlap = true
        for (a in listAppts)
        {
            //if the ONLY appointment that conflicts with the requested appt is ITSELF, then no conflict
            if (listAppts.size == 1 && a.id == request.id)
            {
                isOverlap = false
                break
            }
        }
    }

    return isOverlap
}
}