package com.radiotelescope.controller.user

import com.radiotelescope.contracts.user.BaseUserFactory
import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.repository.accountActivateToken.IAccountActivateTokenRepository
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.loginAttempt.ILoginAttemptRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.services.s3.MockAwsS3DeleteService
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired

internal abstract class BaseUserRestControllerTest : BaseRestControllerTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var accountActivateTokenRepo: IAccountActivateTokenRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    @Autowired
    private lateinit var loginAttemptRepo: ILoginAttemptRepository

    // These will both be needed in all user rest controller
    // tests, so instantiate them here
    private lateinit var wrapper: UserUserWrapper
    private lateinit var factory: BaseUserFactory

    @Before
    override fun init() {
        super.init()

        factory = BaseUserFactory(
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                accountActivateTokenRepo = accountActivateTokenRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                loginAttemptRepo = loginAttemptRepo,
                deleteService = MockAwsS3DeleteService(true)
        )

        wrapper = UserUserWrapper(
                context = getContext(),
                factory = factory,
                userRepo = userRepo
        )
    }

    // Once instantiated, this will not be altered
    // so only supply a getter for it
    fun getWrapper(): UserUserWrapper {
        return wrapper
    }
}