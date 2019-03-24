package com.radiotelescope.contracts.appointment.create

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
import java.util.*

/**
 * Override of the [Command] interface method used for Celestial Body Appointment creation
 *
 * @param request the [Request] object
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param userRepo the [IUserRepository] interface
 * @param userRoleRepo the [IUserRoleRepository] interface
 * @param telescopeRepo the [ITelescopeRepository] interface
 * @param celestialBodyRepo the [ICelestialBodyRepository] interface
 */
class CelestialBodyAppointmentCreate(
        private val request: Request,
        private val appointmentRepo: IAppointmentRepository,
        private val userRepo: IUserRepository,
        private val userRoleRepo: IUserRoleRepository,
        private val telescopeRepo: ITelescopeRepository,
        private val celestialBodyRepo: ICelestialBodyRepository
) : Command<Long, Multimap<ErrorTag, String>>, AppointmentCreate {
    /**
     * Override of the [Command.execute] method. Calls the [validateRequest] method
     * that will handle all constraint checking and validation.
     *
     * If validation passes, it will create and persist the [Appointment] object and
     * return the id in the [SimpleResult] object.
     *
     * If validation fails, it will return a [SimpleResult] with the errors
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val theAppointment = request.toEntity()

            // "Celestial Body" Appointments will have reference to a Celestial Body
            val theCelestialBody = celestialBodyRepo.findById(request.celestialBodyId).get()
            theAppointment.celestialBody = theCelestialBody

            theAppointment.user = userRepo.findById(request.userId).get()

            appointmentRepo.save(theAppointment)

            return SimpleResult(theAppointment.id, null)
        }
    }

    /**
     * Method responsible for constraint checking and validations for the
     * appointment create request. It will ensure that both the user and telescope
     * id exists and that the appointment's end time is not before its start time.
     * It then ensures that the start time is not before the current date.
     *
     * Specific to the appointment type, it will make sure the celestial body supplied
     * exists.
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        basicValidateRequest(
                request = request,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                appointmentRepo = appointmentRepo
        )?.let { return it }

        var errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if (!celestialBodyRepo.existsById(celestialBodyId))
                errors.put(ErrorTag.CELESTIAL_BODY, "Celestial Body #$celestialBodyId could not be found")
        }

        if (!errors!!.isEmpty)
            return errors

        errors = validateAvailableAllottedTime(
                request = request,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo
        )

        return if (errors.isEmpty) null else errors
    }

    /**
     * Data class representing all fields necessary for appointment creation.
     * Implements [BaseCreateRequest] interface
     */
    data class Request(
            override val userId: Long,
            override val startTime: Date,
            override val endTime: Date,
            override val telescopeId: Long,
            override val isPublic: Boolean,
            val celestialBodyId: Long
    ) : AppointmentCreate.Request() {
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
                    type = Appointment.Type.CELESTIAL_BODY
            )
        }
    }
}