package com.radiotelescope.contracts.appointment.factory.manual

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.factory.BaseAppointmentFactory
import com.radiotelescope.contracts.appointment.manual.AddFreeControlAppointmentCommand
import com.radiotelescope.contracts.appointment.manual.StartFreeControlAppointment
import com.radiotelescope.contracts.appointment.manual.StopFreeControlAppointment
import com.radiotelescope.contracts.appointment.manual.CalibrateFreeControlAppointment
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.orientation.IOrientationRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.spectracyberConfig.ISpectracyberConfigRepository
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import com.radiotelescope.repository.user.IUserRepository

/**
 * Concrete implementation of the [ManualAppointmentFactory] for Free Control Appointments
 */
class FreeControlAppointmentFactory(
        private val appointmentRepo: IAppointmentRepository,
        private val userRepo: IUserRepository,
        private val radioTelescopeRepo: IRadioTelescopeRepository,
        private val coordinateRepo: ICoordinateRepository,
        private val orientationRepo: IOrientationRepository,
        private val spectracyberConfigRepo: ISpectracyberConfigRepository,
        userRoleRepo: IUserRoleRepository,
        allottedTimeCapRepo: IAllottedTimeCapRepository
) : ManualAppointmentFactory, BaseAppointmentFactory(
        appointmentRepo = appointmentRepo,
        userRepo = userRepo,
        radioTelescopeRepo = radioTelescopeRepo,
        userRoleRepo = userRoleRepo,
        allottedTimeCapRepo = allottedTimeCapRepo
) {
    /**
     * Override of the [ManualAppointmentFactory.startAppointment] method that will return a [StartFreeControlAppointment]
     * command object
     *
     * @param request the [StartFreeControlAppointment.Request] object
     * @return a [StartFreeControlAppointment] command
     */
    override fun startAppointment(request: StartFreeControlAppointment.Request): Command<Long, Multimap<ErrorTag, String>> {
        return StartFreeControlAppointment(
                request = request,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRepo = userRepo,
                coordinateRepo = coordinateRepo,
                spectracyberConfigRepo = spectracyberConfigRepo
        )
    }

    /**
     * Override of the [ManualAppointmentFactory.addCommand] method that will return a [AddFreeControlAppointmentCommand]
     * command object
     *
     * @param request the [AddFreeControlAppointmentCommand.Request] object
     * @return an [AddFreeControlAppointmentCommand] command
     */
    override fun addCommand(request: AddFreeControlAppointmentCommand.Request): Command<Long, Multimap<ErrorTag, String>> {
        return AddFreeControlAppointmentCommand(
                request = request,
                appointmentRepo = appointmentRepo,
                coordinateRepo = coordinateRepo
        )
    }

    /**
     * Override of the [ManualAppointmentFactory.stopAppointment] method that will return a [StopFreeControlAppointment]
     * command object
     *
     * @param appointmentId the Appointment id
     * @return a [StopFreeControlAppointment] command
     */
    override fun stopAppointment(appointmentId: Long): Command<Long, Multimap<ErrorTag, String>> {
        return StopFreeControlAppointment(
                appointmentId = appointmentId,
                appointmentRepo = appointmentRepo
        )
    }

    /**
     * Override of the [ManualAppointmentFactory.calibrateAppointment] method that will return a [CalibrateFreeControlAppointment]
     * command object
     *
     * @param appointmentId the Appointment id
     * @return a [CalibrateFreeControlAppointment] command
     */
    override fun calibrateAppointment(appointmentId: Long): Command<Long, Multimap<ErrorTag, String>> {
        return CalibrateFreeControlAppointment(
                appointmentId = appointmentId,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                orientationRepo = orientationRepo
        )
    }
}