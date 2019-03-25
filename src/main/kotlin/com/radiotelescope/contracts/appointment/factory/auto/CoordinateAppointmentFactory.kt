package com.radiotelescope.contracts.appointment.factory.auto

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.appointment.create.CoordinateAppointmentCreate
import com.radiotelescope.contracts.appointment.create.AppointmentCreate
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.factory.BaseAppointmentFactory
import com.radiotelescope.contracts.appointment.request.AppointmentRequest
import com.radiotelescope.contracts.appointment.request.CoordinateAppointmentRequest
import com.radiotelescope.contracts.appointment.update.AppointmentUpdate
import com.radiotelescope.contracts.appointment.update.CoordinateAppointmentUpdate
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.orientation.IOrientationRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.user.IUserRepository

/**
 * Concrete implementation of the [BaseAppointmentFactory] for Coordinate Appointments
 */
class CoordinateAppointmentFactory(
        private val appointmentRepo: IAppointmentRepository,
        private val userRepo: IUserRepository,
        private val telescopeRepo: ITelescopeRepository,
        private val userRoleRepo: IUserRoleRepository,
        private val coordinateRepo: ICoordinateRepository,
        private val orientationRepo: IOrientationRepository,
        private val allottedTimeCapRepo: IAllottedTimeCapRepository
): AutoAppointmentFactory, BaseAppointmentFactory(
        appointmentRepo = appointmentRepo,
        userRepo = userRepo,
        telescopeRepo = telescopeRepo,
        userRoleRepo = userRoleRepo,
        allottedTimeCapRepo = allottedTimeCapRepo
) {
    /**
     * Override of the [AutoAppointmentFactory.create] method that will return a [CoordinateAppointmentCreate]
     * command object
     *
     * @param request the [CoordinateAppointmentCreate.Request] object
     * @return a [CoordinateAppointmentCreate] command
     */
    override fun create(request: AppointmentCreate.Request): Command<Long, Multimap<ErrorTag, String>> {
        return CoordinateAppointmentCreate(
                request = request as CoordinateAppointmentCreate.Request,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
    }


    /**
     * Override of the [AutoAppointmentFactory.request] method that will return a [CoordinateAppointmentRequest]
     * command object
     *
     * @param request the [CoordinateAppointmentRequest.Request] object
     * @return a [CoordinateAppointmentRequest] command
     */
    override fun request(request: AppointmentRequest.Request): Command<Long, Multimap<ErrorTag, String>> {
        return CoordinateAppointmentRequest(
                request = request as CoordinateAppointmentRequest.Request,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo
        )
    }

    /**
     * Override of the [AutoAppointmentFactory.update] method that will return a [CoordinateAppointmentUpdate] command object
     *
     * @param request the [CoordinateAppointmentUpdate.Request]
     * @return a [CoordinateAppointmentUpdate] command object
     */
    override fun update(request: AppointmentUpdate.Request): Command<Long, Multimap<ErrorTag, String>>  {
        return CoordinateAppointmentUpdate(
                request = request as CoordinateAppointmentUpdate.Request,
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
    }
}