package com.radiotelescope.contracts.appointment.manual

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.isNotEmpty
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
import java.util.*

/**
 * Override of the [Command] interface method used for Free Control Appointment creation
 *
 * @param request the [Request] object
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param userRepo the [IUserRepository] interface
 * @param radioTelescopeRepo the [IRadioTelescopeRepository] interface
 * @param coordinateRepo the [ICoordinateRepository] interface
 */
class StartFreeControlAppointment(
        private val request: Request,
        private val appointmentRepo: IAppointmentRepository,
        private val radioTelescopeRepo: IRadioTelescopeRepository,
        private val userRepo: IUserRepository,
        private val coordinateRepo: ICoordinateRepository
) : Command<Long, Multimap<ErrorTag, String>> {
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

            val currentTime = System.currentTimeMillis()
            theAppointment.startTime = Date(currentTime)
            theAppointment.endTime = Date(durationToEndTime(currentTime, request.duration))

            val theCoordinate = request.toCoordinate()
            coordinateRepo.save(theCoordinate)

            theAppointment.user = userRepo.findById(request.userId).get()

            // "Free Control" Appointments will have a single Coordinate
            // to indicate the starting coordinate for the appointment
            theAppointment.coordinateList = mutableListOf(theCoordinate)
            appointmentRepo.save(theAppointment)

            theCoordinate.appointment = theAppointment
            coordinateRepo.save(theCoordinate)

            return SimpleResult(theAppointment.id, null)
        }
    }

    /**
     * Adapts the duration (minutes) into an end time (milliseconds)
     *
     * @param startTime the start time in milliseconds
     * @param duration the duration in minutes
     * @return the end time in milliseconds
     */
    private fun durationToEndTime(startTime: Long, duration: Long): Long {
        // Duration is in minutes, startTime is in milliseconds
        return startTime + (duration * 1000 * 60)
    }

    /**
     * Validation method that ensures the user and telescope exist, then
     * checks if the hours, minutes, seconds, and declination are all valid.
     * Finally, it makes sure there is not another free control appointment
     * being conducted.
     *
     * @return a [HashMultimap] of errors or null
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        with (request) {
            if (!userRepo.existsById(userId))
                errors.put(ErrorTag.USER_ID, "User #$userId could not be found")
            if (!radioTelescopeRepo.existsById(telescopeId))
                errors.put(ErrorTag.TELESCOPE_ID, "Telescope #$telescopeId could not be found")
            if (hours < 0 || hours >= 24)
                errors!!.put(ErrorTag.HOURS, "Hours must be between 0 and 23")
            if (minutes < 0 || minutes >= 60)
                errors!!.put(ErrorTag.MINUTES, "Minutes must be between 0 and 60")
            if (declination > 90 || declination < -90)
                errors!!.put(ErrorTag.DECLINATION, "Declination must be between -90 and 90")

            if (errors.isNotEmpty())
                return errors

            val inProgressAppointment = appointmentRepo.findFirstByStatusAndTelescopeId(Appointment.Status.IN_PROGRESS, telescopeId)

            if (inProgressAppointment != null) {
                // Cannot have multiple free control appointments concurrently
                if (inProgressAppointment.type == Appointment.Type.FREE_CONTROL)
                    errors.put(ErrorTag.OVERLAP, "A manual appointment is already in progress")
            }
        }

        return if (errors.isEmpty) null else errors
    }

    /**
     * Data class containing all fields necessary for starting a free control appointment.
     * Implements the [BaseCreateRequest] interface
     */
    data class Request(
            val userId: Long,
            val telescopeId: Long,
            val duration: Long,
            val hours: Int,
            val minutes: Int,
            val declination: Double,
            val isPublic: Boolean
    ) : BaseCreateRequest<Appointment> {
        /**
         * Concrete implementation of the [BaseCreateRequest.toEntity] method that
         * returns an Appointment object
         */
        override fun toEntity(): Appointment {
            return Appointment(
                    startTime = Date(),
                    endTime = Date(),
                    telescopeId = telescopeId,
                    isPublic = isPublic,
                    priority = Appointment.Priority.MANUAL,
                    type = Appointment.Type.FREE_CONTROL
            )
        }

        /**
         * Method that will take the [Request] hours, minutes, seconds, and declination
         * and returns a [Coordinate] object
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