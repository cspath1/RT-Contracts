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
        val zero: Long = 0
        //if at least one appt returned, there IS an overlap

        println("size of listAppts: " + listAppts.size)

        if (!listAppts.isEmpty()) {
            isOverlap = true

            for (a in listAppts)
            {


                println("appt id of a:" + a.id)
                println("lastIndex in listAppts: " + listAppts.lastIndex)
                println("isOverlap is true; appt id of the user who scheduled the appointment already in the table: " + a.id)

                //      println("isOverlap is true; user id of the user who requested a new appointment: " + request.id)

                println("isOverlap is true; start time of the appointment already in the table : " + a.startTime)
                println("isOverlap is true;  end time of the appointment already in the table : " + a.endTime)

                println("isOverlap is true; start time of the appointment that was to be scheduled: " + request.startTime)
                println("isOverlap is true; end time of the appointment that was to be scheduled: " + request.endTime)

                println("startTime of appointment in index 0 of listAppts: " + listAppts.get(0).startTime)


            }



        }

        else
        {



        }

        return isOverlap

    }
}