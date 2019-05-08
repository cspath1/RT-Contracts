package com.radiotelescope.controller.admin.telescopeLog

import com.radiotelescope.contracts.telescopeLog.AdminTelescopeLogWrapper
import com.radiotelescope.contracts.telescopeLog.BaseTelescopeLogFactory
import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.repository.telescopeLog.ITelescopeLogRepository
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired

internal abstract class BaseTelescopeLogRestControllerTest : BaseRestControllerTest() {
    @Autowired
    private lateinit var telescopeLogRepo: ITelescopeLogRepository

    // These will both be needed in all telescope log
    // rest controller tests, so instantiate them here
    private lateinit var wrapper: AdminTelescopeLogWrapper
    private lateinit var factory: BaseTelescopeLogFactory

    @Before
    override fun init() {
        super.init()

        factory = BaseTelescopeLogFactory(
                telescopeLogRepo = telescopeLogRepo
        )

        wrapper = AdminTelescopeLogWrapper(
                context = getContext(),
                factory = factory
        )
    }

    // Once instantiated, this will not be altered
    // so only supply a getter for it
    fun getWrapper(): AdminTelescopeLogWrapper {
        return wrapper
    }
}