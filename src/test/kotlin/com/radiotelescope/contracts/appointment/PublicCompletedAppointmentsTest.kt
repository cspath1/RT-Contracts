package com.radiotelescope.contracts.appointment

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import liquibase.integration.spring.SpringLiquibase
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
internal class PublicCompletedAppointmentsTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }

        @Bean
        fun liquibase(): SpringLiquibase {
            val liquibase = SpringLiquibase()
            liquibase.setShouldRun(false)
            return liquibase
        }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

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
                isPublic = true
        )

        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.COMPLETED,
                startTime = Date(System.currentTimeMillis() - 30000L),
                endTime = Date(System.currentTimeMillis() - 20000L),
                isPublic = true
        )

        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.COMPLETED,
                startTime = Date(System.currentTimeMillis() - 15000L),
                endTime = Date(System.currentTimeMillis() - 5000L),
                isPublic = false
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