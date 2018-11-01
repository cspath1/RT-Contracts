package com.radiotelescope.contracts.resetPasswordToken

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.resetPasswordToken.IResetPasswordTokenRepository
import com.radiotelescope.repository.resetPasswordToken.ResetPasswordToken
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import liquibase.integration.spring.SpringLiquibase



@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class UserResetPasswordTokenWrapperTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }

        @Bean
        fun liquibase(): SpringLiquibase {
            val liquibase = SpringLiquibase()
            liquibase.setShouldRun(false)
            return liquibase
        }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var resetPasswordTokeRepo: IResetPasswordTokenRepository

    private lateinit var wrapper: UserResetPasswordTokenWrapper
    private lateinit var factory: ResetPasswordTokenFactory
    private lateinit var user: User
    private lateinit var token: ResetPasswordToken

    @Before
    fun init() {
        // Initialize the factory and wrapper
        factory = BaseResetPasswordTokenFactory(
                resetPasswordTokenRepo = resetPasswordTokeRepo,
                userRepo = userRepo
        )
        wrapper = UserResetPasswordTokenWrapper(factory)

        // Persist user and token
        user = testUtil.createUser("rpim@ycp.edu")
        token = testUtil.createResetPasswordToken(user)
    }

    @Test
    fun testValid_CreateResetPasswordToken_Success(){
        val (token, error) = wrapper.requestPasswordReset(
                email = user.email
        ).execute()

        assertNotNull(token)
        assertNull(error)
    }

    @Test
    fun testValid_ResetPassword_Success(){
        val (id, error) = wrapper.resetPassword(
                request = ResetPassword.Request(
                       password = "ValidPassword1",
                       passwordConfirm = "ValidPassword1"
                ),
                token = token.token
        ).execute()

        assertNotNull(id)
        assertNull(error)
    }
}