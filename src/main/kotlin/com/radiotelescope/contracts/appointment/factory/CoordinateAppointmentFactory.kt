package com.radiotelescope.contracts.appointment.factory

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.appointment.create.CoordinateAppointmentCreate
import com.radiotelescope.contracts.appointment.create.AppointmentCreate
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.request.AppointmentRequest
import com.radiotelescope.contracts.appointment.request.CoordinateAppointmentRequest
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
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
        private val coordinateRepo: ICoordinateRepository
) : BaseAppointmentFactory(
        appointmentRepo = appointmentRepo,
        userRepo = userRepo,
        telescopeRepo = telescopeRepo,
        userRoleRepo = userRoleRepo
) {
    /**
     * Override of the [AppointmentFactory.create] method that will return a [CoordinateAppointmentCreate]
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
                coordinateRepo = coordinateRepo
        )
    }


    /**
     * Override of the [AppointmentFactory.request] method that will return a [CoordinateAppointmentRequest]
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
}