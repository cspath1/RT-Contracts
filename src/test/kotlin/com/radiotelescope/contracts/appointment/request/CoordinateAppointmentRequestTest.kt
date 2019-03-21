package com.radiotelescope.contracts.appointment.request

import com.radiotelescope.TestUtil
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.telescope.Telescope
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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
@ActiveProfiles(value = ["test"])
internal class CoordinateAppointmentRequestTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var telescopeRepo: ITelescopeRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    private val baseRequest = CoordinateAppointmentRequest.Request(
            userId = -1L,
            telescopeId = 1L,
            startTime = Date(System.currentTimeMillis() + 10000L),
            endTime = Date(System.currentTimeMillis() + 30000L),
            isPublic = true,
            hours = 12,
            minutes = 12,
            seconds = 12,
            declination = 69.0
    )

    private lateinit var user: User

    @Before
    fun setUp() {
        // Persist User and Telescope
        user = testUtil.createUser("rpim@ycp.edu")
    }

    @Test
    fun testValid_CorrectConstraints_Success() {
        // Create a copy of the request with a valid id
        val requestCopy = baseRequest.copy(
                userId = user.id
        )

        // Execute the command
        val (id, errors) = CoordinateAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)

        // Make sure everything was persisted correctly
        val theAppointment = appointmentRepo.findById(id!!).get()

        assertEquals(requestCopy.isPublic, theAppointment.isPublic)
        assertEquals(requestCopy.startTime, theAppointment.startTime)
        assertEquals(requestCopy.endTime, theAppointment.endTime)
        assertEquals(requestCopy.telescopeId, theAppointment.telescopeId)
        assertEquals(requestCopy.userId, theAppointment.user.id)
        assertEquals(Appointment.Type.POINT, theAppointment.type)
        assertEquals(Appointment.Status.REQUESTED, theAppointment.status)
    }

    @Test
    fun testInvalid_UserDoesNotExist_Failure() {
        // Create a copy of the request with an invalid id
        val requestCopy = baseRequest.copy(
                userId = 123456789
        )

        // Execute the command
        val (id, errors) = CoordinateAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure the command was a success
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertTrue(errors!![ErrorTag.USER_ID].isNotEmpty())
    }

    @Test
    fun testInvalid_TelescopeDoesNotExist_Failure() {
        // Create a copy of the request with an invalid telescope id
        val requestCopy = baseRequest.copy(
                userId = user.id,
                telescopeId = 311L
        )

        // Execute the command
        val (id, errors) = CoordinateAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure the command was a success
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertTrue(errors!![ErrorTag.TELESCOPE_ID].isNotEmpty())
    }

    @Test
    fun testInvalid_StartTimeIsBeforeCurrentTime_Failure() {
        // Create a copy of the request with an start time before now
        val requestCopy = baseRequest.copy(
                userId = user.id,
                startTime = Date(System.currentTimeMillis() - 1000L)
        )

        // Execute the command
        val (id, errors) = CoordinateAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure the command was a success
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertTrue(errors!![ErrorTag.START_TIME].isNotEmpty())
    }

    @Test
    fun testInvalid_StartTimeIsAfterEndTime_Failure() {
        // Create a copy of the request with a start time before the end time
        val requestCopy = baseRequest.copy(
                userId = user.id,
                startTime = baseRequest.endTime,
                endTime = baseRequest.startTime
        )

        // Execute the command
        val (id, errors) = CoordinateAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure the command was a success
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertTrue(errors!![ErrorTag.END_TIME].isNotEmpty())
    }

    @Test
    fun testHoursTooLow_Failure() {
        // Create a copy of the request with hours below 0
        val requestCopy = baseRequest.copy(
                userId = user.id,
                hours = -311
        )

        // Execute the command
        val (id, errors) = CoordinateAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure the command was a success
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertTrue(errors!![ErrorTag.HOURS].isNotEmpty())
    }

    @Test
    fun testHoursTooHigh_Failure() {
        // Create a copy of the request with hours above 24
        val requestCopy = baseRequest.copy(
                userId = user.id,
                hours = 311
        )

        // Execute the command
        val (id, errors) = CoordinateAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure the command was a success
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertTrue(errors!![ErrorTag.HOURS].isNotEmpty())
    }

    @Test
    fun testMinutesTooLow_Failure() {
        // Create a copy of the request with minutes below 0
        val requestCopy = baseRequest.copy(
                userId = user.id,
                minutes = -311
        )

        // Execute the command
        val (id, errors) = CoordinateAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure the command was a success
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertTrue(errors!![ErrorTag.MINUTES].isNotEmpty())
    }

    @Test
    fun testMinutesTooHigh_Failure() {
        // Create a copy of the request with minutes above 60
        val requestCopy = baseRequest.copy(
                userId = user.id,
                hours = 311
        )

        // Execute the command
        val (id, errors) = CoordinateAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure the command was a success
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertTrue(errors!![ErrorTag.HOURS].isNotEmpty())
    }

    @Test
    fun testSecondsTooLow_Failure() {
        // Create a copy of the request with seconds below 0
        val requestCopy = baseRequest.copy(
                userId = user.id,
                seconds = -311
        )

        // Execute the command
        val (id, errors) = CoordinateAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure the command was a success
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertTrue(errors!![ErrorTag.SECONDS].isNotEmpty())
    }

    @Test
    fun testSecondsTooHigh_Failure() {
        // Create a copy of the request with seconds above 60
        val requestCopy = baseRequest.copy(
                userId = user.id,
                seconds = 311
        )

        // Execute the command
        val (id, errors) = CoordinateAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure the command was a success
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertTrue(errors!![ErrorTag.SECONDS].isNotEmpty())
    }

    @Test
    fun testDeclinationTooLow_Failure() {
        // Create a copy of the request with a declination below 0
        val requestCopy = baseRequest.copy(
                userId = user.id,
                declination = -311.0
        )

        // Execute the command
        val (id, errors) = CoordinateAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure the command was a success
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertTrue(errors!![ErrorTag.DECLINATION].isNotEmpty())
    }

    @Test
    fun testDeclinationTooGreat_Failure() {
        // Create a copy of the request with a declination above 90
        val requestCopy = baseRequest.copy(
                userId = user.id,
                declination = 311.0
        )

        // Execute the command
        val (id, errors) = CoordinateAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure the command was a success
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertTrue(errors!![ErrorTag.DECLINATION].isNotEmpty())
    }
}