package com.radiotelescope.contracts.appointment.factory

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.appointment.create.CoordinateAppointmentCreate
import com.radiotelescope.contracts.appointment.create.Create
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.user.IUserRepository

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
        userRoleRepo = userRoleRepo,
        coordinateRepo = coordinateRepo
) {
    /**
     * Override of the [AppointmentFactory.create] method that will return a [CoordinateAppointmentCreate]
     * command object
     *
     * @param request the [CoordinateAppointmentCreate.Request] object
     * @return a [CoordinateAppointmentCreate] command
     */
    override fun create(request: Create.Request): Command<Long, Multimap<ErrorTag, String>> {
        return CoordinateAppointmentCreate(
                request = request as CoordinateAppointmentCreate.Request,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo
        )
    }
}