package com.radiotelescope.service

import com.radiotelescope.contracts.appointment.Update
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository


public class HasOverlapUpdate(val appointmentRepo:IAppointmentRepository
                              )
{

        public fun hasOverlap(request: Update.Request):Boolean
{
    var isOverlap = false
    val listAppts:List<Appointment> = appointmentRepo.selectAppointmentsWithinPotentialAppointmentTimeRange(request.endTime, request.startTime, request.telescopeId)
    val one:Long = 1L
    if (!listAppts.isEmpty())
    { //if it's not empty,  there IS an overlap, cannot schedule
        isOverlap = true

      //  println("size of listAppts: " + listAppts.size)



        for (a in listAppts)
        {

            if (listAppts.size == 1 && a.id == request.id)
            {
                isOverlap = false
                break
            }

            /*
            println("appt id of a:" + a.id)
            println("lastIndex in listAppts: " + listAppts.lastIndex)
            println("isOverlap is true; appt id of the user who scheduled the appointment already in the table: " + a.id)

            //      println("isOverlap is true; user id of the user who requested a new appointment: " + request.id)

            println("isOverlap is true; start time of the appointment already in the table : " + a.startTime)
            println("isOverlap is true;  end time of the appointment already in the table : " + a.endTime)

            println("isOverlap is true; start time of the appointment that was to be scheduled: " + request.startTime)
            println("isOverlap is true; end time of the appointment that was to be scheduled: " + request.endTime)

            println("startTime of appointment in index 0 of listAppts: " + listAppts.get(0).startTime)
*/
            //if the ONLY appointment that conflicts with the requested appt is ITSELF, then no conflict



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