package com.radiotelescope.controller.appointment.manual

import com.radiotelescope.controller.appointment.BaseAppointmentRestControllerTest
import com.radiotelescope.controller.model.appointment.AddFreeControlAppointmentCommandForm
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
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
internal class AddFreeControlAppointmentCommandControllerTest : BaseAppointmentRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var addFreeControlAppointmentCommandController: AddFreeControlAppointmentCommandController
    private lateinit var user: User
    private lateinit var appointment: Appointment
    private lateinit var baseForm: AddFreeControlAppointmentCommandForm

    @Before
    override fun init() {
        super.init()

        addFreeControlAppointmentCommandController = AddFreeControlAppointmentCommandController(
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
                priority = Appointment.Priority.MANUAL,
                type = Appointment.Type.FREE_CONTROL
        )

        baseForm = AddFreeControlAppointmentCommandForm(
                hours = 1,
                minutes = 2,
                declination = 4.20
        )
    }

    @Test
    fun testSuccessResponse() {
        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER))

        val result = addFreeControlAppointmentCommandController.execute(baseForm, appointment.id)

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
        // Make a copy of the form with a null hours field
        val formCopy = baseForm.copy(
                hours = null
        )

        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER))

        val result = addFreeControlAppointmentCommandController.execute(formCopy, appointment.id)

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
    fun testValidForm_FailedValidationResponse() {
        // Make a copy of the form with an invalid hours field
        val formCopy = baseForm.copy(
                hours = 311
        )

        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER))

        val result = addFreeControlAppointmentCommandController.execute(formCopy, appointment.id)

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
    fun testFailedAuthenticationResponse() {
        // Do not log the user in
        val result = addFreeControlAppointmentCommandController.execute(baseForm, appointment.id)

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