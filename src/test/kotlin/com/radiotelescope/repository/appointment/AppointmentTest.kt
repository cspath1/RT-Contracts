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
import liquibase.integration.spring.SpringLiquibase
import org.springframework.test.context.jdbc.Sql


@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
internal class AppointmentTest {
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

    private lateinit var user: User
    private lateinit var futureAppointment: Appointment
    private lateinit var pastAppointment: Appointment

    private val currentTime = System.currentTimeMillis()

    @Before
    fun setUp() {
        // Persist a user
        user = testUtil.createUser("cspath1@ycp.edu")

        // Persist a past appointment, future appointment, and canceled future appointment
        futureAppointment = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.Scheduled,
                startTime = Date(currentTime + 100000L),
                endTime = Date(currentTime + 300000L),
                isPublic = true
        )

        pastAppointment = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.Completed,
                startTime = Date(currentTime - 30000L),
                endTime = Date(currentTime - 10000L),
                isPublic = true
        )

        // Make a future canceled appointment to ensure it is not retrieved
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.Canceled,
                startTime = Date(currentTime + 10000L),
                endTime = Date(currentTime + 30000L),
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

    @Test
    fun testFindTotalScheduledTimeForUser() {
        // Test first with the only scheduled appointment
        var totalTime = appointmentRepo.findTotalScheduledAppointmentTimeForUser(user.id)

        // 300,000 - 100,000
        assertEquals(200000L, totalTime)

        // Persist a much more longer appointment
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.Scheduled,
                startTime = Date(currentTime + 15200000L),
                endTime = Date(currentTime + 18272500L),
                isPublic = true
        )

        totalTime = appointmentRepo.findTotalScheduledAppointmentTimeForUser(user.id)

        // (300,000 - 100,000) + (18,272,500 - 15,200,000)
        assertEquals(3272500L, totalTime)
    }

    @Test
    fun testFindAppointmentsBetweenDates(){
        val startTime = System.currentTimeMillis() + 400000L
        val endTime = System.currentTimeMillis() +   800000L

        // Appointment start at the startTime and end before the endTime
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.Scheduled,
                startTime = Date(startTime),
                endTime = Date(startTime + 1000L),
                isPublic = true

        )

        // Appointment between the start and end time
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.Scheduled,
                startTime = Date(startTime + 2000L),
                endTime = Date(startTime + 3000L),
                isPublic = true

        )

        // Appointment end at the endTime and start after the startTime
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.Scheduled,
                startTime = Date(endTime - 1000L),
                endTime = Date(endTime),
                isPublic = true

        )

        // Appointment start before startTime and end before endTime
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.Scheduled,
                startTime = Date(startTime - 2000L),
                endTime = Date(startTime + 500L),
                isPublic = true

        )

        // Appointment start before endTime and end after endTime
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.Scheduled,
                startTime = Date(endTime - 500L),
                endTime = Date(endTime + 1000L),
                isPublic = true

        )

        // Appointment status is Requested
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.Requested,
                startTime = Date(startTime + 1010L),
                endTime = Date(startTime + 1020L),
                isPublic = true

        )

        // Appointment status is Canceled
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.Canceled,
                startTime = Date(startTime + 1030L),
                endTime = Date(startTime + 1040L),
                isPublic = true

        )


        val listOfAppointments = appointmentRepo.findAppointmentsBetweenDates(
                startTime = Date(startTime),
                endTime = Date(endTime),
                telescopeId = 1L
        )

        assertEquals(5, listOfAppointments.size)
    }

}