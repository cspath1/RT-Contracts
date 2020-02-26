package com.radiotelescope.contracts.appointment.update

import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.appointment.Appointment
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseUpdateRequest
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.orientation.IOrientationRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import java.util.*

/**
 * Command class for editing a Coordinate (Point) Appointment
 *
 * @param request the [Request] object
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param radioTelescopeRepo the [IRadioTelescopeRepository] interface
 * @param userRoleRepo the [IUserRoleRepository] interface
 * @param coordinateRepo the [ICoordinateRepository] interface
 * @param orientationRepo the [IOrientationRepository] interface
 * @param allottedTimeCapRepo the [IAllottedTimeCapRepository] interface
 */
class CoordinateAppointmentUpdate(
        private val request: Request,
        private val appointmentRepo: IAppointmentRepository,
        private val radioTelescopeRepo: IRadioTelescopeRepository,
        private val userRoleRepo: IUserRoleRepository,
        private val coordinateRepo: ICoordinateRepository,
        private val orientationRepo: IOrientationRepository,
        private val allottedTimeCapRepo: IAllottedTimeCapRepository
):  Command<Long, Multimap<ErrorTag,String>>, AppointmentUpdate {
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
     * Method responsible for constraint checking and validations for the appointment
     * update request. Calls the [baseRequestValidation] to handle validation common
     * to all [Appointment] update commands. From there, it will check to make sure
     * the hours, minutes, seconds, and declination are all valid.
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
                allottedTimeCapRepo = allottedTimeCapRepo
        )?.let { return it }

        var errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if (hours < 0 || hours >= 24)
                errors.put(ErrorTag.HOURS, "Hours must be between 0 and 24")
            if (minutes < 0 || minutes >= 60)
                errors.put(ErrorTag.MINUTES, "Minutes must be between 0 and 60")
            if (declination > 90 || declination < -90)
                errors.put(ErrorTag.DECLINATION, "Declination must be between -90 - 90")
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
        if (appointment.type == Appointment.Type.POINT) {
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

            val theCoordinate = request.toCoordinate()
            coordinateRepo.save(theCoordinate)

            theAppointment.user = theUser
            theAppointment.status = theStatus

            // "Point" Appointments will have a single Coordinate
            theAppointment.coordinateList = arrayListOf(theCoordinate)
            appointmentRepo.save(theAppointment)

            theCoordinate.appointment = theAppointment
            coordinateRepo.save(theCoordinate)

            return theAppointment
        }
    }

    /**
     * Data class containing all fields necessary for coordinate appointment update. Implements the
     * [BaseUpdateRequest] interface and overrides the [BaseUpdateRequest.updateEntity]
     * method
     *
     * @param hours the Right Ascension hours
     * @param minutes the Right Ascension minutes
     * @param declination the Declination
     */
    data class Request(
            override var id: Long,
            override val telescopeId: Long,
            override val startTime: Date,
            override val endTime: Date,
            override val isPublic: Boolean,
            override val priority: Appointment.Priority,
            val hours: Int,
            val minutes: Int,
            val declination: Double
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
            entity.priority = priority

            entity.coordinateList[0].hours = hours
            entity.coordinateList[0].minutes = minutes
            entity.coordinateList[0].declination = declination
            entity.coordinateList[0].rightAscension = Coordinate.hoursMinutesToDegrees(
                    hours = hours,
                    minutes = minutes
            )

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
                    priority = priority,
                    type = Appointment.Type.POINT
            )
        }

        /**
         * Method used when changing appointment types, since when the type
         * changes, the existing record is deleted and a new one is persisted.
         * Returns a new [Coordinate] record
         *
         * @return a new [Coordinate] record
         */
        fun toCoordinate(): Coordinate {
            return Coordinate(
                    hours = hours,
                    minutes = minutes,
                    rightAscension = Coordinate.hoursMinutesToDegrees(
                            hours = hours,
                            minutes = minutes
                    ),
                    declination = declination
            )
        }
    }
}