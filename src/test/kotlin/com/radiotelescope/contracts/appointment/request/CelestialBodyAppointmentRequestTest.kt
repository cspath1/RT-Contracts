package com.radiotelescope.contracts.appointment.request

import com.radiotelescope.TestUtil
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.celestialBody.CelestialBody
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
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
internal class CelestialBodyAppointmentRequestTest {
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
    private lateinit var radioTelescopeRepo: IRadioTelescopeRepository

    @Autowired
    private lateinit var celestialBodyRepo: ICelestialBodyRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    private val baseRequest = CelestialBodyAppointmentRequest.Request(
            userId = -1L,
            telescopeId = 1L,
            startTime = Date(System.currentTimeMillis() + 10000L),
            endTime = Date(System.currentTimeMillis() + 30000L),
            isPublic = true,
            celestialBodyId = -1L
    )

    private lateinit var user: User
    private lateinit var celestialBody: CelestialBody

    @Before
    fun setUp() {
        user = testUtil.createUser("cspath1@ycp.edu")

        val coordinate = Coordinate(
                hours = 5,
                minutes = 34,
                seconds = 32,
                rightAscension = Coordinate.hoursMinutesSecondsToDegrees(
                        hours = 5,
                        minutes = 34,
                        seconds = 32
                ),
                declination = 22.0
        )
        coordinateRepo.save(coordinate)

        celestialBody = testUtil.createCelestialBody(
                name = "Crab Nebula",
                coordinate = coordinate
        )
    }

    @Test
    fun testValidConstraints_Success() {
        // Create a copy of the request with a valid user and celestial body id
        val requestCopy = baseRequest.copy(
                userId = user.id,
                celestialBodyId = celestialBody.id
        )

        // Execute the command
        val (id, errors) = CelestialBodyAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)

        // Make sure everything was persisted correctly
        val theAppointment = appointmentRepo.findById(id!!).get()

        assertNotNull(theAppointment.celestialBody)
        assertEquals(celestialBody.id, theAppointment.celestialBody!!.id)
        assertEquals(requestCopy.isPublic, theAppointment.isPublic)
        assertEquals(requestCopy.startTime, theAppointment.startTime)
        assertEquals(requestCopy.endTime, theAppointment.endTime)
        assertEquals(requestCopy.telescopeId, theAppointment.telescopeId)
        assertEquals(requestCopy.userId, theAppointment.user.id)
        assertEquals(Appointment.Type.CELESTIAL_BODY, theAppointment.type)
        assertEquals(Appointment.Status.REQUESTED, theAppointment.status)
    }

    @Test
    fun testInvalidUserId_Failure() {
        // Create a copy of the request with an invalid user id
        val requestCopy = baseRequest.copy(
                userId = 311L,
                celestialBodyId = celestialBody.id
        )

        // Execute the command
        val (id, errors) = CelestialBodyAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure the command was a success
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertTrue(errors!![ErrorTag.USER_ID].isNotEmpty())
    }

    @Test
    fun testInvalidTelescopeId_Failure() {
        // Create a copy of the request with an invalid telescope id
        val requestCopy = baseRequest.copy(
                userId = user.id,
                telescopeId = 311L
        )

        // Execute the command
        val (id, errors) = CelestialBodyAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure the command was a success
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertTrue(errors!![ErrorTag.TELESCOPE_ID].isNotEmpty())
    }

    @Test
    fun testStartTimeBeforeNow_Failure() {
        // Create a copy of the request with an start time before now
        val requestCopy = baseRequest.copy(
                userId = user.id,
                celestialBodyId = celestialBody.id,
                startTime = Date(System.currentTimeMillis() - 1000L)
        )

        // Execute the command
        val (id, errors) = CelestialBodyAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure the command was a success
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertTrue(errors!![ErrorTag.START_TIME].isNotEmpty())
    }

    @Test
    fun testStartTimeAfterEndTime_Failure() {
        // Create a copy of the request with a start time before the end time
        val requestCopy = baseRequest.copy(
                userId = user.id,
                startTime = baseRequest.endTime,
                endTime = baseRequest.startTime,
                celestialBodyId = celestialBody.id
        )

        // Execute the command
        val (id, errors) = CelestialBodyAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure the command was a success
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertTrue(errors!![ErrorTag.END_TIME].isNotEmpty())
    }

    @Test
    fun testInvalidCelestialBodyId_Failure() {
        // Create a copy of the request with an invalid celestial body id
        val requestCopy = baseRequest.copy(
                userId = user.id,
                celestialBodyId = 311L
        )

        // Execute the command
        val (id, errors) = CelestialBodyAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure the command was a success
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertTrue(errors!![ErrorTag.CELESTIAL_BODY].isNotEmpty())
    }
}