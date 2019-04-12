package com.radiotelescope.contracts.appointment

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
internal class RetrieveTest : AbstractSpringTest() {
    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private lateinit var appointment: Appointment

    @Before
    fun setUp() {
        // Persist a user
        val user = testUtil.createUser("spathcody@gmail.com")

        // Persist an appointment
        appointment = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis() + 10000L),
                endTime = Date(System.currentTimeMillis() + 30000L),
                isPublic = true,
                type = Appointment.Type.POINT
        )
    }

    @Test
    fun testValidConstraints_Success() {
        // Execute the command with a valid id
        val (info, errors) = Retrieve(
                appointmentId = appointment.id,
                appointmentRepo = appointmentRepo
        ).execute()

        // Make sure it was a success
        assertNull(errors)
        assertNotNull(info)
    }

    @Test
    fun testInvalidId_Failure() {
        // Execute the command with an invalid id
        val (info, errors) = Retrieve(
                appointmentId = 311L,
                appointmentRepo = appointmentRepo
        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(info)

        // Make sure it failed for the expected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.ID].isNotEmpty())
    }
}