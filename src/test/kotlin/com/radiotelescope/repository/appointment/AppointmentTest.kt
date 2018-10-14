package com.radiotelescope.repository.appointment

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class AppointmentTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private lateinit var user: User
    private lateinit var futureAppointment: Appointment
    private lateinit var pastAppointment: Appointment

    @Before
    fun setUp() {
        // Persist a user
        user = testUtil.createUser("cspath1@ycp.edu")

        // Persist a past and future appointment
        futureAppointment = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.Scheduled,
                startTime = Date(System.currentTimeMillis() + 10000),
                endTime = Date(System.currentTimeMillis() + 30000),
                isPublic = true
        )

        pastAppointment = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.Completed,
                startTime = Date(System.currentTimeMillis() - 30000L),
                endTime = Date(System.currentTimeMillis() - 10000L),
                isPublic = true
        )
    }

    @Test
    fun testFindFutureAppointmentsByUser() {
        val pageOfAppointments = appointmentRepo.findFutureAppointmentsByUser(
                userId = user.id,
                pageable = PageRequest.of(0, 5)
        )

        assertEquals(1, pageOfAppointments.content.size)
        assertEquals(futureAppointment.id, pageOfAppointments.content[0].id)
    }

    @Test
    fun testFindPastAppointmentsByUser() {
        val pageOfAppointments = appointmentRepo.findPreviousAppointmentsByUser(
                userId = user.id,
                pageable = PageRequest.of(0, 5)
        )

        assertEquals(1, pageOfAppointments.content.size)
        assertEquals(pastAppointment.id, pageOfAppointments.content[0].id)
    }

}