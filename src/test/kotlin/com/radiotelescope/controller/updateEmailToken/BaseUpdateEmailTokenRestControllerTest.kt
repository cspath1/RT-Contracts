package com.radiotelescope.controller.updateEmailToken

import com.radiotelescope.contracts.updateEmailToken.BaseUpdateEmailTokenFactory
import com.radiotelescope.contracts.updateEmailToken.UserUpdateEmailTokenWrapper
import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.repository.updateEmailToken.IUpdateEmailTokenRepository
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired

internal abstract class BaseUpdateEmailTokenRestControllerTest : BaseRestControllerTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var updateEmailTokenRepo: IUpdateEmailTokenRepository

    // These will both be needed in all update email token
    // controller tests, so instantiate them here
    private lateinit var wrapper: UserUpdateEmailTokenWrapper
    private lateinit var factory: BaseUpdateEmailTokenFactory

    @Before
    override fun init() {
        super.init()

        factory = BaseUpdateEmailTokenFactory(
                updateEmailTokenRepo = updateEmailTokenRepo,
                userRepo = userRepo
        )

        wrapper = UserUpdateEmailTokenWrapper(
                context = getContext(),
                factory = factory,
                userRepo = userRepo
        )
    }

    // Once instantiated, this will not be altered
    // so only supply a getter for it
    fun getWrapper(): UserUpdateEmailTokenWrapper {
        return wrapper
    }
}