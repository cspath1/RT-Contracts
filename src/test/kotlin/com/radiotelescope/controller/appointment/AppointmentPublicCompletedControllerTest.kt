package com.radiotelescope.controller.appointment

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.Appointment
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
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class AppointmentPublicCompletedControllerTest : BaseAppointmentRestControllerTest() {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var appointmentPublicCompletedController: AppointmentPublicCompletedController
    private lateinit var user: User

    @Before
    override fun init() {
        super.init()

        appointmentPublicCompletedController = AppointmentPublicCompletedController(
                appointmentWrapper = getCoordinateCreateWrapper(),
                logger = getLogger()
        )

       user = testUtil.createUser("cspath1@ycp.edu")

        // Create two public completed appointments
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.COMPLETED,
                startTime = Date(System.currentTimeMillis() - 50000L),
                endTime = Date(System.currentTimeMillis() - 25000L),
                isPublic = true,
                type = Appointment.Type.POINT
        )

        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.COMPLETED,
                startTime = Date(System.currentTimeMillis() - 15000L),
                endTime = Date(System.currentTimeMillis() - 5000L),
                isPublic = true,
                type = Appointment.Type.POINT
        )
    }

    @Test
    fun testSuccessResponse() {
        // Log the user in
        getContext().login(user.id)
        getContext().currentRoles.add(UserRole.Role.USER)

        val result = appointmentPublicCompletedController.execute(
                pageNumber = 0,
                pageSize = 25
        )

        assertNotNull(result)
        assertTrue(result.data is Page<*>)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // A log should have been created for each record returned (2)
        assertEquals(2, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testInvalidPageParamsResponse() {
        // Log the user in
        getContext().login(user.id)
        getContext().currentRoles.add(UserRole.Role.USER)

        val result = appointmentPublicCompletedController.execute(
                pageNumber = -420,
                pageSize = -311
        )

        assertNotNull(result)
        assertNull(result.data)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertNotNull(result.errors)

        // Ensure a log was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.BAD_REQUEST.value(), it.status)
        }
    }

    @Test
    fun testFailedAuthenticationResponse() {
        // Do not log the user in
        val result = appointmentPublicCompletedController.execute(
                pageNumber = 0,
                pageSize = 25
        )

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