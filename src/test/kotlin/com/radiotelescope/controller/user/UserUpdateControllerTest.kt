package com.radiotelescope.controller.user

import com.radiotelescope.TestUtil
import com.radiotelescope.controller.model.user.UpdateForm
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.User
import liquibase.integration.spring.SpringLiquibase
import org.junit.Assert
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
@ActiveProfiles("test")
internal class UserUpdateControllerTest : BaseUserRestControllerTest() {
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

    private lateinit var userUpdateController: UserUpdateController

    private lateinit var baseForm: UpdateForm

    private lateinit var user: User

    private var userContext = getContext()

    @Before
    override fun init() {
        super.init()

        user = testUtil.createUser(
                email = "rpim@ycp.edu",
                accountHash = "Test Account"
        )
        // Simulate a login
        userContext.login(user.id)
        userContext.currentRoles.add(UserRole.Role.USER)

        userUpdateController = UserUpdateController(
                userWrapper = getWrapper(),
                logger = getLogger()
        )

        baseForm = UpdateForm(
                id = user.id,
                firstName = "firstname",
                lastName = "lastname",
                company = "company",
                phoneNumber = "0001112222"
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

        Assert.assertNotNull(result)
        Assert.assertEquals(user.id, result.data)
        Assert.assertEquals(HttpStatus.OK, result.status)
        Assert.assertNull(result.errors)
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
        Assert.assertNotNull(result)
        Assert.assertNull(result.data)
        Assert.assertNotNull(result.errors)
        Assert.assertEquals(HttpStatus.BAD_REQUEST, result.status)
        Assert.assertEquals(1, result.errors!!.size)
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
        Assert.assertNotNull(result)
        Assert.assertNull(result.data)
        Assert.assertNotNull(result.errors)
        Assert.assertEquals(HttpStatus.BAD_REQUEST, result.status)
        Assert.assertEquals(1, result.errors!!.size)
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

        Assert.assertNotNull(result)
        Assert.assertNull(result.data)
        Assert.assertNotNull(result.errors)
        Assert.assertEquals(HttpStatus.FORBIDDEN, result.status)
        Assert.assertEquals(1, result.errors!!.size)
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

        Assert.assertNotNull(result)
        Assert.assertNull(result.data)
        Assert.assertNotNull(result.errors)
        Assert.assertEquals(HttpStatus.NOT_FOUND, result.status)
        Assert.assertEquals(1, result.errors!!.size)
    }
}