package com.radiotelescope.controller.appointment

import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.services.ses.MockAwsSesSendService
import com.radiotelescope.services.sns.MockAwsSnsService
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner
import java.util.*


@DataJpaTest
@RunWith(SpringRunner::class)
internal class AppointmentCancelControllerTest : BaseAppointmentRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    private lateinit var appointmentCancelController: AppointmentCancelController
    private lateinit var user: User
    private lateinit var appointment: Appointment

    @Before
    override fun init() {
        super.init()

        appointmentCancelController = AppointmentCancelController(
                autoAppointmentWrapper = getCoordinateCreateWrapper(),
                userRepo = userRepo,
                appointmentRepo = appointmentRepo,
                awsSesSendService = MockAwsSesSendService(true),
                awsSnsService = MockAwsSnsService(true),
                logger = getLogger()
        )

        user = testUtil.createUser("cspath1@ycp.edu")

        appointment = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis() + 50000L),
                endTime = Date(System.currentTimeMillis() + 100000L),
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.POINT
        )
    }

    @Test
    fun testSuccessResponse() {
        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.add(UserRole.Role.USER)

        val result = appointmentCancelController.execute(appointment.id)

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
        // Set the status to canceled and then call the method
        appointment.status = Appointment.Status.CANCELED
        appointmentRepo.save(appointment)

        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.add(UserRole.Role.USER)

        val result = appointmentCancelController.execute(appointment.id)

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
        val result = appointmentCancelController.execute(appointment.id)

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

    @Test
    fun testInvalidResourceIdResponse() {
        // Call the method on a record id that does not exist

        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.add(UserRole.Role.USER)

        val result = appointmentCancelController.execute(311L)

        assertNotNull(result)
        assertNull(result.data)
        assertEquals(HttpStatus.NOT_FOUND, result.status)
        assertNotNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.NOT_FOUND.value(), it.status)
        }
    }
}