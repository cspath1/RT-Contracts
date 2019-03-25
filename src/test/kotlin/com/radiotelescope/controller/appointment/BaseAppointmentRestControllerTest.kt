package com.radiotelescope.controller.appointment

import com.radiotelescope.contracts.appointment.factory.BaseAppointmentFactory
import com.radiotelescope.contracts.appointment.factory.CoordinateAppointmentFactory
import com.radiotelescope.contracts.appointment.UserAppointmentWrapper
import com.radiotelescope.contracts.appointment.factory.CelestialBodyAppointmentFactory
import com.radiotelescope.contracts.appointment.factory.RasterScanAppointmentFactory
import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.orientation.IOrientationRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.viewer.IViewerRepository
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired

abstract class BaseAppointmentRestControllerTest : BaseRestControllerTest() {
    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var telescopeRepo: ITelescopeRepository

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

    // These will both be needed in all appointment
    // controller tests, so instantiate them here
    private lateinit var wrapper: UserAppointmentWrapper
    private lateinit var factory: BaseAppointmentFactory

    @Before
    override fun init() {
        super.init()

        // The factory used for most of the tests does not matter
        // It only matters for different appointment types
        factory = CoordinateAppointmentFactory(
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
    }

    // Once instantiated, this will not be altered
    // so only supply a getter for it
    fun getCoordinateCreateWrapper(): UserAppointmentWrapper {
        return UserAppointmentWrapper(
                getContext(),
                factory = factory,
                appointmentRepo = appointmentRepo,
                viewerRepo = viewerRepo
        )
    }

    // Add other wrappers here (i.e. wrapper that will create a Celestial Body appointment)
    fun getCelestialBodyCreateWrapper(): UserAppointmentWrapper {
        return UserAppointmentWrapper(
                context = getContext(),
                factory = CelestialBodyAppointmentFactory(
                        appointmentRepo = appointmentRepo,
                        userRepo = userRepo,
                        userRoleRepo = userRoleRepo,
                        telescopeRepo = telescopeRepo,
                        celestialBodyRepo = celestialBodyRepo,
                        coordinateRepo = coordinateRepo,
                        orientationRepo = orientationRepo,
                        allottedTimeCapRepo = allottedTimeCapRepo
                ),
                appointmentRepo = appointmentRepo,
                viewerRepo = viewerRepo
        )
    }

    fun getRasterScanCreateWrapper(): UserAppointmentWrapper {
        return UserAppointmentWrapper(
                context = getContext(),
                factory = RasterScanAppointmentFactory(
                        appointmentRepo = appointmentRepo,
                        userRepo = userRepo,
                        userRoleRepo = userRoleRepo,
                        telescopeRepo = telescopeRepo,
                        coordinateRepo = coordinateRepo,
                        allottedTimeCapRepo = allottedTimeCapRepo,
                        orientationRepo = orientationRepo
                ),
                appointmentRepo = appointmentRepo,
                viewerRepo = viewerRepo
        )
    }
}