package com.radiotelescope.controller.user

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.User
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
@ActiveProfiles("test")
internal class UserDeleteControllerTest : BaseUserRestControllerTest() {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var userDeleteController: UserDeleteController

    private lateinit var user: User

    private var userContext = getContext()

    @Before
    override fun init() {
        super.init()

        user = testUtil.createUser("rpim@ycp.edu")

        // Simulate a login
        userContext.login(user.id)
        userContext.currentRoles.add(UserRole.Role.USER)

        userDeleteController = UserDeleteController(
                userWrapper = getWrapper(),
                logger = getLogger()
        )
    }

    @Test
    fun testSuccessResponse() {
        // Test the success scenario to ensure the result
        // object is correctly set
        val result = userDeleteController.execute(user.id)

        assertNotNull(result)
        assertEquals(user.id, result.data)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testFailedValidationResponse() {
        // Test the scenario where the validation
        // in the command object fails
        userContext.currentRoles.add(UserRole.Role.ADMIN)

        val result = userDeleteController.execute(123456)

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertEquals(1, result.errors!!.size)

        // Ensure a log was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.BAD_REQUEST.value(), it.status)
        }
    }

    @Test
    fun testValidForm_FailedAuthenticationResponse() {
        // Test the scenario where the authentication
        // in the wrapper fails

        // Simulate a log out
        userContext.logout()

        val result = userDeleteController.execute(user.id)

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.FORBIDDEN, result.status)
        assertEquals(1, result.errors!!.size)

        // Ensure a log was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.FORBIDDEN.value(), it.status)
        }
    }
}