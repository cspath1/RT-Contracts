package com.radiotelescope.contracts.resetPasswordToken

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.resetPasswordToken.IResetPasswordTokenRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
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
        user = testUtil.createUser("rpim@ycp.edu")
    }

    @Test
    fun testValid_UserExist_Success(){
        // Execute the command
        val (token, errors) = CreateResetPasswordToken(
                userId = user.id,
                userRepo = userRepo,
                resetPasswordTokenRepo = resetPasswordTokenRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(token)
        assertNull(errors)
    }

    @Test
    fun testValid_UserDoesNotExist_Success(){
        // Execute the command
        val (token, errors) = CreateResetPasswordToken(
                userId = 123456789,
                userRepo = userRepo,
                resetPasswordTokenRepo = resetPasswordTokenRepo
        ).execute()

        // Make sure the command was a success
        assertNull(token)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.USER_ID].isNotEmpty())
    }
}