package com.radiotelescope.contracts.appointment.update

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.controller.model.Profile
import com.radiotelescope.isNotEmpty
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.heartbeatMonitor.IHeartbeatMonitorRepository
import com.radiotelescope.repository.orientation.IOrientationRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import java.util.*

/**
 * Command class for editing a Celestial Body Appointment
 *
 * @param request the [Request] object
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param radioTelescopeRepo the [IRadioTelescopeRepository] interface
 * @param userRoleRepo the [IUserRoleRepository] interface
 * @param coordinateRepo the [ICoordinateRepository] interface
 * @param orientationRepo the [IOrientationRepository] interface
 * @param celestialBodyRepo the [ICelestialBodyRepository] interface
 * @param allottedTimeCapRepo the [IAllottedTimeCapRepository] interface
 * @param heartbeatMonitorRepo the [IHeartbeatMonitorRepository] interface
 * @param profile the application's profile
 */
class CelestialBodyAppointmentUpdate(
        private val request: Request,
        private val appointmentRepo: IAppointmentRepository,
        private val radioTelescopeRepo: IRadioTelescopeRepository,
        private val userRoleRepo: IUserRoleRepository,
        private val coordinateRepo: ICoordinateRepository,
        private val orientationRepo: IOrientationRepository,
        private val celestialBodyRepo: ICelestialBodyRepository,
        private val allottedTimeCapRepo: IAllottedTimeCapRepository,
        private val heartbeatMonitorRepo: IHeartbeatMonitorRepository,
        private val profile: Profile
) : Command<Long, Multimap<ErrorTag, String>>, AppointmentUpdate {
    /**
     * Override of the [Command.execute] method. Calls the [validateRequest] method
     * that will handle all constraint checking and validation.
     *
     * If validation passes, it will update and persist the [Appointment] object and
     * return the id in the [SimpleResult] object.
     *
     * If validation fails, it will return a [SimpleResult] with the errors.
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val theAppointment = appointmentRepo.findById(request.id).get()
            val updatedAppointment = handleEntityUpdate(theAppointment, request)

            return SimpleResult(updatedAppointment.id, null)
        }
    }

    /**
     * Method responsible for constraint checking and validations for the appointment
     * update request. Calls the [baseRequestValidation] to handle validation common to
     * all [Appointment] update commands. From there it will make sure the celestial body
     * id refers to an existing record.
     *
     * From there, if no validation has failed thus far, it will call the [validateAvailableAllottedTime]
     * method to check if the user has enough time for the appointment.
     *
     * @return a [HashMultimap] of errors or null
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        baseRequestValidation(
                request = request,
                radioTelescopeRepo = radioTelescopeRepo,
                appointmentRepo = appointmentRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = profile
        )?.let { return it }

        var errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if (!celestialBodyRepo.existsById(celestialBodyId))
                errors.put(ErrorTag.CELESTIAL_BODY, "Celestial Body #$celestialBodyId could not be found")
        }

        if (errors.isNotEmpty())
            return errors

        errors = validateAvailableAllottedTime(
                request = request,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )

        return if (errors.isEmpty) null else errors
    }

    /**
     * Private method used to handle updating the entity. In the event that
     * the entity that is being changed has had its type changed, the current
     * record will be deleted, and a new one will be persisted in its place.
     *
     * If it has not changed, the entity can be updated normally.
     *
     * @param appointment the current [Appointment]
     * @param request the [Request]
     * @return the updated (or new) [Appointment]
     */
    private fun handleEntityUpdate(
            appointment: Appointment,
            request: Request
    ): Appointment {
        // If the type is the same, the entity can be updated
        if (appointment.type == Appointment.Type.CELESTIAL_BODY) {
            val updatedAppointment = request.updateEntity(appointment)
            updatedAppointment.celestialBody = celestialBodyRepo.findById(request.celestialBodyId).get()
            return appointmentRepo.save(updatedAppointment)
        } else {
            // The type is being changed, and we must delete the old
            // record and persist a new one
            val theUser = appointment.user
            val theStatus = appointment.status

            deleteAppointment(
                    appointment = appointment,
                    appointmentRepo = appointmentRepo,
                    coordinateRepo = coordinateRepo,
                    orientationRepo = orientationRepo
            )
            val theAppointment = request.toEntity()
            theAppointment.user = theUser
            theAppointment.status = theStatus

            // "Celestial Body" Appointments will have a Celestial Body
            theAppointment.celestialBody = celestialBodyRepo.findById(request.celestialBodyId).get()
            return appointmentRepo.save(theAppointment)
        }
    }

    /**
     * Data class containing fields necessary for celestial body appointment update.
     * Implements the [AppointmentUpdate.Request] abstract class.
     *
     * @param celestialBodyId the Celestial Body id
     */
    data class Request(
            override var id: Long,
            override val telescopeId: Long,
            override val startTime: Date,
            override val endTime: Date,
            override val isPublic: Boolean,
            override val priority: Appointment.Priority,
            val celestialBodyId: Long
    ): AppointmentUpdate.Request() {
        /**
         * Concrete implementation of the [AppointmentUpdate.Request.updateEntity]
         * method that takes an [Appointment] and will update all of its values
         * to the values in the request
         *
         * @param entity the [Appointment]
         * @return an updated [Appointment] object
         */
        override fun updateEntity(entity: Appointment): Appointment {
            entity.telescopeId = telescopeId
            entity.startTime = startTime
            entity.endTime = endTime
            entity.isPublic = isPublic
            entity.priority = priority

            return entity
        }

        /**
         * Method used when changing appointment types, since when the
         * type changes, the existing record is deleted and a new one
         * is persisted.
         *
         * @return a new [Appointment] record
         */
        fun toEntity(): Appointment {
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