package com.radiotelescope.contracts.viewer

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.repository.viewer.IViewerRepository
import com.radiotelescope.repository.viewer.Viewer
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
@ActiveProfiles(value = ["test"])
internal class UnSharePrivateAppointmentTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var viewerRepo: IViewerRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private lateinit var user: User
    private lateinit var otherUser: User
    private lateinit var appointment: Appointment
    private lateinit var viewer: Viewer

    @Before
    fun setUp() {
        user = testUtil.createUser("rpim@ycp.edu")
        otherUser = testUtil.createUser("rpim1@ycp.edu")
        appointment = testUtil.createAppointment(
                user = otherUser,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis() + 100000L),
                endTime = Date(System.currentTimeMillis() + 300000L),
                isPublic = false,
                type = Appointment.Type.POINT
        )

        viewer = testUtil.createViewer(user, appointment)
    }

    @Test
    fun testValidConstraints_Success() {
        // CoordinateCreate the request
        val request = UnsharePrivateAppointment.Request(
                userId = user.id,
                appointmentId = appointment.id
        )

        // Execute the command
        val (id, errors) = UnsharePrivateAppointment(
                request = request,
                viewerRepo = viewerRepo,
                userRepo = userRepo,
                appointmentRepo = appointmentRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)

        assertEquals(viewer.id, id)
    }

    @Test
    fun testInvalid_UserDoesNotExist_Failure() {
        // CoordinateCreate the request
        val request = UnsharePrivateAppointment.Request(
                userId = 1234L,
                appointmentId = appointment.id
        )

        // Execute the command
        val (id, errors) = UnsharePrivateAppointment(
                request = request,
                viewerRepo = viewerRepo,
                userRepo = userRepo,
                appointmentRepo = appointmentRepo
        ).execute()

        // Make sure the command was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.USER_ID].isNotEmpty())
    }

    @Test
    fun testInvalid_AppointmentDoesNotExist_Failure() {
        // CoordinateCreate the request
        val request = UnsharePrivateAppointment.Request(
                userId = user.id,
                appointmentId = 1234L
        )

        // Execute the command
        val (id, errors) = UnsharePrivateAppointment(
                request = request,
                viewerRepo = viewerRepo,
                userRepo = userRepo,
                appointmentRepo = appointmentRepo
        ).execute()

        // Make sure the command was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.APPOINTMENT_ID].isNotEmpty())
    }

    @Test
    fun testInvalid_NotShared_Failure() {
        viewerRepo.delete(viewer)

        // CoordinateCreate the request
        val request = UnsharePrivateAppointment.Request(
                userId = user.id,
                appointmentId = appointment.id
        )

        // Execute the command
        val (id, errors) = UnsharePrivateAppointment(
                request = request,
                viewerRepo = viewerRepo,
                userRepo = userRepo,
                appointmentRepo = appointmentRepo
        ).execute()

        // Make sure the command was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.ID].isNotEmpty())
    }
}