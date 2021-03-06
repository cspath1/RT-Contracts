package com.radiotelescope.contracts.appointment

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
internal class RequestedListTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private lateinit var user: User

    @Before
    fun init(){
        // Persist the user and appointment
        user = testUtil.createUser("rpim@ycp.edu")
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.REQUESTED,
                startTime = Date(System.currentTimeMillis() + 1000L),
                endTime = Date(System.currentTimeMillis() + 4000L),
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.POINT
        )
    }

    @Test
    fun testValid_Success(){
        val(infoPage, error) = RequestedList(
                pageable = PageRequest.of(0, 10),
                userRepo = userRepo,
                appointmentRepo = appointmentRepo
        ).execute()

        // Should not have failed
        assertNull(error)
        assertNotNull(infoPage)

        // should only have 1 AppointmentInfo
        assertEquals(1, infoPage!!.content.size)
    }
}