package com.radiotelescope.contracts.resetPasswordToken

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.resetPasswordToken.IResetPasswordTokenRepository
import com.radiotelescope.repository.resetPasswordToken.ResetPasswordToken
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import liquibase.integration.spring.SpringLiquibase
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class ResetPasswordTest {
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
    private lateinit var resetPasswordTokenRepo: IResetPasswordTokenRepository

    private lateinit var token: ResetPasswordToken
    private lateinit var user: User

    @Before
    fun init(){
        // Persist the user and token
        user = testUtil.createUser(
                email = "rpim@ycp.edu",
                accountHash = "Test Account 1"
        )

        token = testUtil.createResetPasswordToken(user)
    }

    @Test
    fun testValid_CorrectContraints_Success(){
        val (id, error) = ResetPassword(
                request = ResetPassword.Request(
                        password = "ValidPassword1",
                        passwordConfirm = "ValidPassword1"
                ),
                token = token.token,
                resetPasswordTokenRepo = resetPasswordTokenRepo,
                userRepo = userRepo
        ).execute()

        //Should have passed
        assertNotNull(id)
        assertNull(error)

        // Ensure the token record was deleted
        assertEquals(0, resetPasswordTokenRepo.count())
    }

    @Test
    fun testInvalid_BlankPassword_Failure(){
        val (id, error) = ResetPassword(
                request = ResetPassword.Request(
                        password = "",
                        passwordConfirm = ""
                ),
                token = token.token,
                resetPasswordTokenRepo = resetPasswordTokenRepo,
                userRepo = userRepo
        ).execute()

        // Should have failed
        assertNull(id)
        assertNotNull(error)

        // Ensure it failed because of the password
        assertTrue(error!![ErrorTag.PASSWORD].isNotEmpty())
    }

    @Test
    fun testInvalid_PasswordsDoNotMatch_Failure(){
        val (id, error) = ResetPassword(
                request = ResetPassword.Request(
                        password = "ValidPassword",
                        passwordConfirm = "InvalidPassword"
                ),
                token = token.token,
                resetPasswordTokenRepo = resetPasswordTokenRepo,
                userRepo = userRepo
        ).execute()

        // Should have failed
        assertNull(id)
        assertNotNull(error)

        // Ensure it failed because of the password confirm not matching
        assertTrue(error!![ErrorTag.PASSWORD_CONFIRM].isNotEmpty())
    }

    @Test
    fun testInvalid_PasswordRegexNotAMatch_Failure(){
        val (id, error) = ResetPassword(
                request = ResetPassword.Request(
                        password = "Password",
                        passwordConfirm = "Password"
                ),
                token = token.token,
                resetPasswordTokenRepo = resetPasswordTokenRepo,
                userRepo = userRepo
        ).execute()

        // Should have failed
        assertNull(id)
        assertNotNull(error)

        // Ensure it failed because of the password
        assertTrue(error!![ErrorTag.PASSWORD].isNotEmpty())
    }

    @Test
    fun testInvalid_TokenNotFound_Failure() {
        val (id, error) = ResetPassword(
                request = ResetPassword.Request(
                        password = "Password1@",
                        passwordConfirm = "Password1@"
                ),
                token = "fake token",
                resetPasswordTokenRepo = resetPasswordTokenRepo,
                userRepo = userRepo
        ).execute()

        // Should have failed
        assertNull(id)
        assertNotNull(error)

        // Ensure it failed because of the token
        assertTrue(error!![ErrorTag.TOKEN].isNotEmpty())
    }

    @Test
    fun testInvalid_TokenExpired_Failure() {
        // make the token expired
        token.expirationDate = Date(System.currentTimeMillis() - 100000L)
        resetPasswordTokenRepo.save(token)

        val (id, error) = ResetPassword(
                request = ResetPassword.Request(
                        password = "Password1@",
                        passwordConfirm = "Password1@"
                ),
                token = "fake token",
                resetPasswordTokenRepo = resetPasswordTokenRepo,
                userRepo = userRepo
        ).execute()

        // Should have failed
        assertNull(id)
        assertNotNull(error)

        // Ensure it failed because of the token
        assertTrue(error!![ErrorTag.TOKEN].isNotEmpty())
    }
}