package com.radiotelescope.controller.appointment

import com.radiotelescope.contracts.appointment.factory.auto.*
import com.radiotelescope.contracts.appointment.wrapper.UserAutoAppointmentWrapper
import com.radiotelescope.contracts.appointment.factory.auto.CelestialBodyAppointmentFactory
import com.radiotelescope.contracts.appointment.factory.auto.RasterScanAppointmentFactory
import com.radiotelescope.contracts.appointment.factory.manual.FreeControlAppointmentFactory
import com.radiotelescope.contracts.appointment.wrapper.UserManualAppointmentWrapper
import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.controller.model.Profile
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.heartbeatMonitor.IHeartbeatMonitorRepository
import com.radiotelescope.repository.orientation.IOrientationRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.viewer.IViewerRepository
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired

internal abstract class BaseAppointmentRestControllerTest : BaseRestControllerTest() {
    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var radioTelescopeRepo: IRadioTelescopeRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    @Autowired
    private lateinit var celestialBodyRepo: ICelestialBodyRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    @Autowired
    private lateinit var viewerRepo: IViewerRepository

    @Autowired
    private lateinit var orientationRepo: IOrientationRepository

    @Autowired
    private lateinit var heartbeatMonitorRepo: IHeartbeatMonitorRepository

    // These will both be needed in all appointment
    // controller tests, so instantiate them here
    private lateinit var factory: AutoAppointmentFactory

    @Before
    override fun init() {
        super.init()

        // The factory used for most of the tests does not matter
        // It only matters for different appointment types
        factory = CoordinateAppointmentFactory(
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        )
    }

    // Once instantiated, this will not be altered
    // so only supply a getter for it
    fun getCoordinateCreateWrapper(): UserAutoAppointmentWrapper {
        return UserAutoAppointmentWrapper(
                getContext(),
                factory = factory,
                appointmentRepo = appointmentRepo,
                viewerRepo = viewerRepo
        )
    }

    // Add other wrappers here (i.e. wrapper that will create a Celestial Body appointment)
    fun getCelestialBodyCreateWrapper(): UserAutoAppointmentWrapper {
        return UserAutoAppointmentWrapper(
                context = getContext(),
                factory = CelestialBodyAppointmentFactory(
                        appointmentRepo = appointmentRepo,
                        userRepo = userRepo,
                        userRoleRepo = userRoleRepo,
                        radioTelescopeRepo = radioTelescopeRepo,
                        celestialBodyRepo = celestialBodyRepo,
                        coordinateRepo = coordinateRepo,
                        orientationRepo = orientationRepo,
                        allottedTimeCapRepo = allottedTimeCapRepo,
                        heartbeatMonitorRepo = heartbeatMonitorRepo,
                        profile = Profile.TEST
                ),
                appointmentRepo = appointmentRepo,
                viewerRepo = viewerRepo
        )
    }

    fun getRasterScanCreateWrapper(): UserAutoAppointmentWrapper {
        return UserAutoAppointmentWrapper(
                context = getContext(),
                factory = RasterScanAppointmentFactory(
                        appointmentRepo = appointmentRepo,
                        userRepo = userRepo,
                        userRoleRepo = userRoleRepo,
                        radioTelescopeRepo = radioTelescopeRepo,
                        coordinateRepo = coordinateRepo,
                        allottedTimeCapRepo = allottedTimeCapRepo,
                        orientationRepo = orientationRepo,
                        heartbeatMonitorRepo = heartbeatMonitorRepo,
                        profile = Profile.TEST
                ),
                appointmentRepo = appointmentRepo,
                viewerRepo = viewerRepo
        )
    }

    fun getFreeControlWrapper(): UserManualAppointmentWrapper {
        return UserManualAppointmentWrapper(
                context = getContext(),
                factory = FreeControlAppointmentFactory(
                        appointmentRepo = appointmentRepo,
                        userRepo = userRepo,
                        radioTelescopeRepo = radioTelescopeRepo,
                        coordinateRepo = coordinateRepo,
                        userRoleRepo = userRoleRepo,
                        allottedTimeCapRepo = allottedTimeCapRepo,
                        orientationRepo = orientationRepo,
                        heartbeatMonitorRepo = heartbeatMonitorRepo,
                        profile = Profile.TEST
                ),
                appointmentRepo = appointmentRepo,
                viewerRepo = viewerRepo
        )
    }

    fun getDriftScanCreateWrapper(): UserAutoAppointmentWrapper {
        return UserAutoAppointmentWrapper(
                context = getContext(),
                factory = DriftScanAppointmentFactory(
                        appointmentRepo = appointmentRepo,
                        userRepo = userRepo,
                        userRoleRepo = userRoleRepo,
                        radioTelescopeRepo = radioTelescopeRepo,
                        allottedTimeCapRepo = allottedTimeCapRepo,
                        orientationRepo = orientationRepo,
                        coordinateRepo = coordinateRepo,
                        heartbeatMonitorRepo = heartbeatMonitorRepo,
                        profile = Profile.TEST
                ),
                appointmentRepo = appointmentRepo,
                viewerRepo = viewerRepo
        )
    }
}