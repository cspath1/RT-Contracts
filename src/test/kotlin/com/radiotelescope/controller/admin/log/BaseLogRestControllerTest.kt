package com.radiotelescope.controller.admin.log

import com.radiotelescope.contracts.log.AdminLogWrapper
import com.radiotelescope.contracts.log.BaseLogFactory
import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired

abstract class BaseLogRestControllerTest : BaseRestControllerTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var logRepo: ILogRepository

    // These will both be needed in all user rest controller
    // tests, so instantiate them here
    private lateinit var wrapper: AdminLogWrapper
    private lateinit var factory: BaseLogFactory

    @Before
    override fun init() {
        super.init()

        factory = BaseLogFactory(
                logRepo = logRepo,
                userRepo = userRepo
        )

        wrapper = AdminLogWrapper(
                context = getContext(),
                factory = factory
        )
    }

    // Once instantiated, this will not be altered
    // so only supply a getter for it
    fun getWrapper(): AdminLogWrapper {
        return wrapper
    }
}