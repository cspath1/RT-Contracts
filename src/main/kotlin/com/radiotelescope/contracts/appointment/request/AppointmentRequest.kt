package com.radiotelescope.contracts.appointment.request

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
import java.util.*

interface AppointmentRequest {
    abstract class Request : BaseCreateRequest<Appointment> {
        abstract val userId: Long
        abstract val startTime: Date
        abstract val endTime: Date
        abstract val telescopeId: Long
        abstract val isPublic: Boolean
    }

    fun baseRequestValidation(
            request: Request,
            userRepo: IUserRepository,
            telescopeRepo: ITelescopeRepository,
            appointmentRepo: IAppointmentRepository
    ): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        with(request) {
            if (!userRepo.existsById(userId)) {
                errors.put(ErrorTag.USER_ID, "User #$userId could not be found")
                return errors
            }
            if (!telescopeRepo.existsById(telescopeId)) {
                errors.put(ErrorTag.TELESCOPE_ID, "Telescope #$telescopeId could not be found")
                return errors
            }
            if (startTime.after(endTime))
                errors.put(ErrorTag.END_TIME, "Start time must be before end time")
            if (startTime.before(Date()))
                errors.put(ErrorTag.START_TIME, "Start time must be after the current time")
        }

        return if (errors.isEmpty) null else errors
    }
}