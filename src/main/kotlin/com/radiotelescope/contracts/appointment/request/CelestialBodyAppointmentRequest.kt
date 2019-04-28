package com.radiotelescope.contracts.appointment.request

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
import java.util.*

/**
 * Override of the [Command] interface method used for requesting a Celestial Body appointment
 *
 * @param request the [Request] object
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param userRepo the [IUserRepository] interface
 * @param radioTelescopeRepo the [IRadioTelescopeRepository] interface
 * @param celestialBodyRepo the [ICelestialBodyRepository] interface
 */
class CelestialBodyAppointmentRequest(
        private val request: Request,
        private val appointmentRepo: IAppointmentRepository,
        private val userRepo: IUserRepository,
        private val radioTelescopeRepo: IRadioTelescopeRepository,
        private val celestialBodyRepo: ICelestialBodyRepository
) : Command<Long, Multimap<ErrorTag, String>>, AppointmentRequest {
    /**
     * Override of the [Command.execute] method. Calls the [validateRequest]
     * method that will handle all constraint checking and validation.
     *
     * If validation passes, it will create and persist the [Appointment] object
     * and return the id in the [SimpleResult] object.
     *
     * If validation fails, it will return a [SimpleResult] with the errors
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val theAppointment = request.toEntity()

            // "Celestial Body" appointments will have a reference to a Celestial Body
            val theCelestialBody = celestialBodyRepo.findById(request.celestialBodyId).get()
            theAppointment.celestialBody = theCelestialBody

            theAppointment.user = userRepo.findById(request.userId).get()

            // Mark as request
            theAppointment.status = Appointment.Status.REQUESTED

            appointmentRepo.save(theAppointment)

            return SimpleResult(theAppointment.id, null)
        }
    }

    /**
     * Method responsible for constraint checking and validations for the request.
     * Calls the [baseRequestValidation] method, which handles validation common to
     * each concrete implementation of the [AppointmentRequest] interface.
     *
     * Specific to the appointment type, it will make sure the celestial body supplied
     * exists.
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        baseRequestValidation(
                request = request,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                appointmentRepo = appointmentRepo
        )?.let { return it }

        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if (!celestialBodyRepo.existsById(celestialBodyId))
                errors.put(ErrorTag.CELESTIAL_BODY, "Celestial Body #$celestialBodyId could not be found")
        }

        return if (errors!!.isEmpty) null else errors
    }

    /**
     * Data class representing all fields necessary for appointment creation.
     * Implements [AppointmentRequest.Request] abstract class
     */
    data class Request(
            override val userId: Long,
            override val startTime: Date,
            override val endTime: Date,
            override val telescopeId: Long,
            override val isPublic: Boolean,
            override val priority: Appointment.Priority,
            val celestialBodyId: Long
    ) : AppointmentRequest.Request() {
        /**
         * Concrete implementation of the [BaseCreateRequest.toEntity] method
         * that returns an Appointment object
         */
        override fun toEntity(): Appointment {
            return Appointment(
                    startTime = startTime,
                    endTime = endTime,
                    telescopeId = telescopeId,
                    isPublic = isPublic,
                    priority = priority,
                    type = Appointment.Type.CELESTIAL_BODY
            )
        }
    }
}