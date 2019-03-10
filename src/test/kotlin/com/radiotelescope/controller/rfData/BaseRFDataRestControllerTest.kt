package com.radiotelescope.controller.rfData

import com.radiotelescope.contracts.rfdata.BaseRFDataFactory
import com.radiotelescope.contracts.rfdata.UserRFDataWrapper
import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.rfdata.IRFDataRepository
import com.radiotelescope.repository.viewer.IViewerRepository
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired

abstract class BaseRFDataRestControllerTest : BaseRestControllerTest() {
    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var rfDataRepo: IRFDataRepository

    @Autowired
    private lateinit var viewerRepo: IViewerRepository

    // These will both be needed in all rf data
    // controller tests, so instantiate them here
    private lateinit var wrapper: UserRFDataWrapper
    private lateinit var factory: BaseRFDataFactory

    @Before
    override fun init() {
        super.init()

        factory = BaseRFDataFactory(
                appointmentRepo = appointmentRepo,
                rfDataRepo = rfDataRepo
        )

        wrapper = UserRFDataWrapper(
                context = getContext(),
                factory = factory,
                appointmentRepo = appointmentRepo,
                viewerRepo = viewerRepo
        )
    }

    // Once instantiated, this will not be altered
    // so only supply a getter for it
    fun getWrapper(): UserRFDataWrapper {
        return wrapper
    }
}