package com.radiotelescope.service

import com.radiotelescope.contracts.appointment.Update
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository


public class HasOverlapUpdate(val appointmentRepo:IAppointmentRepository)
{

        public fun hasOverlap(request: Update.Request):Boolean
{
    var isOverlap = false
    val listAppts:List<Appointment> = appointmentRepo.selectAppointmentsWithinPotentialAppointmentTimeRange(request.endTime, request.startTime, request.telescopeId)
    val zero:Long = 0
    if (!listAppts.isEmpty())
    { //if it's not empty,  there IS an overlap, cannot schedule
        isOverlap = true

        println("size of listAppts: " + listAppts.size)

        for (a in listAppts)
        {
            println("isOverlap is true; user id of the user who scheduled the appointment already in the table: " + a.id)
            println("isOverlap is true; user id of the user who requested a new appointment: " + request.id)

            println("isOverlap is true; start time of the appointment already in the table : " + a.startTime)
            println("isOverlap is true;  end time of the appointment already in the table : " + a.endTime)

            println("isOverlap is true; start time of the appointment that was to be scheduled: " + request.startTime)
            println("isOverlap is true; end time of the appointment that was to be scheduled: " + request.endTime)

            println("startTime of appointment in index 0 of listAppts: " + listAppts.get(0).startTime)
        }



    }
    else if(listAppts.isEmpty())
    {
    //if it IS empty, no overlap, can schedule,
        isOverlap = false

    }

    return isOverlap
}
}