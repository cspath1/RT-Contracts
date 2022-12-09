package com.radiotelescope.controller.appointment.update

import com.radiotelescope.controller.appointment.BaseAppointmentRestControllerTest
import com.radiotelescope.controller.model.appointment.update.RasterScanAppointmentUpdateForm
import com.radiotelescope.controller.model.coordinate.CoordinateForm
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
internal class RasterScanAppointmentUpdateControllerTest : BaseAppointmentRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var rasterScanUpdateController: RasterScanAppointmentUpdateController
    private lateinit var user: User
    private lateinit var appointment: Appointment

    private lateinit var rasterScanAppointmentUpdateForm: RasterScanAppointmentUpdateForm

    @Before
    override fun init() {
        super.init()

        rasterScanUpdateController = RasterScanAppointmentUpdateController(
                autoAppointmentWrapper = getRasterScanCreateWrapper(),
                logger = getLogger()
        )

        user = testUtil.createUser("cspath1@ycp.edu")

        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )

        appointment = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis() + 50000L),
                endTime = Date(System.currentTimeMillis() + 100000L),
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.RASTER_SCAN
        )

        val coordinateFormOne = CoordinateForm(
                hours = 12,
                minutes = 12,
                declination = 45.0
        )

        val coordinateFormTwo = CoordinateForm(
                hours = 13,
                minutes = 13,
                declination = 50.0
        )

        rasterScanAppointmentUpdateForm = RasterScanAppointmentUpdateForm(
                startTime = appointment.startTime,
                endTime = appointment.endTime,
                telescopeId = appointment.telescopeId,
                isPublic = appointment.isPublic,
                priority = appointment.priority,
                coordinates = mutableListOf(coordinateFormOne, coordinateFormTwo)
        )
    }

    @Test
    fun testSuccessResponse() {
        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.RESEARCHER))

        val result = rasterScanUpdateController.execute(
                appointmentId = appointment.id,
                form = rasterScanAppointmentUpdateForm
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
        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.RESEARCHER))

        // Create a copy of the form
        val formCopy = rasterScanAppointmentUpdateForm.copy(
                startTime = null
        )

        val result = rasterScanUpdateController.execute(
                appointmentId = appointment.id,
                form = formCopy
        )

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
    fun testValidForm_FailedValidationResponse() {
        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.RESEARCHER))

        // Create a copy of the form
        val formCopy = rasterScanAppointmentUpdateForm.copy(
                telescopeId = 420L
        )

        val result = rasterScanUpdateController.execute(
                appointmentId = appointment.id,
                form = formCopy
        )

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
        val result = rasterScanUpdateController.execute(
                appointmentId = appointment.id,
                form = rasterScanAppointmentUpdateForm
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
    fun testInvalidResourceIdResponse() {
        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.RESEARCHER))

        val result = rasterScanUpdateController.execute(
                appointmentId = 311L,
                form = rasterScanAppointmentUpdateForm
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