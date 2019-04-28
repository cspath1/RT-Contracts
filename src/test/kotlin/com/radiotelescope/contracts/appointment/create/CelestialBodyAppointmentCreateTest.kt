package com.radiotelescope.contracts.appointment.create

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.controller.model.Profile
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.celestialBody.CelestialBody
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.heartbeatMonitor.IHeartbeatMonitorRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
internal class CelestialBodyAppointmentCreateTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var radioTelescopeRepo: IRadioTelescopeRepository

    @Autowired
    private lateinit var celestialBodyRepo: ICelestialBodyRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    @Autowired
    private lateinit var heartbeatMonitorRepo: IHeartbeatMonitorRepository

    private val baseRequest = CelestialBodyAppointmentCreate.Request(
            userId = -1L,
            startTime = Date(System.currentTimeMillis() + 100000L),
            endTime = Date(System.currentTimeMillis() + 300000L),
            telescopeId = 1L,
            isPublic = true,
            priority = Appointment.Priority.PRIMARY,
            celestialBodyId = -1L
    )

    private lateinit var user: User
    private lateinit var celestialBody: CelestialBody

    private val date = Date()
    private val twoHours = 2 * 60 * 60 * 1000

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
    fun testValidConstraints_EnoughTime_Success() {
        // Make the user a guest
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        val requestCopy = baseRequest.copy(
                userId = user.id,
                celestialBodyId = celestialBody.id
        )

        // Execute the command
        val (id, errors) = CelestialBodyAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                celestialBodyRepo = celestialBodyRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)

        // And make sure the appointment was persisted
        val theAppointment = appointmentRepo.findById(id!!).get()

        // Make sure the correct information was persisted
        assertEquals(requestCopy.startTime, theAppointment.startTime)
        assertEquals(requestCopy.endTime, theAppointment.endTime)
        assertEquals(requestCopy.telescopeId, theAppointment.telescopeId)
        assertEquals(requestCopy.userId, theAppointment.user.id)
        assertTrue(theAppointment.isPublic)
        assertEquals(Appointment.Type.CELESTIAL_BODY, theAppointment.type)
    }

    @Test
    fun testInvalidTelescopeId_Failure() {
        // Create a copy of the base request with an invalid
        // telescope id
        val requestCopy = baseRequest.copy(
                userId = user.id,
                celestialBodyId = celestialBody.id,
                telescopeId = 311L
        )

        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        val (id, errors) = CelestialBodyAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                celestialBodyRepo = celestialBodyRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.TELESCOPE_ID].isNotEmpty())
    }

    @Test
    fun testInvalidUserId_Failure() {
        // Create a copy of the base request with an invalid user id
        val requestCopy = baseRequest.copy(
                celestialBodyId = celestialBody.id
        )

        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        val (id, errors) = CelestialBodyAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                celestialBodyRepo = celestialBodyRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.USER_ID].isNotEmpty())
    }

    @Test
    fun testInvalidCelestialBodyId_Failure() {
        // Create a copy of the base request with an invalid celestial body id
        val requestCopy = baseRequest.copy(
                userId = user.id,
                celestialBodyId = 311L
        )

        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        val (id, errors) = CelestialBodyAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                celestialBodyRepo = celestialBodyRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.CELESTIAL_BODY].isNotEmpty())
    }

    @Test
    fun testStartAfterEnd_Failure() {
        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        // Create a copy of the base request with the start
        // time before the end time
        val requestCopy = baseRequest.copy(
                userId = user.id,
                startTime = Date(System.currentTimeMillis() + 30000L),
                endTime = Date(System.currentTimeMillis() + 10000L),
                celestialBodyId = celestialBody.id
        )

        val (id, errors) = CelestialBodyAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                celestialBodyRepo = celestialBodyRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.END_TIME].isNotEmpty())
    }

    @Test
    fun testStartBeforeNow_Failure() {
        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        // Create a copy of the base request with the start time
        // in the past
        val requestCopy = baseRequest.copy(
                userId = user.id,
                startTime = Date(System.currentTimeMillis() - 10000L),
                celestialBodyId = celestialBody.id
        )

        // Execute the command
        val (id, errors) = CelestialBodyAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                celestialBodyRepo = celestialBodyRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.START_TIME].isNotEmpty())
    }

    @Test
    fun testNotEnoughTime_Guest_Failure() {
        // Make the user a guest
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        // 8 hour appointment
        val requestCopy = baseRequest.copy(
                userId = user.id,
                startTime = Date(date.time + twoHours),
                endTime = Date(date.time + (twoHours * 5)),
                celestialBodyId = celestialBody.id
        )

        val (id, errors) = CelestialBodyAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                celestialBodyRepo = celestialBodyRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.ALLOTTED_TIME].isNotEmpty())
    }

    @Test
    fun testSchedulingConflict_StartAtEnd_Failure() {
        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L

        testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.POINT
        )

        val requestCopy = baseRequest.copy(
                userId = user.id,
                celestialBodyId = celestialBody.id,
                startTime = Date(endTime),
                endTime = Date(endTime + 20000L)
        )

        val (id, errors) = CelestialBodyAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                celestialBodyRepo = celestialBodyRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.OVERLAP].isNotEmpty())
    }

    @Test
    fun testNoCommunicationWithTelescope_Failure() {
        // Make the user a guest
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        val requestCopy = baseRequest.copy(
                userId = user.id,
                celestialBodyId = celestialBody.id
        )

        // Set last communication to 30 minutes in the past
        val monitor = heartbeatMonitorRepo.findByRadioTelescopeId(1L)

        assertNotNull(monitor)

        monitor!!.lastCommunication = Date(System.currentTimeMillis() - (1000 * 60 * 30))
        heartbeatMonitorRepo.save(monitor)

        val (id, errors) = CelestialBodyAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                userRepo = userRepo,radioTelescopeRepo = radioTelescopeRepo,
                celestialBodyRepo = celestialBodyRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        assertNotNull(errors)
        assertNull(id)

        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.CONNECTION].isNotEmpty())
    }
}