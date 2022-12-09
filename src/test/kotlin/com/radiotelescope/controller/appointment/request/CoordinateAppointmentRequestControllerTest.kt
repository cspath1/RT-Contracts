package com.radiotelescope.controller.appointment.request

import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.controller.appointment.BaseAppointmentRestControllerTest
import com.radiotelescope.controller.model.appointment.request.CoordinateAppointmentRequestForm
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
internal class CoordinateAppointmentRequestControllerTest : BaseAppointmentRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    private lateinit var coordinateAppointmentRequestController: CoordinateAppointmentRequestController
    private lateinit var user: User

    private val baseForm = CoordinateAppointmentRequestForm(
            userId = -1L,
            startTime = Date(System.currentTimeMillis() + 50000L),
            endTime = Date(System.currentTimeMillis() + 100000L),
            telescopeId = 1L,
            isPublic = true,
            hours = 22,
            minutes = 30,
            declination = 42.0,
            priority = Appointment.Priority.PRIMARY
    )

    @Before
    override fun init() {
        super.init()

        coordinateAppointmentRequestController = CoordinateAppointmentRequestController(
                autoAppointmentWrapper = getCoordinateCreateWrapper(),
                awsSesSendService = MockAwsSesSendService(true),
                logger = getLogger(),
                userRepo = userRepo
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
        // Make a copy of the base form with a valid id
        val formCopy = baseForm.copy(user.id)

        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.MEMBER, UserRole.Role.USER))

        val result = coordinateAppointmentRequestController.execute(formCopy)

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
        // Make a copy of the form with a valid id and null declination
        val formCopy = baseForm.copy(
                userId = user.id,
                declination = null
        )

        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.MEMBER, UserRole.Role.USER))

        val result = coordinateAppointmentRequestController.execute(formCopy)

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
        // Make a copy of the form with an valid id and invalid declination
        val formCopy = baseForm.copy(
                userId = user.id,
                declination = 420.0
        )

        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.MEMBER, UserRole.Role.USER))

        val result = coordinateAppointmentRequestController.execute(formCopy)

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

        val result = coordinateAppointmentRequestController.execute(formCopy)

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