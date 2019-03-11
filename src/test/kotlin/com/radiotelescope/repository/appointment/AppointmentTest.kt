package com.radiotelescope.repository.appointment

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.model.appointment.AppointmentSpecificationBuilder
import com.radiotelescope.repository.model.appointment.Filter
import com.radiotelescope.repository.model.appointment.SearchCriteria
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
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    private lateinit var user: User
    private lateinit var otherUser: User
    private lateinit var futureAppointment: Appointment
    private lateinit var pastAppointment: Appointment
    private lateinit var requestedAppointment: Appointment

    private val currentTime = System.currentTimeMillis()

    @Before
    fun setUp() {
        // Persist users
        user = testUtil.createUser("cspath1@ycp.edu")
        otherUser = testUtil.createUser("rpim@ycp.edu")

        // Persist a past appointment, future appointment, and canceled future appointment
        futureAppointment = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(currentTime + 100000L),
                endTime = Date(currentTime + 300000L),
                isPublic = true
        )

        pastAppointment = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.COMPLETED,
                startTime = Date(currentTime - 30000L),
                endTime = Date(currentTime - 10000L),
                isPublic = true
        )

        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.CANCELED,
                startTime = Date(currentTime + 10000L),
                endTime = Date(currentTime + 30000L),
                isPublic = true

        )

        requestedAppointment = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.REQUESTED,
                startTime = Date(currentTime + 1000000000L),
                endTime = Date(currentTime +   3000000000L),
                isPublic = true
        )
        testUtil.createViewer(
                user = otherUser,
                appointment = futureAppointment
        )
    }

    @Test
    fun testFindFutureAppointmentsByUser() {
        val pageOfAppointments = appointmentRepo.findFutureAppointmentsByUser(
                userId = user.id,
                pageable = PageRequest.of(0, 5)
        )

        assertEquals(2, pageOfAppointments.content.size)
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
                status = Appointment.Status.SCHEDULED,
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
                status = Appointment.Status.SCHEDULED,
                startTime = Date(startTime),
                endTime = Date(startTime + 1000L),
                isPublic = true

        )

        // Appointment between the start and end time
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(startTime + 2000L),
                endTime = Date(startTime + 3000L),
                isPublic = true

        )

        // Appointment end at the endTime and start after the startTime
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(endTime - 1000L),
                endTime = Date(endTime),
                isPublic = true

        )

        // Appointment start before startTime and end before endTime
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(startTime - 2000L),
                endTime = Date(startTime + 500L),
                isPublic = true

        )

        // Appointment start before endTime and end after endTime
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(endTime - 500L),
                endTime = Date(endTime + 1000L),
                isPublic = true

        )

        // Appointment status is requested
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.REQUESTED,
                startTime = Date(startTime + 1010L),
                endTime = Date(startTime + 1020L),
                isPublic = true

        )

        // Appointment status is canceled
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.CANCELED,
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

    @Test
    fun testFindCompletedPublicAppointments() {
        // There will already be one completed public appointment from the set up
        // Make one more, and make a completed private appointment and ensure
        // this one is not retrieved
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.COMPLETED,
                startTime = Date(System.currentTimeMillis() - 10000L),
                endTime = Date(System.currentTimeMillis() - 5000L),
                isPublic = true
        )

        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.COMPLETED,
                startTime = Date(System.currentTimeMillis() - 30000L),
                endTime = Date(System.currentTimeMillis() - 15000L),
                isPublic = false
        )

        val pageRequest = PageRequest.of(0, 5)

        val appointments = appointmentRepo.findCompletedPublicAppointments(pageRequest)

        assertNotNull(appointments)

        // The appointments page should have 2 appointments,
        // all of which are completed and public
        assertEquals(2, appointments.content.size)
        appointments.forEach {
            assertTrue(it.isPublic)
            assertEquals(it.status, Appointment.Status.COMPLETED)
        }
    }

    @Test
    fun testFindRequest() {
        val pageOfAppointments = appointmentRepo.findRequest(
                pageable = PageRequest.of(0, 5)
        )

        assertEquals(1, pageOfAppointments.content.size)
        assertEquals(requestedAppointment.id, pageOfAppointments.content[0].id)
    }

    @Test
    fun testFindConflict() {
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L

        // Appointment start at the startTime and end before the endTime
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(startTime),
                endTime = Date(startTime + 1000L),
                isPublic = true

        )

        // Appointment between the start and end time
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(startTime + 2000L),
                endTime = Date(startTime + 3000L),
                isPublic = true

        )

        // Appointment end at the endTime and start after the startTime
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(endTime - 1000L),
                endTime = Date(endTime),
                isPublic = true

        )

        // Appointment start before startTime and end before endTime
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(startTime - 2000L),
                endTime = Date(startTime + 500L),
                isPublic = true

        )

        // Appointment start before endTime and end after endTime
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(endTime - 500L),
                endTime = Date(endTime + 1000L),
                isPublic = true

        )

        // Appointment end at the start time
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(startTime - 1000L),
                endTime = Date(startTime),
                isPublic = true
        )

        // Appointment start at the end time
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(endTime),
                endTime = Date(endTime + 2000L),
                isPublic = true
        )

        // Appointment start before start and end after end
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(startTime - 1111L),
                endTime = Date(endTime + 1111L),
                isPublic = true
        )

        // Appointment status is REQUESTED
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.REQUESTED,
                startTime = Date(startTime + 1010L),
                endTime = Date(startTime + 1020L),
                isPublic = true
        )

        // Appointment status is CANCELED
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.CANCELED,
                startTime = Date(startTime + 1030L),
                endTime = Date(startTime + 1040L),
                isPublic = true

        )


        val listOfAppointments = appointmentRepo.findConflict(
                startTime = Date(startTime),
                endTime = Date(endTime),
                telescopeId = 1L
        )

        assertEquals(8, listOfAppointments.size)
    }

    @Test
    fun testSearchUserFullName() {
        user.firstName = "Cody"
        user.lastName = "Spath"
        userRepo.save(user)

        // Create a search criteria for the user's full name
        val searchCriteria = SearchCriteria(Filter.USER_FULL_NAME, "cody spath")

        val specification = AppointmentSpecificationBuilder().with(searchCriteria).build()

        val appointmentList = appointmentRepo.findAll(specification)

        assertNotNull(appointmentList)
        assertEquals(2, appointmentList.size)
    }

    @Test
    fun testSearchFirstAndLastName() {
        user.firstName = "John"
        user.lastName = "Henry"
        userRepo.save(user)

        // Create a search criteria for the first and last name
        val searchCriteriaOne = SearchCriteria(Filter.USER_FIRST_NAME, "Henry")
        val searchCriteriaTwo = SearchCriteria(Filter.USER_LAST_NAME, "Henry")

        val specification = AppointmentSpecificationBuilder().with(searchCriteriaOne).with(searchCriteriaTwo).build()

        val appointmentList = appointmentRepo.findAll(specification)

        assertNotNull(appointmentList)
        assertEquals(2, appointmentList.size)
    }

    @Test
    fun testFindSharedAppointmentsByUser() {
        val appointmentPage = appointmentRepo.findSharedAppointmentsByUser(
                userId = otherUser.id,
                pageable = PageRequest.of(0, 25)
        )

        assertNotNull(appointmentPage)
        assertEquals(1, appointmentPage.content.size)

        assertEquals(futureAppointment.id, appointmentPage.content[0].id)
    }
}