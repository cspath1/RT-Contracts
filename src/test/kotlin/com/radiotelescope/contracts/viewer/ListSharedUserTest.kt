package com.radiotelescope.contracts.viewer

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
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
internal class ListSharedUserTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private lateinit var user: User
    private lateinit var otherUser: User
    private lateinit var appointment: Appointment

    @Before
    fun setUp() {
        // Persist a user
        user = testUtil.createUser("rpim@ycp.edu")
        otherUser = testUtil.createUser("rpim1@ycp.edu")

        appointment = testUtil.createAppointment(
                user = otherUser,
                telescopeId = 1L,
                startTime = Date(System.currentTimeMillis() + 100000L),
                endTime = Date(System.currentTimeMillis() + 200000L),
                status = Appointment.Status.SCHEDULED,
                isPublic = false,
                type = Appointment.Type.POINT
        )

        testUtil.createViewer(
                user = otherUser,
                appointment = appointment
        )
    }

    @Test
    fun testValidConstraints_Success(){
        val (page, error) = ListSharedUser(
                appointmentId = appointment.id,
                pageable = PageRequest.of(0, 25),
                userRepo = userRepo,
                appointmentRepo = appointmentRepo
        ).execute()

        assertNotNull(page)
        assertNull(error)

        assertEquals(1, page!!.content.size)
    }

    @Test
    fun testInvalid_AppointmentDoesNotExist_Failure(){
        val (page, error) = ListSharedUser(
                appointmentId = 123L,
                pageable = PageRequest.of(0, 25),
                userRepo = userRepo,
                appointmentRepo = appointmentRepo
        ).execute()

        assertNull(page)
        assertNotNull(error)

        assertEquals(1, error!!.size())
        assertTrue(error[ErrorTag.APPOINTMENT_ID].isNotEmpty())
    }
}