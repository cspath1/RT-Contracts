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

/**
 * Override of the [Command] interface method used adding a command
 * to a Free Control Appointment
 *
 * @param request the [Request] object
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param coordinateRepo the [ICoordinateRepository] interface
 */
class AddFreeControlAppointmentCommand(
        private val request: Request,
        private val appointmentRepo: IAppointmentRepository,
        private val coordinateRepo: ICoordinateRepository
) : Command<Long, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method. Calls the [validateRequest] method
     * that will handle all constraint checking and validation.
     *
     * If validation passes, it will create and persist a new [Coordinate] and
     * associate it with the appointment. It will then return the appointment id
     * in the [SimpleResult] object.
     *
     * If validation fails, it will return a [SimpleResult] with the errors.
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val theAppointment = appointmentRepo.findById(request.appointmentId).get()

            val theNewCoordinate = request.toEntity()
            theNewCoordinate.appointment = theAppointment
            coordinateRepo.save(theNewCoordinate)

            theAppointment.coordinateList.add(theNewCoordinate)
            appointmentRepo.save(theAppointment)

            return SimpleResult(theAppointment.id, null)
        }
    }

    /**
     * Validation method that ensures the appointment exists and the
     * new coordinate information is valid. If this is the case, it will
     * make sure the appointment is a manual appointment and currently
     * in progress
     *
     * @return a [HashMultimap] of errors or null
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if (!appointmentRepo.existsById(appointmentId))
                errors.put(ErrorTag.ID, "Appointment #$appointmentId could not be found")
            if (hours < 0 || hours >= 24)
                errors!!.put(ErrorTag.HOURS, "Hours must be between 0 and 23")
            if (minutes < 0 || minutes >= 60)
                errors!!.put(ErrorTag.MINUTES, "Minutes must be between 0 and 60")
            if (seconds < 0 || seconds >= 60)
                errors!!.put(ErrorTag.SECONDS, "Seconds must be between 0 and 60")
            if (declination > 90 || declination < -90)
                errors!!.put(ErrorTag.DECLINATION, "Declination must be between -90 and 90")

            if (errors.isNotEmpty())
                return errors

            val theAppointment = appointmentRepo.findById(appointmentId).get()

            if (theAppointment.type != Appointment.Type.FREE_CONTROL)
                errors.put(ErrorTag.TYPE, "Appointment #$appointmentId is not a free control appointment")
            if (theAppointment.status != Appointment.Status.IN_PROGRESS)
                errors.put(ErrorTag.STATUS, "Appointment #$appointmentId is not In Progress")
        }

        return if (errors.isEmpty) null else errors
    }

    /**
     * Data class containing fields necessary to add a new command
     * to a free control appointment. Implements [BaseCreateRequest]
     * interface.
     */
    data class Request(
            val appointmentId: Long,
            val hours: Int,
            val minutes: Int,
            val seconds: Int,
            val declination: Double
    ) : BaseCreateRequest<Coordinate> {
        /**
         * Concrete implementation of the [BaseCreateRequest.toEntity] method that
         * returns a Coordinate object
         */
        override fun toEntity(): Coordinate {
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