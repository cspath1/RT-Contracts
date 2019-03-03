package com.radiotelescope.controller.resetPasswordToken

import com.radiotelescope.TestUtil
import com.radiotelescope.controller.model.resetPasswordToken.UpdateForm
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.resetPasswordToken.ResetPasswordToken
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class UserResetPasswordControllerTest : BaseResetPasswordTokenRestControllerTest() {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var userResetPasswordController: UserResetPasswordController
    private lateinit var resetPasswordToken: ResetPasswordToken

    private val baseForm = UpdateForm(
            password = "Password1@",
            passwordConfirm = "Password1@"
    )

    @Before
    override fun init() {
        super.init()

        userResetPasswordController = UserResetPasswordController(
                resetPasswordTokenWrapper = getWrapper(),
                logger = getLogger()
        )

        val user = testUtil.createUser("cspath1@ycp.edu")

        resetPasswordToken = testUtil.createResetPasswordToken(user)
    }

    @Test
    fun testSuccessResponse() {
        // Test the success scenario to ensure the
        // result object is correctly set
        val result = userResetPasswordController.execute(
                token = resetPasswordToken.token,
                form = baseForm
        )

        assertNotNull(result)
        assertTrue(result.data is Long)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testInvalidFormResponse() {
        // Test the scenario where the form's validate request
        // method fails to ensure the result object has the
        // correct properties
        val formCopy = baseForm.copy(
                password = "password",
                passwordConfirm = "password"
        )

        val result = userResetPasswordController.execute(
                token = resetPasswordToken.token,
                form = formCopy
        )

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertEquals(1, result.errors!!.size)

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.BAD_REQUEST.value(), it.status)
        }
    }

    @Test
    fun testValidForm_FailedValidationResponse() {
        // Test the scenario where the form is valid,
        // but validation in the command object fails

        // Pass in an invalid password reset token
        val result = userResetPasswordController.execute(
                token = "AToken",
                form = baseForm
        )

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertEquals(1, result.errors!!.size)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.BAD_REQUEST.value(), it.status)
        }
    }
}