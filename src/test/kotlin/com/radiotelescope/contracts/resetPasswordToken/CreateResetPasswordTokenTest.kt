package com.radiotelescope.contracts.resetPasswordToken

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.resetPasswordToken.IResetPasswordTokenRepository
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

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class CreateResetPasswordTokenTest {
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

    private lateinit var user: User

    @Before
    fun setUp() {
        user = testUtil.createUser(
                email = "rpim@ycp.edu",
                accountHash = "Test Account 1"
        )
    }

    @Test
    fun testValid_UserExist_Success(){
        // Execute the command
        val (token, errors) = CreateResetPasswordToken(
                email = user.email,
                userRepo = userRepo,
                resetPasswordTokenRepo = resetPasswordTokenRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(token)
        assertNull(errors)
    }

    @Test
    fun testInvalid_UserDoesNotExist_Failure(){
        // Execute the command
        val (token, errors) = CreateResetPasswordToken(
                email = "",
                userRepo = userRepo,
                resetPasswordTokenRepo = resetPasswordTokenRepo
        ).execute()

        // Make sure the command was a failure
        assertNull(token)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.EMAIL].isNotEmpty())
    }

    @Test
    fun testValid_OnlyOneToken_Success(){
        testUtil.createResetPasswordToken(user)

        // Execute the command
        val (token, errors) = CreateResetPasswordToken(
                email = user.email,
                userRepo = userRepo,
                resetPasswordTokenRepo = resetPasswordTokenRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(token)
        assertNull(errors)

        // Make sure there's only one token
        assertEquals(1, resetPasswordTokenRepo.findAll().count())
    }
}