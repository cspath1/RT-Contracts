package com.radiotelescope.controller.appointment.manual

import com.radiotelescope.controller.appointment.BaseAppointmentRestControllerTest
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
internal class CalibrateFreeControlAppointmentControllerTest : BaseAppointmentRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private lateinit var calibrateFreeControlAppointmentController: CalibrateFreeControlAppointmentController
    private lateinit var user: User
    private lateinit var appointment: Appointment

    @Before
    override fun init() {
        super.init()

        calibrateFreeControlAppointmentController = CalibrateFreeControlAppointmentController(
                wrapper = getFreeControlWrapper(),
                logger = getLogger()
        )

        user = testUtil.createUser("cspath1@ycp.edu")

        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.ADMIN,
                isApproved = true
        )

        appointment = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.IN_PROGRESS,
                startTime = Date(System.currentTimeMillis() - 150000L),
                endTime = Date(System.currentTimeMillis() + 150000L),
                isPublic = true,
                type = Appointment.Type.FREE_CONTROL
        )
    }

    @Test
    fun testSuccessResponse() {
        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER))

        val result = calibrateFreeControlAppointmentController.execute(appointment.id)

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
        // Mark the appointment as completed
        appointment.status = Appointment.Status.COMPLETED
        appointment.endTime = Date(System.currentTimeMillis())
        appointmentRepo.save(appointment)

        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER))

        val result = calibrateFreeControlAppointmentController.execute(appointment.id)

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
    fun testFailedAuthentication() {
        // Do not log the user in
        val result = calibrateFreeControlAppointmentController.execute(appointment.id)

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
}