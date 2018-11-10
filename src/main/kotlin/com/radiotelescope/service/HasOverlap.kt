package com.radiotelescope.service

import com.radiotelescope.contracts.appointment.Create
import com.radiotelescope.contracts.appointment.Update
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository

/**
 *
 * Class that contains the method to check for appointment overlaps when an Update.Request is generated
 * @param an [IAppointmentRepository] on which to call selectAppointmentsWithinPotentialAppointmentTimeRange
 */

class HasOverlap(val appointmentRepo:IAppointmentRepository)
{
    /**
     * Method checks if there are any conflicts when re-scheduling an appointment.
     *
     * @param the [Update.Request], containing the details of the potential appointment
     * @return a [Boolean], true if conflict, false if no conflict
     */

    fun hasOverlapUpdate(request: Update.Request):List<Appointment> {
    val listAppts:List<Appointment> = appointmentRepo.selectAppointmentsWithinPotentialAppointmentTimeRange(request.endTime, request.startTime, request.telescopeId)
        return listAppts
}
    /**
     * Method checks if there are any conflicts when re-scheduling an appointment.
     *
     * @param the [Create.Request], containing the details of the potential appointment
     * @return a [Boolean], true if conflict, false if no conflict
     */
   fun hasOverlapCreate(request: Create.Request): List<Appointment> {
        val listAppts: List<Appointment> = appointmentRepo.selectAppointmentsWithinPotentialAppointmentTimeRange(request.endTime, request.startTime, request.telescopeId)
        return listAppts
    }
}