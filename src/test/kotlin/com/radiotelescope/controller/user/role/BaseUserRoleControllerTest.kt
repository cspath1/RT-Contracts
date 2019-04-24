package com.radiotelescope.controller.user.role

import com.radiotelescope.contracts.role.BaseUserRoleFactory
import com.radiotelescope.contracts.role.UserUserRoleWrapper
import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.repository.accountActivateToken.IAccountActivateTokenRepository
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired

internal abstract class BaseUserRoleControllerTest : BaseRestControllerTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var accountActivationTokenRepo: IAccountActivateTokenRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    // These will both be needed in all user rest controller
    // so instantiate them here
    private lateinit var wrapper: UserUserRoleWrapper
    private lateinit var factory: BaseUserRoleFactory

    @Before
    override fun init() {
        super.init()

        factory = BaseUserRoleFactory(
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                accountActivateTokenRepo = accountActivationTokenRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )

        wrapper = UserUserRoleWrapper(
                context = getContext(),
                factory = factory,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        )
    }

    // Once instantiated, this will not be altered
    // so only supply a getter for it
    fun getWrapper(): UserUserRoleWrapper {
        return wrapper
    }
}