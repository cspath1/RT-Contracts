package com.radiotelescope.controller.appointment

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.IUserRoleRepository
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
@ActiveProfiles(value = ["test"])
internal class UserAvailableTimeControllerTest : BaseAppointmentRestControllerTest() {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    private lateinit var userAvailableTimeController: UserAvailableTimeController
    private lateinit var user: User

    @Before
    override fun init() {
        super.init()

        userAvailableTimeController = UserAvailableTimeController(
                appointmentWrapper = getWrapper(),
                logger = getLogger()
        )

        user = testUtil.createUser("cspath1@ycp.edu")

        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.MEMBER,
                isApproved = true
        )

        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = 48*60*60*1000
        )
    }

    @Test
    fun testSuccessResponse() {
        // Test the success scenario to ensure
        // the result object is correctly set

        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.MEMBER, UserRole.Role.USER))

        val result = userAvailableTimeController.execute(user.id)

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
    fun testFailedValidationResponse() {
        // Delete the user's category of service
        userRoleRepo.findAll().forEach {
            if (it.role != UserRole.Role.USER) {
                userRoleRepo.delete(it)
            }
        }

        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.add(UserRole.Role.USER)

        val result = userAvailableTimeController.execute(user.id)

        assertNotNull(result)
        assertNull(result.data)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertNotNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.BAD_REQUEST.value(), it.status)
        }
    }

    @Test
    fun testFailedAuthenticationResponse() {
        // Do not log the user in
        val result = userAvailableTimeController.execute(user.id)

        assertNotNull(result)
        assertNull(result.data)
        assertEquals(HttpStatus.FORBIDDEN, result.status)
        assertNotNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.FORBIDDEN.value(), it.status)
        }
    }
}