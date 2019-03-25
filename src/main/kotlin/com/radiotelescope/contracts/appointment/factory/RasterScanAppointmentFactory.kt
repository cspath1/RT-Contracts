package com.radiotelescope.contracts.appointment.factory

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.create.AppointmentCreate
import com.radiotelescope.contracts.appointment.create.RasterScanAppointmentCreate
import com.radiotelescope.contracts.appointment.request.AppointmentRequest
import com.radiotelescope.contracts.appointment.request.RasterScanAppointmentRequest
import com.radiotelescope.contracts.appointment.update.AppointmentUpdate
import com.radiotelescope.contracts.appointment.update.RasterScanAppointmentUpdate
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.orientation.IOrientationRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.user.IUserRepository

/**
 * Concrete implementation of the [BaseAppointmentFactory] for Raster Scan Appointments
 */
class RasterScanAppointmentFactory(
        private val appointmentRepo: IAppointmentRepository,
        private val userRepo: IUserRepository,
        private val telescopeRepo: ITelescopeRepository,
        private val userRoleRepo: IUserRoleRepository,
        private val coordinateRepo: ICoordinateRepository,
        private val allottedTimeCapRepo: IAllottedTimeCapRepository,
        private val orientationRepo: IOrientationRepository
) : BaseAppointmentFactory(
        appointmentRepo = appointmentRepo,
        userRepo = userRepo,
        telescopeRepo = telescopeRepo,
        userRoleRepo = userRoleRepo,
        allottedTimeCapRepo = allottedTimeCapRepo
) {
    /**
     * Override of the [AppointmentFactory.create] method that will return a [RasterScanAppointmentCreate]
     * command object
     *
     * @param request the [RasterScanAppointmentCreate.Request] object
     * @return a [RasterScanAppointmentCreate] command
     */
    override fun create(request: AppointmentCreate.Request): Command<Long, Multimap<ErrorTag, String>> {
        return RasterScanAppointmentCreate(
                request = request as RasterScanAppointmentCreate.Request,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
    }

    override fun request(request: AppointmentRequest.Request): Command<Long, Multimap<ErrorTag, String>> {
        return RasterScanAppointmentRequest(
                request = request as RasterScanAppointmentRequest.Request,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo
        )
    }

    override fun update(request: AppointmentUpdate.Request): Command<Long, Multimap<ErrorTag, String>> {
        return RasterScanAppointmentUpdate(
                request = request as RasterScanAppointmentUpdate.Request,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                orientationRepo = orientationRepo
        )
    }
}