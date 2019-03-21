package com.radiotelescope.contracts.appointment.factory

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.create.CelestialBodyAppointmentCreate
import com.radiotelescope.contracts.appointment.create.AppointmentCreate
import com.radiotelescope.contracts.appointment.request.AppointmentRequest
import com.radiotelescope.contracts.appointment.request.CelestialBodyAppointmentRequest
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.user.IUserRepository

/**
 * Concrete implementation of the [BaseAppointmentFactory] for Celestial Body Appointments
 */
class CelestialBodyAppointmentFactory(
        private val appointmentRepo: IAppointmentRepository,
        private val userRepo: IUserRepository,
        private val telescopeRepo: ITelescopeRepository,
        private val userRoleRepo: IUserRoleRepository,
        private val celestialBodyRepo: ICelestialBodyRepository
) : BaseAppointmentFactory(
        appointmentRepo = appointmentRepo,
        userRepo = userRepo,
        telescopeRepo = telescopeRepo,
        userRoleRepo = userRoleRepo
) {
    /**
     * Override of the [AppointmentFactory.create] method that will return a [CelestialBodyAppointmentCreate]
     * command object
     *
     * @param request the [CelestialBodyAppointmentCreate.Request] object
     * @return a [CelestialBodyAppointmentCreate] command
     */
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

    /**
     * Override of the [AppointmentFactory.request] method that will return a [CelestialBodyAppointmentRequest]
     * command object
     *
     * @param request the [CelestialBodyAppointmentRequest.Request] object
     * @return a [CelestialBodyAppointmentRequest] command
     */
    override fun request(request: AppointmentRequest.Request): Command<Long, Multimap<ErrorTag, String>> {
        return CelestialBodyAppointmentRequest(
                request = request as CelestialBodyAppointmentRequest.Request,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                celestialBodyRepo = celestialBodyRepo
        )
    }
}