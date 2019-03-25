package com.radiotelescope.contracts.appointment.factory.auto

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.create.CelestialBodyAppointmentCreate
import com.radiotelescope.contracts.appointment.create.AppointmentCreate
import com.radiotelescope.contracts.appointment.factory.BaseAppointmentFactory
import com.radiotelescope.contracts.appointment.request.AppointmentRequest
import com.radiotelescope.contracts.appointment.request.CelestialBodyAppointmentRequest
import com.radiotelescope.contracts.appointment.update.AppointmentUpdate
import com.radiotelescope.contracts.appointment.update.CelestialBodyAppointmentUpdate
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.orientation.IOrientationRepository
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
        private val celestialBodyRepo: ICelestialBodyRepository,
        private val coordinateRepo: ICoordinateRepository,
        private val orientationRepo: IOrientationRepository,
        private val allottedTimeCapRepo: IAllottedTimeCapRepository
) : AutoAppointmentFactory, BaseAppointmentFactory(
        appointmentRepo = appointmentRepo,
        userRepo = userRepo,
        telescopeRepo = telescopeRepo,
        userRoleRepo = userRoleRepo,
        allottedTimeCapRepo = allottedTimeCapRepo
) {
    /**
     * Override of the [AutoAppointmentFactory.create] method that will return a [CelestialBodyAppointmentCreate]
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
                celestialBodyRepo = celestialBodyRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
    }

    /**
     * Override of the [AutoAppointmentFactory.update] method that will return a [CelestialBodyAppointmentUpdate]
     * command object
     *
     * @param request the [CelestialBodyAppointmentUpdate.Request] object
     * @return a [CelestialBodyAppointmentUpdate] command
     */
    override fun update(request: AppointmentUpdate.Request): Command<Long, Multimap<ErrorTag, String>> {
        return CelestialBodyAppointmentUpdate(
                request = request as CelestialBodyAppointmentUpdate.Request,
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo,
                celestialBodyRepo = celestialBodyRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
    }

    /**
     * Override of the [AutoAppointmentFactory.request] method that will return a [CelestialBodyAppointmentRequest]
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