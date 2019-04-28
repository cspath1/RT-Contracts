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
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
internal class PublicCompletedAppointmentsTest : AbstractSpringTest() {
    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private val pageRequest = PageRequest.of(0, 5)

    @Before
    fun setUp() {
        // Persist a user
        val user = testUtil.createUser("cspath1@ycp.edu")

        // Persist three completed public appointments
        // One of which is private
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.COMPLETED,
                startTime = Date(System.currentTimeMillis() - 50000L),
                endTime = Date(System.currentTimeMillis() - 40000L),
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.POINT
        )

        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.COMPLETED,
                startTime = Date(System.currentTimeMillis() - 30000L),
                endTime = Date(System.currentTimeMillis() - 20000L),
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.POINT
        )

        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.COMPLETED,
                startTime = Date(System.currentTimeMillis() - 15000L),
                endTime = Date(System.currentTimeMillis() - 5000L),
                isPublic = false,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.POINT
        )
    }

    @Test
    fun testValidConstraints_Success() {
        val (page, errors) = PublicCompletedAppointments(
                pageable = pageRequest,
                appointmentRepo = appointmentRepo
        ).execute()

        assertNotNull(page)
        assertNull(errors)

        assertEquals(2, page!!.content.size)
        page.forEach {
            assertTrue(it.isPublic)
            assertEquals(it.status, Appointment.Status.COMPLETED.label)
        }
    }
}