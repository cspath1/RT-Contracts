package com.radiotelescope.controller.allottedTimeCap

import com.radiotelescope.contracts.allottedTimeCap.BaseAllottedTimeCapFactory
import com.radiotelescope.contracts.allottedTimeCap.UserAllottedTimeCapWrapper
import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired

internal abstract class BaseAllottedTimeCapRestControllerTest: BaseRestControllerTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    // These will both be needed in all user rest controller
    // tests, so instantiate them here
    private lateinit var wrapper: UserAllottedTimeCapWrapper
    private lateinit var factory: BaseAllottedTimeCapFactory

    @Before
    override fun init() {
        super.init()

        factory = BaseAllottedTimeCapFactory(
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )

        wrapper = UserAllottedTimeCapWrapper(
                context = getContext(),
                factory = factory
        )
    }

    // Once instantiated, this will not be altered
    // so only supply a getter for it
    fun getWrapper(): UserAllottedTimeCapWrapper {
        return wrapper
    }
}