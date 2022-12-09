package com.radiotelescope.contracts.appointment.request

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.orientation.IOrientationRepository
import com.radiotelescope.repository.orientation.Orientation
import com.radiotelescope.repository.spectracyberConfig.ISpectracyberConfigRepository
import com.radiotelescope.repository.spectracyberConfig.SpectracyberConfig
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
import java.util.*

/**
 * Override of the [Command] interface used to request a Drift Scan Appointment.
 *
 * @param request the [Request] data class
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param userRepo the [IUserRepository] interface
 * @param radioTelescopeRepo the [IRadioTelescopeRepository] interface
 * @param orientationRepo the [IOrientationRepository] interface
 */
class DriftScanAppointmentRequest(
        private val request: Request,
        private val appointmentRepo: IAppointmentRepository,
        private val userRepo: IUserRepository,
        private val radioTelescopeRepo: IRadioTelescopeRepository,
        private val orientationRepo: IOrientationRepository,
        private val spectracyberConfigRepo: ISpectracyberConfigRepository
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
        validateRequest()?.let {return SimpleResult(null, it)} ?: let {
            val theAppointment = request.toEntity()

            val theOrientation = request.toOrientation()
            orientationRepo.save(theOrientation)

            theAppointment.user = userRepo.findById(request.userId).get()
            theAppointment.status = Appointment.Status.REQUESTED
            theAppointment.orientation = theOrientation
            theAppointment.spectracyberConfig = spectracyberConfigRepo.save(SpectracyberConfig(SpectracyberConfig.Mode.SPECTRAL, 0.3, 0.0, 10.0, 1, 1200))

            appointmentRepo.save(theAppointment)

            return SimpleResult(theAppointment.id, null)
        }
    }

    /**
     * Method responsible for constraint checking and validations for the
     * appointment create request. It will ensure that both the user and telescope
     * id exists and that the appointment's end time is not before its start time.
     * It also ensures that the start time is not before the current date
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        baseRequestValidation(
                request = request,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                appointmentRepo = appointmentRepo
        )?.let { return it }

        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if(elevation < 0 || elevation > 90)
                errors.put(ErrorTag.ELEVATION, "Elevation must be between 0 and 90")
            if(azimuth < 0 || azimuth >= 360)
                errors.put(ErrorTag.AZIMUTH, "Azimuth must be between 0 and 359")
        }

        return if (errors.isEmpty) null else errors
    }

    /**
     * Data class containing all fields necessary for appointment creation. Implements
     * the [BaseCreateRequest] interface.
     */
    data class Request(
            override val userId: Long,
            override val startTime: Date,
            override val endTime: Date,
            override val telescopeId: Long,
            override val isPublic: Boolean,
            override val priority: Appointment.Priority,
            val azimuth: Double,
            val elevation: Double
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
                    type = Appointment.Type.DRIFT_SCAN
            )
        }

        /**
         * Method that will take the [Request] azimuth and elevation
         * and returns an [Orientation] object
         */
        fun toOrientation(): Orientation {
            return Orientation(
                    azimuth = azimuth,
                    elevation = elevation
            )
        }
    }
}