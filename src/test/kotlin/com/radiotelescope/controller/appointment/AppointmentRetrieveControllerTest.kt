package com.radiotelescope.controller.appointment

import com.radiotelescope.contracts.appointment.info.AppointmentInfo
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
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
internal class AppointmentRetrieveControllerTest : BaseAppointmentRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var appointmentRetrieveController: AppointmentRetrieveController
    private lateinit var user: User
    private lateinit var appointment: Appointment

    @Before
    override fun init() {
        super.init()

        appointmentRetrieveController = AppointmentRetrieveController(
                autoAppointmentWrapper = getCoordinateCreateWrapper(),
                logger = getLogger()
        )

        user = testUtil.createUser("cspath1@ycp.edu")

        appointment = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis() + 50000L),
                endTime = Date(System.currentTimeMillis() + 100000L),
                isPublic = false,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.POINT
        )
    }

    @Test
    fun testSuccessResponse() {
        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.add(UserRole.Role.USER)

        val result = appointmentRetrieveController.execute(appointment.id)

        assertNotNull(result)
        assertTrue(result.data is AppointmentInfo)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testInvalidResourceIdResponse() {
        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.add(UserRole.Role.USER)

        val result = appointmentRetrieveController.execute(420L)

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

    @Test
    fun testFailedAuthenticationResponse() {
        // Do not log the user in
        val result = appointmentRetrieveController.execute(appointment.id)

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