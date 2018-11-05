package com.radiotelescope.service

import com.radiotelescope.contracts.appointment.Update
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository

/**
 *
 * Class that contains the method to check for appointment overlaps when an Update.Request is generated
 * @param an [IAppointmentRepository] on which to call selectAppointmentsWithinPotentialAppointmentTimeRange
 */

public class HasOverlapUpdate(val appointmentRepo:IAppointmentRepository)
{
    /**
     * Method checks if there are any conflicts when re-scheduling an appointment.
     *
     * @param the [Update.Request], containing the details of the potential appointment
     * @return a [Boolean], true if conflict, false if no conflict
     */

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