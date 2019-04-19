package com.radiotelescope.contracts.appointment.update

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseUpdateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.controller.model.Profile
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.heartbeatMonitor.IHeartbeatMonitorRepository
import com.radiotelescope.repository.orientation.IOrientationRepository
import com.radiotelescope.repository.orientation.Orientation
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import java.util.*


/**
 * Command class for editing a Drift Scan Appointment
 *
 * @param request the [Request] object
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param radioTelescopeRepo the [IRadioTelescopeRepository] interface
 * @param userRoleRepo the [IUserRoleRepository] interface
 * @param coordinateRepo the [ICoordinateRepository] interface
 * @param orientationRepo the [IOrientationRepository] interface
 * @param allottedTimeCapRepo the [IAllottedTimeCapRepository] interface
 */
class DriftScanAppointmentUpdate(
        private val request: Request,
        private val appointmentRepo: IAppointmentRepository,
        private val radioTelescopeRepo: IRadioTelescopeRepository,
        private val userRoleRepo: IUserRoleRepository,
        private val coordinateRepo: ICoordinateRepository,
        private val orientationRepo: IOrientationRepository,
        private val allottedTimeCapRepo: IAllottedTimeCapRepository,
        private val heartbeatMonitorRepo: IHeartbeatMonitorRepository,
        private val profile: Profile
): Command<Long, Multimap<ErrorTag, String>>, AppointmentUpdate {
    /**
     * Override of the [Command.execute] method. Calls the [validateRequest] method
     * that will handle all constraint checking and validation.
     *
     * If validation passes, it will update and persist the [Appointment] object and
     * return the id in the [SimpleResult] object.
     *
     * If validation fails, it will return a [SimpleResult] with the errors
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val appointment = appointmentRepo.findById(request.id).get()
            val updatedAppointment = handleEntityUpdate(appointment, request)
            return SimpleResult(updatedAppointment.id, null)
        }
    }

    /**
     * Private method used to handle updating the entity. In the event that
     * the entity being changed has had its typed changed, the current record
     * will be deleted, and a new one will be persisted in its place.
     *
     * If it has not changed, the entity can be updated normally.
     *
     * @param appointment current [Appointment]
     * @param request the [Request]
     * @return the updated (or new) [Appointment]
     */
    private fun handleEntityUpdate(
            appointment: Appointment,
            request: Request
    ): Appointment {
        // If the type is the same, the entity can be updated
        if (appointment.type == Appointment.Type.DRIFT_SCAN) {
            return appointmentRepo.save(request.updateEntity(appointment))
        } else {
            // The type is being changed, and we must delete the
            // old record and persist a new one
            val theUser = appointment.user
            val theStatus = appointment.status

            deleteAppointment(
                    appointment = appointment,
                    appointmentRepo = appointmentRepo,
                    coordinateRepo = coordinateRepo,
                    orientationRepo = orientationRepo
            )
            val theAppointment = request.toEntity()

            val theOrientation = request.toOrientation()
            orientationRepo.save(theOrientation)

            theAppointment.user = theUser
            theAppointment.status = theStatus

            theAppointment.orientation = theOrientation

            appointmentRepo.save(theAppointment)

            return theAppointment
        }
    }

    /**
     * Method responsible for constraint checking and validations for the appointment
     * update request. Calls the [baseRequestValidation] to handle validation common
     * to all [Appointment] update commands. From there, it will check to make sure
     * the elevation and azimuth are all valid.
     *
     * If all of these fields pass validation, it will call the [validateAvailableAllottedTime]
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
            if(elevation < 0 || elevation > 90)
                errors.put(ErrorTag.ELEVATION, "Elevation must be between 0 and 90")
            if(azimuth < 0 || azimuth >= 360)
                errors.put(ErrorTag.AZIMUTH, "Azimuth must be between 0 and 359")
        }

        if (!errors.isEmpty)
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
     * Data class containing all fields necessary for drift scan appointment update. Implements the
     * [BaseUpdateRequest] interface and overrides the [BaseUpdateRequest.updateEntity]
     * method
     *
     * @param elevation the Elevation
     * @param azimuth the Azimuth
     */
    data class Request(
            override var id: Long,
            override val telescopeId: Long,
            override val startTime: Date,
            override val endTime: Date,
            override val isPublic: Boolean,
            val elevation: Double,
            val azimuth: Double
    ): AppointmentUpdate.Request() {
        /**
         * Override of the [BaseUpdateRequest.updateEntity] method that
         * takes an [Appointment] and will update all of its values to the
         * values in the request
         */
        override fun updateEntity(entity: Appointment): Appointment {
            entity.telescopeId = telescopeId
            entity.startTime = startTime
            entity.endTime = endTime
            entity.isPublic = isPublic
            entity.orientation!!.elevation = elevation
            entity.orientation!!.azimuth = azimuth

            return entity
        }

        /**
         * Method used when changing appointment types, since when the type
         * changes, the existing record is deleted and a new one is persisted.
         *
         * @return a new [Appointment] record
         */
        fun toEntity(): Appointment {
            return Appointment(
                    startTime = startTime,
                    endTime = endTime,
                    telescopeId = telescopeId,
                    isPublic = isPublic,
                    type = Appointment.Type.DRIFT_SCAN
            )
        }

        /**
         * Method used when changing appointment types, since when the type
         * changes, the existing record is deleted and a new one is persisted.
         * Returns a new [Orientation] record
         *
         * @return a new [Orientation] record
         */
        fun toOrientation(): Orientation {
            return Orientation(
                    azimuth = azimuth,
                    elevation = elevation
            )
        }
    }
}