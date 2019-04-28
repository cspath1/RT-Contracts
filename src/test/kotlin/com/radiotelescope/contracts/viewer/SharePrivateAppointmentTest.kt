package com.radiotelescope.contracts.viewer

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.repository.viewer.IViewerRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
internal class SharePrivateAppointmentTest : AbstractSpringTest() {
    @Autowired
    private lateinit var viewerRepo: IViewerRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private lateinit var user: User
    private lateinit var otherUser: User
    private lateinit var appointment: Appointment

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
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.POINT
        )
    }

    @Test
    fun testValidConstraints_Success() {
        //Create the request
        val request = SharePrivateAppointment.Request(
                email = user.email,
                appointmentId = appointment.id
        )

        // Execute the command
        val (id, errors) = SharePrivateAppointment(
                request = request,
                viewerRepo = viewerRepo,
                userRepo = userRepo,
                appointmentRepo = appointmentRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)

        // Make sure the viewer was persisted
        val theViewer = viewerRepo.findById(id!!)
        assertTrue(theViewer.isPresent)

        // Make sure the correct information was persisted
        assertEquals(request.email, theViewer.get().user.email)
        assertEquals(request.appointmentId, theViewer.get().appointment.id)
    }

    @Test
    fun testInvalid_UserDoesNotExist_Failure(){
        //Create the request
        val request = SharePrivateAppointment.Request(
                email = "michaelscott@dundermifflin.com",
                appointmentId = appointment.id
        )

        // Execute the command
        val (id, errors) = SharePrivateAppointment(
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
    fun testInvalid_AppointmentDoesNotExist_Failure(){
        // Create the request
        val request = SharePrivateAppointment.Request(
                email = user.email,
                appointmentId = -1L
        )

        // Execute the command
        val (id, errors) = SharePrivateAppointment(
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
    fun testInvalid_AppointmentIsNotPrivate_Failure(){
        val publicAppointment = testUtil.createAppointment(
                user = otherUser,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis() + 400000L),
                endTime = Date(System.currentTimeMillis() + 500000L),
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.POINT
        )

        // Create the request
        val request = SharePrivateAppointment.Request(
                email = user.email,
                appointmentId = publicAppointment.id
        )

        // Execute the command
        val (id, errors) = SharePrivateAppointment(
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
        assertTrue(errors[ErrorTag.PRIVATE].isNotEmpty())
    }

    @Test
    fun testInvalid_AppointmentIsAlreadyShared_Failure(){
        //Create viewer
        testUtil.createViewer(user, appointment)

        //Create the request
        val request = SharePrivateAppointment.Request(
                email = user.email,
                appointmentId = appointment.id
        )

        // Execute the command
        val (id, errors) = SharePrivateAppointment(
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