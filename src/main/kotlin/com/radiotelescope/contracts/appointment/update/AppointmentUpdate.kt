package com.radiotelescope.contracts.appointment.update

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseUpdateRequest
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
import java.util.*

interface AppointmentUpdate {
    abstract class Request : BaseUpdateRequest<Appointment> {
        abstract var id: Long
        abstract val telescopeId: Long
        abstract val startTime: Date
        abstract val endTime: Date
        abstract val isPublic: Boolean
    }

    /**
     * Method responsible for check if the requested appointment
     * conflict with the one that are already scheduled
     */
    fun isOverlap(
            request: Request,
            appointmentRepo: IAppointmentRepository
    ): Boolean {
        var isOverlap = false
        val appointmentList = appointmentRepo.findConflict(
                endTime = request.endTime,
                startTime = request.startTime,
                telescopeId = request.telescopeId
        )

        if (appointmentList.size > 1)
            isOverlap = true
        else if (appointmentList.size == 1 && appointmentList[0].id != request.id)
            isOverlap = true

        return isOverlap
    }

    /**
     * Method responsible for constraint checking and validations for the
     * appointment update request.
     */
    fun baseRequestValidation(
            request: Request,
            telescopeRepo: ITelescopeRepository,
            appointmentRepo: IAppointmentRepository
    ): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if (appointmentRepo.existsById(id)) {
                if(telescopeRepo.existsById(telescopeId)) {
                    if (startTime.before(Date()))
                        errors.put(ErrorTag.START_TIME, "New start time cannot be before the current time")
                    if (endTime.before(startTime) || endTime == startTime)
                        errors.put(ErrorTag.END_TIME, "New end time cannot be less than or equal to the new start time")
                    if (isOverlap(this, appointmentRepo))
                        errors.put(ErrorTag.OVERLAP, "Appointment time is conflicted with another appointment")
                }
            }
        }

        return if (errors.isEmpty) null else errors
    }
}