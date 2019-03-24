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

/**
 * Interface containing logic/fields common to all Appointment Request commands
 */
interface AppointmentRequest {
    /**
     * Abstract class containing all fields common to Appointment Request request objects
     *
     * @property userId the User id
     * @property startTime the Appointment's start time
     * @property endTime the Appointment's end time
     * @property telescopeId the Telescope id
     * @property isPublic whether the appointment is public or not
     */
    abstract class Request : BaseCreateRequest<Appointment> {
        abstract val userId: Long
        abstract val startTime: Date
        abstract val endTime: Date
        abstract val telescopeId: Long
        abstract val isPublic: Boolean
    }

    /**
     * Method responsible for constraint checking and validations for the
     * Appointment Request request. It will ensure that both the user and telescope
     * id exist and the appointment's end time and start time are valid.
     *
     * @param request the [Request] object
     * @param userRepo the [IUserRepository] interface
     * @param telescopeRepo the [ITelescopeRepository] interface
     * @param appointmentRepo the [ITelescopeRepository] interface
     * @return a [HashMultimap] of errors or null
     */
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