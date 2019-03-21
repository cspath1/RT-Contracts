package com.radiotelescope.controller.appointment

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles("test")
internal class AppointmentSearchControllerTest : BaseAppointmentRestControllerTest() {
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
    private lateinit var logRepo: ILogRepository

    private lateinit var appointmentSearchController: AppointmentSearchController

    private val userContext = getContext()

    @Before
    override fun init() {
        super.init()

        // Create a user and an appointment
        val user = testUtil.createUser("cspath1@ycp.edu")
        user.firstName = "Cody"
        user.lastName = "Spath"
        userRepo.save(user)

        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis() + 100000L),
                endTime = Date(System.currentTimeMillis() + 200000L),
                isPublic = true,
                type = Appointment.Type.POINT
        )

        // Simulate a login
        userContext.login(user.id)
        userContext.currentRoles.add(UserRole.Role.USER)

        appointmentSearchController = AppointmentSearchController(
                appointmentWrapper = getCoordinateCreateWrapper(),
                logger = getLogger()
        )
    }

    @Test
    fun testSuccessResponse_FullName() {
        // Test the success response scenario to ensure the result
        // object is correctly set
        val result = appointmentSearchController.execute(
                pageNumber = 0,
                pageSize = 15,
                search = "userFullName",
                value = "cody spath"
        )

        assertNotNull(result)
        assertTrue(result.data is Page<*>)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        assertEquals(logRepo.count(), 1)

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testSuccessResponse_FirstAndLastName() {
        // Test the success response scenario to ensure the result
        // object is correctly set
        val result = appointmentSearchController.execute(
                pageNumber = 0,
                pageSize = 15,
                search = "userFirstName+userLastName",
                value = "cody"
        )

        assertNotNull(result)
        assertTrue(result.data is Page<*>)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        assertEquals(logRepo.count(), 1)

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testErrorResponse() {
        // Test the scenario where the business logic did not pass
        val result = appointmentSearchController.execute(
                pageNumber = 0,
                pageSize = 15,
                search = "userSearchName",
                value = "cody"
        )

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertEquals(1, result.errors!!.size)

        assertEquals(logRepo.count(), 1)

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.BAD_REQUEST.value(), it.status)
        }
    }

    @Test
    fun testFailedAuthenticationResponse() {
        // Test the scenario where the authentication
        // in the wrapper fails

        // Simulate a logout
        userContext.logout()

        val result = appointmentSearchController.execute(
                pageNumber = 0,
                pageSize = 15,
                search = "firstName",
                value = "weuibgwoie"
        )

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.FORBIDDEN, result.status)
        assertEquals(1, result.errors!!.size)

        assertEquals(logRepo.count(), 1)

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.FORBIDDEN.value(), it.status)
        }
    }

    @Test
    fun testInvalidPageParametersResponse() {
        // Test the scenario where the page parameters supplied
        // are invalid
        val result = appointmentSearchController.execute(
                pageNumber = -1,
                pageSize = -1,
                search = "firstName",
                value = "weibuwibeion"
        )

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertEquals(1, result.errors!!.size)

        assertEquals(logRepo.count(), 1)

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.BAD_REQUEST.value(), it.status)
        }
    }
}