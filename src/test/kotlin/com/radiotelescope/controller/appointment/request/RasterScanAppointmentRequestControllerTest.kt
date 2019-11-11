package com.radiotelescope.controller.appointment.request

import com.radiotelescope.controller.appointment.BaseAppointmentRestControllerTest
import com.radiotelescope.controller.model.appointment.request.RasterScanAppointmentRequestForm
import com.radiotelescope.controller.model.coordinate.CoordinateForm
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.services.ses.MockAwsSesSendService
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
internal class RasterScanAppointmentRequestControllerTest : BaseAppointmentRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    private lateinit var rasterScanAppointmentRequestController: RasterScanAppointmentRequestController
    private lateinit var user: User

    private val coordinateFormOne = CoordinateForm(
            hours = 12,
            minutes = 12,
            declination = 45.0
    )

    private val coordinateFormTwo = CoordinateForm(
            hours = 13,
            minutes = 13,
            declination = 50.0
    )

    private val baseForm = RasterScanAppointmentRequestForm(
            userId = -1L,
            telescopeId = 1L,
            startTime = Date(System.currentTimeMillis() + 100000L),
            endTime = Date(System.currentTimeMillis() + 300000L),
            isPublic = true,
            priority = Appointment.Priority.PRIMARY,
            coordinates = mutableListOf(coordinateFormOne, coordinateFormTwo)
    )

    @Before
    override fun init() {
        super.init()

        rasterScanAppointmentRequestController = RasterScanAppointmentRequestController(
                autoAppointmentWrapper = getRasterScanCreateWrapper(),
                awsSesSendService = MockAwsSesSendService(true),
                userRepo = userRepo,
                logger = getLogger()
        )

        user = testUtil.createUser("cspath1@ycp.edu")

        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.MEMBER,
                isApproved = true
        )
    }

    @Test
    fun testSuccessResponse() {
        // Make a copy fo the base for with a valid user id
        val formCopy = baseForm.copy(
                userId = user.id
        )

        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.MEMBER, UserRole.Role.USER))

        val result = rasterScanAppointmentRequestController.execute(formCopy)

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
        // Make a copy of the form with a null start time
        val formCopy = baseForm.copy(
                userId = user.id,
                startTime = null
        )

        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.MEMBER, UserRole.Role.USER))

        val result = rasterScanAppointmentRequestController.execute(formCopy)

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
        // Make a copy of the form with a start time in the past
        val formCopy = baseForm.copy(
                userId = user.id,
                startTime = Date(System.currentTimeMillis() - 10000L)
        )

        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.MEMBER, UserRole.Role.USER))

        val result = rasterScanAppointmentRequestController.execute(formCopy)

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
        val formCopy = baseForm.copy(
                userId = user.id
        )

        val result = rasterScanAppointmentRequestController.execute(formCopy)

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