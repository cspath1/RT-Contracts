package com.radiotelescope.service

import com.radiotelescope.contracts.appointment.Create
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository

public class HasOverlapCreate(
        val appointmentRepo:IAppointmentRepository
)
{
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