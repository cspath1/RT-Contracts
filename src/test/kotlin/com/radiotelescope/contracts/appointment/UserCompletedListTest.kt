package com.radiotelescope.contracts.appointment

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository
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
internal class UserCompletedListTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private var firstUserId: Long = 0
    private var secondUserId: Long = 0

    @Before
    fun setUp() {
        // Persist a user that has an appointment
        val user = testUtil.createUser("spathcody@gmail.com")
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.COMPLETED,
                startTime = Date(System.currentTimeMillis() - 30000L),
                endTime = Date(System.currentTimeMillis() - 10000L),
                isPublic = true,
                type = Appointment.Type.POINT
        )
        firstUserId = user.id

        // Persist another user without any appointments
        val secondUser = testUtil.createUser("cspath1@ycp.edu")
        secondUserId = secondUser.id
    }

    @Test
    fun testValidConstraints_EmptyList_Success() {
        val (page, error) = UserCompletedList(
                appointmentRepo = appointmentRepo,
                userId = secondUserId,
                userRepo = userRepo,
                pageable = PageRequest.of(0, 20)
        ).execute()

        assertNotNull(page)
        assertNull(error)

        assertEquals(0, page!!.content.size)
    }

    @Test
    fun nonEmptyListTest() {
        val (page, error) = UserCompletedList(
                appointmentRepo = appointmentRepo,
                userId = firstUserId,
                userRepo = userRepo,
                pageable = PageRequest.of(0, 20)
        ).execute()

        assertNotNull(page)
        assertNull(error)

        assertEquals(1, page!!.content.size)
    }

    @Test
    fun invalidUserId() {
        val (page, error) = UserCompletedList(
                appointmentRepo = appointmentRepo,
                userId = 311L,
                userRepo = userRepo,
                pageable = PageRequest.of(0, 20)
        ).execute()

        assertNotNull(error)
        assertNull(page)

        assertEquals(1, error!!.size())
        assertTrue(error[ErrorTag.USER_ID].isNotEmpty())
    }
}