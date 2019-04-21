package com.radiotelescope.controller.viewer

import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.User
import com.radiotelescope.service.ses.MockAwsSesSendService
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
internal class ViewerSharePrivateControllerTest : BaseViewerRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private lateinit var viewerSharePrivateController: ViewerSharePrivateController
    private lateinit var user: User
    private lateinit var researcher: User
    private lateinit var appointment: Appointment

    @Before
    override fun init() {
        super.init()

        viewerSharePrivateController = ViewerSharePrivateController(
                appointmentRepo = appointmentRepo,
                viewerWrapper = getWrapper(),
                logger = getLogger(),
                awsSesSendService = MockAwsSesSendService(true)
        )

        user = testUtil.createUser("rpim@ycp.edu")
        researcher = testUtil.createUser("rpim1@ycp.edu")

        appointment = testUtil.createAppointment(
                user = researcher,
                startTime = Date(System.currentTimeMillis() + 100000L),
                endTime = Date(System.currentTimeMillis()  +  200000L),
                status = Appointment.Status.SCHEDULED,
                isPublic = false,
                telescopeId = 1L,
                type = Appointment.Type.POINT
        )
    }

    @Test
    fun testSuccessResponse() {
        // Test the success scenario to ensure
        // the result object is correctly set

        // Simulate a login
        getContext().login(researcher.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.RESEARCHER, UserRole.Role.USER))

        val result = viewerSharePrivateController.execute(
                appointmentId = appointment.id,
                email = user.email
        )

        assertNotNull(result)
        assertTrue(result.data is Long)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())
    }

    @Test
    fun testFailedValidationResponse() {
        // Test the failure scenario to ensure
        // the result object is correctly set

        // Simulate a login
        getContext().login(researcher.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.RESEARCHER, UserRole.Role.USER))

        val result = viewerSharePrivateController.execute(
                appointmentId = appointment.id,
                email = "thom@yorke.com"
        )

        assertNotNull(result)
        assertNull(result.data)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertNotNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

    }

    @Test
    fun testFailedAuthenticationResponse() {
        // Test the failure scenario to ensure
        // the result object is correctly set

        // Do not simulate a login

        val result = viewerSharePrivateController.execute(
                appointmentId = appointment.id,
                email = user.email
        )

        assertNotNull(result)
        assertNull(result.data)
        assertEquals(HttpStatus.FORBIDDEN, result.status)
        assertNotNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())
    }

    @Test
    fun testInvalidResourceIdResponse() {
        // Test the failure scenario where the
        // appointment id does not exist
        val result = viewerSharePrivateController.execute(
                appointmentId = 311L,
                email = user.email
        )

        assertNotNull(result)
        assertNull(result.data)
        assertEquals(HttpStatus.NOT_FOUND, result.status)
        assertNotNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())
    }
}