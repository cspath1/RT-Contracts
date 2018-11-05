package com.radiotelescope.service

import com.radiotelescope.contracts.appointment.Create
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository

/**
 *
 * Class that contains the method to check for appointment overlaps when a Create.Request is generated
 * @param an [IAppointmentRepository] on which to call selectAppointmentsWithinPotentialAppointmentTimeRange
 */

public class HasOverlapCreate(
        val appointmentRepo:IAppointmentRepository
)
{

    /**
     * Method checks if there are any conflicts when scheduling a new appointment.
     *
     * @param the [Create.Request], containing the details of the potential appointment
     * @return a [Boolean], true if conflict, false if no conflict
     */
    public fun hasOverlap(
            request: Create.Request
    ): Boolean {
        var isOverlap = false
        val listAppts: List<Appointment> = appointmentRepo.selectAppointmentsWithinPotentialAppointmentTimeRange(request.endTime, request.startTime, request.telescopeId)
        //if at least one appt returned, there IS an overlap
        if (!listAppts.isEmpty()) {
            isOverlap = true
        }
        return isOverlap
    }
}