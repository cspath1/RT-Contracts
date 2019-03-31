package com.radiotelescope.controller.viewer

import com.radiotelescope.contracts.viewer.BaseViewerFactory
import com.radiotelescope.contracts.viewer.UserViewerWrapper
import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.viewer.IViewerRepository
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired

internal abstract class BaseViewerRestControllerTest : BaseRestControllerTest() {
    @Autowired
    private lateinit var viewerRepo: IViewerRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    // These will both be needed in all appointment
    // controller tests, so instantiate them here
    private lateinit var wrapper: UserViewerWrapper
    private lateinit var factory: BaseViewerFactory

    @Before
    override fun init() {
        super.init()

        factory = BaseViewerFactory(
                viewerRepo = viewerRepo,
                userRepo = userRepo,
                appointmentRepo = appointmentRepo
        )

        wrapper = UserViewerWrapper(
                getContext(),
                factory = factory,
                appointmentRepo = appointmentRepo
        )
    }

    // Once instantiated, this will not be altered
    // so only supply a getter for it
    fun getWrapper(): UserViewerWrapper {
        return wrapper
    }
}