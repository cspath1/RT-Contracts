package com.radiotelescope.controller.resetPasswordToken

import com.radiotelescope.contracts.resetPasswordToken.BaseResetPasswordTokenFactory
import com.radiotelescope.contracts.resetPasswordToken.UserResetPasswordTokenWrapper
import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.repository.loginAttempt.ILoginAttemptRepository
import com.radiotelescope.repository.resetPasswordToken.IResetPasswordTokenRepository
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired

internal abstract class BaseResetPasswordTokenRestControllerTest : BaseRestControllerTest() {
    @Autowired
    private lateinit var resetPasswordTokenRepo: IResetPasswordTokenRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var loginAttemptRepo: ILoginAttemptRepository

    // These will both be needed in all reset password token controller
    // tests, so instantiate them here
    private lateinit var wrapper: UserResetPasswordTokenWrapper
    private lateinit var factory: BaseResetPasswordTokenFactory

    @Before
    override fun init() {
        super.init()

        factory = BaseResetPasswordTokenFactory(
                resetPasswordTokenRepo = resetPasswordTokenRepo,
                userRepo = userRepo,
                loginAttemptRepo = loginAttemptRepo
        )

        wrapper = UserResetPasswordTokenWrapper(
                resetPasswordTokenFactory = factory
        )
    }

    // Once instantiated, this will not be altered
    // so only supply a getter for it
    fun getWrapper(): UserResetPasswordTokenWrapper {
        return wrapper
    }
}