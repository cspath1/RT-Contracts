package com.radiotelescope.contracts.resetPasswordToken

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.resetPasswordToken.IResetPasswordTokenRepository
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class BaseResetPasswordTokenFactoryTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var resetPasswordTokenRepo: IResetPasswordTokenRepository

    private lateinit var factory: ResetPasswordTokenFactory

    @Before
    fun init() {
        factory = BaseResetPasswordTokenFactory(
                userRepo = userRepo,
                resetPasswordTokenRepo = resetPasswordTokenRepo
        )
    }

    @Test
    fun resetPasswordToken(){
        // Call the factory method
        val cmd = factory.requestPasswordReset(
                email = ""
        )

        // Ensure it is the correct command
        assertTrue(cmd is CreateResetPasswordToken)
    }

    @Test
    fun resetPassword(){
        val cmd = factory.resetPassword(
                request = ResetPassword.Request(
                        password = "ValidPassword1",
                        passwordConfirm = "ValidPassword1"
                ),
                token = "SomeToken"
        )

        // Ensure it is the correct command
        assertTrue(cmd is ResetPassword)
    }
}