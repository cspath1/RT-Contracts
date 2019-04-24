package com.radiotelescope.contracts.appointment.manual

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
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
internal class StopFreeControlAppointmentTest : AbstractSpringTest() {
    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private lateinit var user: User
    private lateinit var appointment: Appointment
    private lateinit var endTime: Date

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
                priority = Appointment.Priority.MANUAL,
                type = Appointment.Type.FREE_CONTROL
        )
        endTime = appointment.endTime
    }

    @Test
    fun testValidConstraints_Success() {
        // Execute the command
        val (id, errors) = StopFreeControlAppointment(
                appointmentId = appointment.id,
                appointmentRepo = appointmentRepo
        ).execute()

        // Make sure it was a success
        assertNotNull(id)
        assertNull(errors)

        // Make sure the appointment was properly modified
        val theAppointment = appointmentRepo.findById(id!!).get()

        assertEquals(Appointment.Status.COMPLETED, theAppointment.status)
        assertEquals(0, theAppointment.coordinateList.size)
        assertNotEquals(endTime, theAppointment.endTime)
    }

    @Test
    fun testInvalidAppointmentId_Failure() {
        // Execute the command
        val (id, errors) = StopFreeControlAppointment(
                appointmentId = 311L,
                appointmentRepo = appointmentRepo
        ).execute()

        // Make sure it was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.ID].isNotEmpty())
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
                priority = Appointment.Priority.MANUAL,
                type = Appointment.Type.POINT
        )

        // Execute the command w/ the new appointment
        val (id, errors) = StopFreeControlAppointment(
                appointmentId = theAppointment.id,
                appointmentRepo = appointmentRepo
        ).execute()

        // Make sure it was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.TYPE].isNotEmpty())
    }

    @Test
    fun testAppointmentAlreadyCompleted_Failure() {
        // Update the appointment to be completed
        appointment.status = Appointment.Status.COMPLETED
        appointment.endTime = Date()
        appointmentRepo.save(appointment)

        val (id, errors) = StopFreeControlAppointment(
                appointmentId = appointment.id,
                appointmentRepo = appointmentRepo
        ).execute()

        // Make sure it was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.STATUS].isNotEmpty())
    }
}