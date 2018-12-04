package com.radiotelescope.controller.user

import com.radiotelescope.contracts.accountActivateToken.BaseAccountActivateTokenFactory
import com.radiotelescope.contracts.accountActivateToken.UserAccountActivateTokenWrapper
import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.repository.accountActivateToken.IAccountActivateTokenRepository
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired

abstract class BaseActivateAccountRestControllerTest : BaseRestControllerTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var accountActivateTokenRepo: IAccountActivateTokenRepository

    // These will both be needed in all user rest controller
    // so instantiate them here
    private lateinit var wrapper: UserAccountActivateTokenWrapper
    private lateinit var factory: BaseAccountActivateTokenFactory

    @Before
    override fun init() {
        super.init()

        factory = BaseAccountActivateTokenFactory(
                accountActivateTokenRepo = accountActivateTokenRepo,
                userRepo = userRepo
        )

        wrapper = UserAccountActivateTokenWrapper(
                factory = factory
        )
    }

    // Once instantiated, this will not be altered
    // so only supply a getter for it
    fun getWrapper(): UserAccountActivateTokenWrapper {
        return wrapper
    }
}