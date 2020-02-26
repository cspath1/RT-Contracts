package com.radiotelescope.contracts.appointment.request

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
import java.util.*

/**
 * Override of the [Command] interface used to request a Point/Coordinate Appointment.
 *
 * @param request the [Request] data class
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param userRepo the [IUserRepository] interface
 * @param radioTelescopeRepo the [IRadioTelescopeRepository] interface
 * @param coordinateRepo the [ICoordinateRepository] interface
 */
class CoordinateAppointmentRequest(
        private val request: Request,
        private val appointmentRepo: IAppointmentRepository,
        private val userRepo: IUserRepository,
        private val radioTelescopeRepo: IRadioTelescopeRepository,
        private val coordinateRepo: ICoordinateRepository
) : Command<Long, Multimap<ErrorTag, String>>, AppointmentRequest {
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
            theAppointment.coordinateList = arrayListOf(theCoordinate)

            theCoordinate.appointment = theAppointment
            coordinateRepo.save(theCoordinate)

            appointmentRepo.save(theAppointment)

            return SimpleResult(theAppointment.id, null)
        }
    }

    /**
     * Method responsible for constraint checking and validation for the [Request].
     * It will ensure the hours, minutes, seconds, and declination are all valid
     * after calling the [baseRequestValidation] method.
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        baseRequestValidation(
                request = request,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                appointmentRepo = appointmentRepo
        )?.let { return it }

        val errors = HashMultimap.create<ErrorTag,String>()

        with(request) {
            if (hours < 0 || hours >= 24)
                errors.put(ErrorTag.HOURS, "Hours must be between 0 and 24")
            if (minutes < 0 || minutes >= 60)
                errors.put(ErrorTag.MINUTES, "Minutes must be between 0 and 60")
            if (declination > 90 || declination < -90)
                errors.put(ErrorTag.DECLINATION, "Declination must be between -90 - 90")
        }

        return if (errors.isEmpty) null else errors
    }

    /**
     * Data class containing all fields necessary for appointment creation. Implements
     * the [BaseCreateRequest] interface.
     *
     * @param hours the Right Ascension hours
     * @param minutes the Right Ascension minutes
     * @param seconds the Right Ascension seconds
     * @param declination the Declination
     */
    data class Request(
            override val userId: Long,
            override val startTime: Date,
            override val endTime: Date,
            override val telescopeId: Long,
            override val isPublic: Boolean,
            override val priority: Appointment.Priority,
            val hours: Int,
            val minutes: Int,
            val declination: Double
    ) : AppointmentRequest.Request() {
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
                    priority = priority,
                    type = Appointment.Type.POINT
            )
        }

        /**
         * Method that will adapt the request into a [Coordinate] entity object
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