package com.radiotelescope.controller.user

import com.radiotelescope.controller.model.user.UpdateForm
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.User
import com.radiotelescope.services.sns.MockAwsSnsService
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class UserUpdateControllerTest : BaseUserRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var userUpdateController: UserUpdateController

    private lateinit var baseForm: UpdateForm

    private lateinit var user: User

    private var userContext = getContext()

    @Before
    override fun init() {
        super.init()

        user = testUtil.createUser("rpim@ycp.edu")
        // Simulate a login
        userContext.login(user.id)
        userContext.currentRoles.add(UserRole.Role.USER)

        userUpdateController = UserUpdateController(
                userWrapper = getWrapper(),
                awsSnsService = MockAwsSnsService(true),
                logger = getLogger()
        )

        userUpdateController.defaultSendTopic = "testARN"

        baseForm = UpdateForm(
                id = user.id,
                firstName = "firstname",
                lastName = "lastname",
                company = "company",
                phoneNumber = "0001112222",
                profilePicture = "firstnamepic.jpg",
                notificationType = "SMS"
        )
    }

    @Test
    fun testSuccessResponse() {
        // Test the success scenario to ensure the result
        // is correct
        val result = userUpdateController.execute(
                userId = user.id,
                form = baseForm
        )

        assertNotNull(result)
        assertEquals(user.id, result.data)
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
        // Test the scenario where the form's validate
        // request method fails to ensure the result object
        // has the correct properties
        val formCopy = baseForm.copy(firstName = "")

        val result = userUpdateController.execute(
                userId = user.id,
                form = formCopy
        )
        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertEquals(1, result.errors!!.size)
    }

    @Test
    fun testValidForm_FailedValidationResponse() {
        // Test the scenario where the form is valid,
        // but validation in the command object fails

        // Create a form with the same password as the current password
        val formCopy = baseForm.copy(firstName = "rathana".repeat(50))

        val result = userUpdateController.execute(
                userId = user.id,
                form = formCopy
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

    @Test
    fun testValidForm_FailedAuthenticationResponse() {
        // Test the scenario where the form is valid,
        // but the authentication in the wrapper fails

        // Simulate a log out
        userContext.logout()

        val result = userUpdateController.execute(
                userId = user.id,
                form = baseForm
        )

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.FORBIDDEN, result.status)
        assertEquals(1, result.errors!!.size)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.FORBIDDEN.value(), it.status)
        }
    }

    @Test
    fun testValidForm_InvalidResourceResponse() {
        // Test the scenario where the form is valid,
        // but the user id does not exist
        val formCopy = baseForm.copy(
                id = 420L
        )

        val result = userUpdateController.execute(
                userId = 420L,
                form = formCopy
        )

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.NOT_FOUND, result.status)
        assertEquals(1, result.errors!!.size)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.NOT_FOUND.value(), it.status)
        }
    }
}