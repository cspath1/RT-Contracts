package com.radiotelescope.contracts.appointment.factory

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.create.CelestialBodyAppointmentCreate
import com.radiotelescope.contracts.appointment.create.AppointmentCreate
import com.radiotelescope.contracts.appointment.request.AppointmentRequest
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.user.IUserRepository

class CelestialBodyAppointmentFactory(
        private val appointmentRepo: IAppointmentRepository,
        private val userRepo: IUserRepository,
        private val telescopeRepo: ITelescopeRepository,
        private val userRoleRepo: IUserRoleRepository,
        coordinateRepo: ICoordinateRepository,
        private val celestialBodyRepo: ICelestialBodyRepository
) : BaseAppointmentFactory(
        appointmentRepo = appointmentRepo,
        userRepo = userRepo,
        telescopeRepo = telescopeRepo,
        userRoleRepo = userRoleRepo,
        coordinateRepo = coordinateRepo
) {
    override fun create(request: AppointmentCreate.Request): Command<Long, Multimap<ErrorTag, String>> {
        return CelestialBodyAppointmentCreate(
                request = request as CelestialBodyAppointmentCreate.Request,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                celestialBodyRepo = celestialBodyRepo
        )
    }

    override fun request(request: AppointmentRequest.Request): Command<Long, Multimap<ErrorTag, String>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}