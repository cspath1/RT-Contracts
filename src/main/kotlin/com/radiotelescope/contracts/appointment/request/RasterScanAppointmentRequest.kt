package com.radiotelescope.contracts.appointment.request

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.coordinate.CoordinateRequest
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
import java.util.*

/**
 * Override of the [Command] interface used for Raster Scan
 * Appointment requests
 *
 * @param request the [Request] object
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param userRepo the [IUserRepository] interface
 * @param telescopeRepo the [ITelescopeRepository] interface
 * @param coordinateRepo the [ICoordinateRepository] interface
 */
class RasterScanAppointmentRequest(
        private val request: Request,
        private val appointmentRepo: IAppointmentRepository,
        private val userRepo: IUserRepository,
        private val telescopeRepo: ITelescopeRepository,
        private val coordinateRepo: ICoordinateRepository
) : Command<Long, Multimap<ErrorTag, String>>, AppointmentRequest {
    /**
     * Override of the [Command.execute] method. Calls the [validateRequest] method
     * that will handle all constraint checking and validation
     *
     * If validation passes, it will create and persist the [Appointment] object
     * and return the id in the [SimpleResult] object
     *
     * If validation fails, it will return the [SimpleResult] with the errors
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val theAppointment = request.toEntity()

            val theCoordinates = request.toCoordinates()
            theCoordinates.forEach { coordinate ->
                coordinateRepo.save(coordinate)
            }

            theAppointment.user = userRepo.findById(request.userId).get()

            // "Raster Scan" appointments will have (for now) two Coordinates
            theAppointment.coordinateList = theCoordinates

            // Mark the appointment as requested
            theAppointment.status = Appointment.Status.REQUESTED
            appointmentRepo.save(theAppointment)

            theCoordinates.forEach { coordinate ->
                coordinate.appointment = theAppointment
                coordinateRepo.save(coordinate)
            }

            return SimpleResult(theAppointment.id, null)
        }
    }

    /**
     * Method responsible for constraint checking and validation for the [Request].
     * It will ensure that there are two coordinates, and the contents of each are valid
     * after calling the [baseRequestValidation] method.
     *
     * @return a [HashMultimap] of errors or null
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        baseRequestValidation(
                request = request,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                appointmentRepo = appointmentRepo
        )?.let { return it }

        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            // This may change, but for now, only allow two coordinates
            if (coordinates.size != 2)
                errors.put(ErrorTag.COORDINATES, "Must have two coordinates supplied")

            // Validate each Coordinate
            coordinates.forEach {
                if (it.hours < 0 || it.hours >= 24)
                    errors.put(ErrorTag.HOURS, "Hours must be between 0 and 24")
                if (it.minutes < 0 || it.minutes >= 60)
                    errors.put(ErrorTag.MINUTES, "Minutes must be between 0 and 60")
                if (it.seconds < 0 || it.seconds >= 60)
                    errors.put(ErrorTag.SECONDS, "Seconds must be between 0 and 60")
                if (it.declination > 90 || it.declination < -90)
                    errors.put(ErrorTag.DECLINATION, "Declination must be between -90 and 90")
            }
        }

        return if (errors.isEmpty) null else errors
    }

    /**
     * Data class containing all fields necessary for requesting an appointment.
     * Implements [BaseCreateRequest] interface.
     *
     * @param coordinates the [List] of [CoordinateRequest] objects
     */
    data class Request(
            override val userId: Long,
            override val startTime: Date,
            override val endTime: Date,
            override val telescopeId: Long,
            override val isPublic: Boolean,
            val coordinates: List<CoordinateRequest>
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