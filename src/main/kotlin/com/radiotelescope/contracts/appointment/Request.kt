package com.radiotelescope.contracts.appointment

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
import java.util.*

/**
 * Override of the [Command] interface used to request an appointment.
 *
 * @param request the [Request] data class
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param userRepo the [IUserRepository] interface
 * @param telescopeRepo the [ITelescopeRepository] interface
 * @param coordinateRepo the [ICoordinateRepository] interface
 */
class Request(
        private val request: Request,
        private val appointmentRepo: IAppointmentRepository,
        private val userRepo: IUserRepository,
        private val telescopeRepo: ITelescopeRepository,
        private val coordinateRepo: ICoordinateRepository
) : Command<Long, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method. Calls the [validateRequest] method
     * that will handle all constraint checking and validation.
     *
     * If validation passes, it will create and persist the [Appointment] object, set [Appointment.status] to Requested
     * and return the id in the [SimpleResult] object.
     *
     * If validation fails, it will return a [SimpleResult] with the errors
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val theAppointment = request.toEntity()

            val theCoordinate = request.toCoordinate()
            coordinateRepo.save(theCoordinate)

            theAppointment.user = userRepo.findById(request.userId).get()
            theAppointment.status = Appointment.Status.REQUESTED
            theAppointment.coordinate = theCoordinate

            appointmentRepo.save(theAppointment)
            return SimpleResult(theAppointment.id, null)
        }
    }

    /**
     * Method responsible for constraint checking and validations for the
     * appointment request request. It will ensure that both the user and telescope
     * id exists and that the appointment's end time is not before its start time.
     * It also ensures that the start time is not before the current date
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag,String>()

        with(request) {
            if (!userRepo.existsById(userId)) {
                errors.put(ErrorTag.USER_ID, "User Id #$userId could not be found")
                return errors
            }
            if (!telescopeRepo.existsById(telescopeId)) {
                errors.put(ErrorTag.TELESCOPE_ID, "Telescope Id #$telescopeId could not be found")
                return errors
            }
            if (startTime >= endTime)
                errors.put(ErrorTag.END_TIME, "Start time must be before end time")
            if (startTime < Date())
                errors.put(ErrorTag.START_TIME, "Start time must be after the current time" )
            if (hours < 0 || hours >= 24)
                errors.put(ErrorTag.HOURS, "Hours must be between 0 and 24")
            if (minutes < 0 || minutes >= 60)
                errors.put(ErrorTag.MINUTES, "Minutes must be between 0 and 60")
            if (seconds < 0 || seconds >= 60)
                errors.put(ErrorTag.SECONDS, "Seconds must be between 0 and 60")
            if (declination > 90 || declination < -90)
                errors.put(ErrorTag.DECLINATION, "Declination must be between -90 - 90")
        }
        return if (errors.isEmpty) null else errors
    }

    /**
     * Data class containing all fields necessary for appointment creation. Implements
     * the [BaseCreateRequest] interface.
     */
    data class Request(
            val userId: Long,
            val startTime: Date,
            val endTime: Date,
            val telescopeId: Long,
            val isPublic: Boolean,
            val hours: Int,
            val minutes: Int,
            val seconds: Int,
            val declination: Double,
            val priority: Appointment.Priority
    ) : BaseCreateRequest<Appointment> {
        /**
         * Concrete implementation of the [BaseCreateRequest.toEntity] method that
         * returns an Appointment object
         */
        override fun toEntity(): Appointment {
            return Appointment(
                    startTime = startTime,
                    endTime = endTime,
                    telescopeId = telescopeId,
                    isPublic = isPublic,
                    priority = priority
            )
        }

        /**
         * Method that will adapt the request into a [Coordinate] entity object
         */
        fun toCoordinate(): Coordinate {
            return Coordinate(
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds,
                    rightAscension = Coordinate.hoursMinutesSecondsToDegrees(
                            hours = hours,
                            minutes = minutes,
                            seconds = seconds
                    ),
                    declination = declination
            )
        }
    }
}