package com.radiotelescope.contracts.appointment.manual

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.user.User
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
internal class AddFreeControlAppointmentCommandTest : AbstractSpringTest() {
    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    private lateinit var baseRequest: AddFreeControlAppointmentCommand.Request

    private lateinit var user: User
    private lateinit var appointment: Appointment

    @Before
    fun setUp() {
        user = testUtil.createUser("cspath1@ycp.edu")

        appointment = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.IN_PROGRESS,
                startTime = Date(System.currentTimeMillis() - 150000L),
                endTime = Date(System.currentTimeMillis() + 150000L),
                isPublic = true,
                type = Appointment.Type.FREE_CONTROL
        )
        assertEquals(1, coordinateRepo.count())

        baseRequest = AddFreeControlAppointmentCommand.Request(
                appointmentId = appointment.id,
                hours = 5,
                minutes = 34,
                seconds = 32,
                declination = 22.0
        )
    }

    @Test
    fun testValidConstraints_Success() {
        // Execute the command
        val (id, errors) = AddFreeControlAppointmentCommand(
                request = baseRequest,
                appointmentRepo = appointmentRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure it was a success
        assertNotNull(id)
        assertNull(errors)

        // There should now be two coordinates
        assertEquals(2, coordinateRepo.count())
    }

    @Test
    fun testInvalidAppointmentId_Failure() {
        // Create a copy of the request with an invalid appointment id
        val requestCopy = baseRequest.copy(
                appointmentId = 311L
        )

        // Execute the command
        val (id, errors) = AddFreeControlAppointmentCommand(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure it was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.ID].isNotEmpty())
    }

    @Test
    fun testHoursTooLow_Failure() {
        // Create a copy of the request with an invalid hours field
        val requestCopy = baseRequest.copy(
                hours = -311
        )

        // Execute the command
        val (id, errors) = AddFreeControlAppointmentCommand(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure it was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.HOURS].isNotEmpty())
    }

    @Test
    fun testHoursTooHigh_Failure() {
        // Create a copy of the request with an invalid hours field
        val requestCopy = baseRequest.copy(
                hours = 311
        )

        // Execute the command
        val (id, errors) = AddFreeControlAppointmentCommand(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure it was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.HOURS].isNotEmpty())
    }

    @Test
    fun testMinutesTooLow_Failure() {
        // Create a copy of the request with an invalid minutes
        val requestCopy = baseRequest.copy(
                minutes = -311
        )

        // Execute the command
        val (id, errors) = AddFreeControlAppointmentCommand(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure it was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.MINUTES].isNotEmpty())
    }

    @Test
    fun testMinutesTooHigh_Failure() {
        // Create a copy of the request with an invalid minutes
        val requestCopy = baseRequest.copy(
                minutes = 311
        )

        // Execute the command
        val (id, errors) = AddFreeControlAppointmentCommand(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure it was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.MINUTES].isNotEmpty())
    }

    @Test
    fun testSecondsTooLow_Failure() {
        // Create a copy of the request with invalid seconds
        val requestCopy = baseRequest.copy(
                seconds = -311
        )

        // Execute the command
        val (id, errors) = AddFreeControlAppointmentCommand(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure it was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.SECONDS].isNotEmpty())
    }

    @Test
    fun testSecondsTooHigh_Failure() {
        // Create a copy of the request with invalid seconds
        val requestCopy = baseRequest.copy(
                seconds = 311
        )

        // Execute the command
        val (id, errors) = AddFreeControlAppointmentCommand(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure it was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.SECONDS].isNotEmpty())
    }

    @Test
    fun testDeclinationTooLow_Failure() {
        // Create a copy of the request with an invalid declination
        val requestCopy = baseRequest.copy(
                declination = -311.0
        )

        // Execute the command
        val (id, errors) = AddFreeControlAppointmentCommand(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure it was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.DECLINATION].isNotEmpty())
    }

    @Test
    fun testDeclinationTooHigh_Failure() {
        // Create a copy of the request with an invalid declination
        val requestCopy = baseRequest.copy(
                declination = 311.0
        )

        // Execute the command
        val (id, errors) = AddFreeControlAppointmentCommand(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure it was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.DECLINATION].isNotEmpty())
    }

    @Test
    fun testAppointmentNotManual_Failure() {
        val theAppointment = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.IN_PROGRESS,
                startTime = Date(System.currentTimeMillis() - 150000L),
                endTime = Date(System.currentTimeMillis() + 150000L),
                isPublic = true,
                type = Appointment.Type.POINT
        )

        // Create a copy of the request with the new appointment id
        val requestCopy = baseRequest.copy(
                appointmentId = theAppointment.id
        )

        // Execute the command
        val (id, errors) = AddFreeControlAppointmentCommand(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure it was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.TYPE].isNotEmpty())
    }

    @Test
    fun testAppointmentNotInProgress_Failure() {
        // Update the appointment to be completed
        appointment.status = Appointment.Status.COMPLETED
        appointment.endTime = Date()
        appointmentRepo.save(appointment)

        val (id, errors) = AddFreeControlAppointmentCommand(
                request = baseRequest,
                appointmentRepo = appointmentRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure it was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.STATUS].isNotEmpty())
    }
}