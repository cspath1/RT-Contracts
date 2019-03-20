package com.radiotelescope.contracts.appointment.create

import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import java.util.*

interface Create {
    abstract class Request : BaseCreateRequest<Appointment> {
        abstract val userId: Long
        abstract val startTime: Date
        abstract val endTime: Date
        abstract val telescopeId: Long
        abstract val isPublic: Boolean
    }

    fun isOverlap(request: Request, appointmentRepo: IAppointmentRepository): Boolean {
        var isOverlap = false
        val appointmentList = appointmentRepo.findConflict(
                endTime = request.endTime,
                startTime = request.startTime,
                telescopeId = request.telescopeId
        )

        if (!appointmentList.isEmpty()) {
            isOverlap = true
        }

        return isOverlap
    }
}