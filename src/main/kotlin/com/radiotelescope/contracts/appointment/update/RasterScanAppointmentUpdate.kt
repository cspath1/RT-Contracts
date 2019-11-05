package com.radiotelescope.contracts.appointment.update

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.coordinate.CoordinateRequest
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.orientation.IOrientationRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import java.util.*

/**
 * Command class for editing a Raster Scan Appointment
 *
 * @param request the [Request] object
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param radioTelescopeRepo the [IRadioTelescopeRepository] interface
 * @param userRoleRepo the [IUserRoleRepository] interface
 * @param coordinateRepo the [ICoordinateRepository] interface
 * @param orientationRepo the [IOrientationRepository] interface
 * @param allottedTimeCapRepo the [IAllottedTimeCapRepository] interface
 */
class RasterScanAppointmentUpdate(
        private val request: Request,
        private val appointmentRepo: IAppointmentRepository,
        private val radioTelescopeRepo: IRadioTelescopeRepository,
        private val userRoleRepo: IUserRoleRepository,
        private val coordinateRepo: ICoordinateRepository,
        private val orientationRepo: IOrientationRepository,
        private val allottedTimeCapRepo: IAllottedTimeCapRepository
) : Command<Long, Multimap<ErrorTag, String>>, AppointmentUpdate {
    /**
     * Override of the [Command.execute] method. Calls the [validateRequest] method
     * that will handle all constraint checking and validation.
     *
     * If validation passes, it will updated and persist the [Appointment] object
     * and return the id in the [SimpleResult] object.
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
     * update request. Calls the [baseRequestValidation] method to handle validation
     * common to all [Appointment] update commands. From there, it will make sure each
     * of the [CoordinateRequest] objects contains valid information, and there are two
     * [CoordinateRequest] records in the list.
     *
     * From there, if no validation had failed thus far, it will call the [validateAvailableAllottedTime]
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

        with (request) {
            // This may change, but for now, only allow two coordinates
            if (coordinates.size != 2)
                errors.put(ErrorTag.COORDINATES, "Must have two coordinates supplied")

            // Validate each Coordinate
            coordinates.forEach {
                if (it.hours < 0 || it.hours >= 24)
                    errors.put(com.radiotelescope.contracts.appointment.ErrorTag.HOURS, "Hours must be between 0 and 24")
                if (it.minutes < 0 || it.minutes >= 60)
                    errors.put(com.radiotelescope.contracts.appointment.ErrorTag.MINUTES, "Minutes must be between 0 and 60")
                if (it.declination > 90 || it.declination < -90)
                    errors.put(com.radiotelescope.contracts.appointment.ErrorTag.DECLINATION, "Declination must be between -90 and 90")
            }
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
     * the entity that is being changed has had its type changed, the current
     * record will be deleted, and a new one will be persisted in tis place.
     *
     * If it has not changed, the entity can be updated normally. In this case,
     * the existing coordinates will be deleted and new ones will be persisted.
     *
     * @param appointment the current [Appointment]
     * @param request the [Request]
     * @return the updated (or new) [Appointment]
     */
    private fun handleEntityUpdate(
            appointment: Appointment,
            request: Request
    ): Appointment {
        // If the type is the same
        if (appointment.type == Appointment.Type.RASTER_SCAN) {
            // Delete old coordinates and dissociate
            val coordinateList = appointment.coordinateList
            coordinateRepo.deleteAll(coordinateList)

            // Update appointment
            val theAppointment = request.updateEntity(appointment)

            // Create new coordinates and associate
            val coordinates = request.toCoordinates()
            coordinates.forEach {
                it.appointment = theAppointment
                coordinateRepo.save(it)
            }
            theAppointment.coordinateList = request.toCoordinates()

            return appointmentRepo.save(theAppointment)
        } else {
            // The type is being changed, and we must delete the
            // old record and persist a new one
            val theUser = appointment.user
            val theStatus = appointment.status

            // Delete old record
            deleteAppointment(
                    appointment = appointment,
                    appointmentRepo = appointmentRepo,
                    coordinateRepo = coordinateRepo,
                    orientationRepo = orientationRepo
            )

            // Persist new coordinates
            val theCoordinates = request.toCoordinates()
            theCoordinates.forEach { coordinate ->
                coordinateRepo.save(coordinate)
            }

            // Persist new Appointment
            val theAppointment = request.toEntity()
            theAppointment.user = theUser
            theAppointment.status = theStatus

            // "Raster Scan" Appointments will have two Coordinates
            theAppointment.coordinateList = theCoordinates
            appointmentRepo.save(theAppointment)

            // Associated appointment and coordinates
            theCoordinates.forEach { coordinate ->
                coordinate.appointment = theAppointment
                coordinateRepo.save(coordinate)
            }

            return theAppointment
        }
    }

    /**
     * Data class containing all fields necessary for updating a
     * Raster Scan Appointment. Implements [AppointmentUpdate.Request]
     */
    data class Request(
            override var id: Long,
            override val startTime: Date,
            override val endTime: Date,
            override val telescopeId: Long,
            override val isPublic: Boolean,
            override val priority: Appointment.Priority,
            val coordinates: List<CoordinateRequest>
    ) : AppointmentUpdate.Request() {
        /**
         * Concrete implementation of the [AppointmentUpdate.Request.updateEntity]
         * method that returns an updated Appointment object
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
                    type = Appointment.Type.RASTER_SCAN
            )
        }

        /**
         * Method that will take the List of [CoordinateRequest] and return
         * a list of coordinates
         */
        fun toCoordinates(): MutableList<Coordinate> {
            val coordinateList = mutableListOf<Coordinate>()
            coordinates.forEach {
                coordinateList.add(it.toEntity())
            }
            return coordinateList
        }
    }
}