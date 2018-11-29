package com.radiotelescope.controller.user

import com.radiotelescope.contracts.user.BaseUserFactory
import com.radiotelescope.contracts.user.UserFactory
import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.repository.accountActivateToken.IAccountActivateTokenRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired

abstract class BaseUserRestControllerTest : BaseRestControllerTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var accountActivateTokenRepo: IAccountActivateTokenRepository

    private lateinit var wrapper: UserUserWrapper
    private lateinit var factory: BaseUserFactory

    @Before
    override fun init() {
        super.init()

        factory = BaseUserFactory(
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                accountActivateTokenRepo = accountActivateTokenRepo
        )

        wrapper = UserUserWrapper(
                context = getContext(),
                factory = factory,
                userRepo = userRepo
        )
    }

    fun getWrapper(): UserUserWrapper {
        return wrapper
    }
}