package com.radiotelescope.contracts.appointment.manual

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.controller.model.Profile
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.heartbeatMonitor.IHeartbeatMonitorRepository
import com.radiotelescope.repository.orientation.IOrientationRepository
import com.radiotelescope.repository.orientation.Orientation
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import com.radiotelescope.repository.telescope.RadioTelescope
import java.util.*

/**
 * Override of the [Command] interface method used to calibrate the
 * radio telescope during a free control appointment
 *
 * @param appointmentId the [Appointment] id
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param radioTelescopeRepo the [IRadioTelescopeRepository] interface
 * @param orientationRepo the [IOrientationRepository] interface
 * @param heartbeatMonitorRepo the [IHeartbeatMonitorRepository] interface
 * @param profile the application's profile
 */
class CalibrateFreeControlAppointment(
        private val appointmentId: Long,
        private val appointmentRepo: IAppointmentRepository,
        private val radioTelescopeRepo: IRadioTelescopeRepository,
        private val orientationRepo: IOrientationRepository,
        private val heartbeatMonitorRepo: IHeartbeatMonitorRepository,
        private val profile: Profile
) : Command<Long, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method. Calls the [validateRequest] method
     * that will handle all constraint checking and validation.
     *
     * If validation passes, it will add an orientation object to the [Appointment]
     * and return the appointment's id in the [SimpleResult] object.
     *
     * If validation fails, it will return a [SimpleResult] with the errors.
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val theAppointment = appointmentRepo.findById(appointmentId).get()

            val theRadioTelescope = radioTelescopeRepo.findById(theAppointment.telescopeId).get()
            val calibrationOrientation = copyCalibrationOrientation(theRadioTelescope)
            orientationRepo.save(calibrationOrientation)

            theAppointment.orientation = calibrationOrientation
            appointmentRepo.save(theAppointment)
            return SimpleResult(theAppointment.id, null)
        }
    }

    /**
     * Copies the appointment radio telescope's calibration orientation and applies it to the
     * appointment, signifying to the control room that it should go through calibration.
     *
     * @param radioTelescope the Appointment [RadioTelescope]
     * @return the Calibration [Orientation]
     */
    private fun copyCalibrationOrientation(radioTelescope: RadioTelescope): Orientation {
        return Orientation(
                azimuth = radioTelescope.getCalibrationOrientation().azimuth,
                elevation = radioTelescope.getCalibrationOrientation().elevation
        )
    }

    /**
     * Validation method that ensures the appointment exists and is both in progress
     * and a free control appointment.
     *
     * @return a [HashMultimap] of errors or null
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (!appointmentRepo.existsById(appointmentId)) {
            errors.put(ErrorTag.ID, "Appointment #$appointmentId could not be found")
            return errors
        }

        val theAppointment = appointmentRepo.findById(appointmentId).get()

        if (theAppointment.type != Appointment.Type.FREE_CONTROL)
            errors.put(ErrorTag.TYPE, "Appointment #$appointmentId is not a free control appointment")
        if (theAppointment.status != Appointment.Status.IN_PROGRESS)
            errors.put(ErrorTag.STATUS, "Appointment #$appointmentId is not In Progress")
        if (profile == Profile.PROD || profile == Profile.TEST) {
            if (!determineInternetConnectivity(theAppointment.telescopeId))
                errors.put(ErrorTag.CONNECTION, "No internet connectivity between the remote and the control room has been established")
        }

        return if (errors.isEmpty) null else errors
    }

    private fun determineInternetConnectivity(telescopeId: Long): Boolean {
        val monitor = heartbeatMonitorRepo.findByRadioTelescopeId(telescopeId)!!

        val now = Date()
        val fiveMinutesAgo = Date(now.time - (1000 * 60 * 5))

        return monitor.lastCommunication > fiveMinutesAgo
    }
}