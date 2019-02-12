package com.radiotelescope.controller.appointment

import com.radiotelescope.contracts.appointment.BaseAppointmentFactory
import com.radiotelescope.contracts.appointment.UserAppointmentWrapper
import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
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

    // These will both be needed in all appointment
    // controller tests, so instantiate them here
    private lateinit var wrapper: UserAppointmentWrapper
    private lateinit var factory: BaseAppointmentFactory

    @Before
    override fun init() {
        super.init()

        factory = BaseAppointmentFactory(
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo
        )

        wrapper = UserAppointmentWrapper(
                getContext(),
                factory = factory,
                appointmentRepo = appointmentRepo
        )
    }

    // Once instantiated, this will not be altered
    // so only supply a getter for it
    fun getWrapper(): UserAppointmentWrapper {
        return wrapper
    }
}