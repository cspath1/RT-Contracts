package com.radiotelescope.contracts.appointment.update

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.controller.model.Profile
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.celestialBody.CelestialBody
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.heartbeatMonitor.IHeartbeatMonitorRepository
import com.radiotelescope.repository.orientation.IOrientationRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
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
internal class CelestialBodyAppointmentUpdateTest : AbstractSpringTest() {
    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var radioTelescopeRepo: IRadioTelescopeRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    @Autowired
    private lateinit var orientationRepo: IOrientationRepository

    @Autowired
    private lateinit var celestialBodyRepo: ICelestialBodyRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    @Autowired
    private lateinit var heartbeatMonitorRepo: IHeartbeatMonitorRepository

    private lateinit var appointment: Appointment
    private lateinit var user: User
    private lateinit var celestialBody: CelestialBody

    private val twoHours = 2 * 60 * 60 * 1000

    private lateinit var baseRequest: CelestialBodyAppointmentUpdate.Request

    @Before
    fun setUp() {
        // Persist the user
        user = testUtil.createUser(
                email = "cspath1@ycp.edu"
        )

        // Persist the appointment
        appointment = testUtil.createAppointment(
                user = user,
                startTime = Date(System.currentTimeMillis() + 10000L),
                endTime = Date(System.currentTimeMillis() + 30000L),
                isPublic = true,
                status = Appointment.Status.SCHEDULED,
                telescopeId = 1L,
                type = Appointment.Type.CELESTIAL_BODY
        )
        assertNotNull(appointment.celestialBody)

        celestialBody = appointment.celestialBody!!

        baseRequest = CelestialBodyAppointmentUpdate.Request(
                id = appointment.id,
                startTime = Date(appointment.endTime.time + twoHours),
                endTime = Date(appointment.endTime.time + (twoHours * 2)),
                telescopeId = 1L,
                isPublic = true,
                celestialBodyId = celestialBody.id
        )
    }

    @Test
    fun testValidConstraints_Guest_Success() {
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

        val (id, errors) = CelestialBodyAppointmentUpdate(
                request = baseRequest,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo,
                celestialBodyRepo = celestialBodyRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure it was not an error
        assertNotNull(id)
        assertNull(errors)

        // Make sure the information was updated correctly
        val theAppointment = appointmentRepo.findById(id!!).get()

        assertEquals(baseRequest.id, theAppointment.id)
        assertNotNull(theAppointment.celestialBody)
        assertEquals(baseRequest.celestialBodyId, theAppointment.celestialBody!!.id)
        assertEquals(baseRequest.endTime, theAppointment.endTime)
        assertEquals(baseRequest.startTime, theAppointment.startTime)
        assertEquals(baseRequest.isPublic, theAppointment.isPublic)
        assertEquals(baseRequest.telescopeId, theAppointment.telescopeId)
        assertNull(theAppointment.orientation)
        assertEquals(0, theAppointment.coordinateList.size)
    }

    @Test
    fun testValidConstraints_ChangedType_Success() {
        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        // Make the user a researcher
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        // Create an appointment that is a different type
        val appointment = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis() + 50000L),
                endTime = Date(System.currentTimeMillis() + 100000L),
                isPublic = true,
                type = Appointment.Type.POINT
        )

        val requestCopy = baseRequest.copy(
                id = appointment.id
        )

        val (id, errors) = CelestialBodyAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo,
                celestialBodyRepo = celestialBodyRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        assertNotNull(id)
        assertNull(errors)

        // Make sure the information was updated correctly
        val theAppointment = appointmentRepo.findById(id!!).get()

        assertNotEquals(baseRequest.id, theAppointment.id)
        assertNotNull(theAppointment.celestialBody)
        assertNull(theAppointment.orientation)
        assertEquals(baseRequest.endTime, theAppointment.endTime)
        assertEquals(baseRequest.startTime, theAppointment.startTime)
        assertEquals(baseRequest.isPublic, theAppointment.isPublic)
        assertEquals(baseRequest.telescopeId, theAppointment.telescopeId)
        assertEquals(0, theAppointment.coordinateList.size)
    }

    @Test
    fun testInvalidCelestialBodyId_Failure() {
        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        // Create a copy of the request with an invalid
        // celestial body id
        val requestCopy = baseRequest.copy(
                celestialBodyId = 311L
        )

        val (id, errors) = CelestialBodyAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo,
                celestialBodyRepo = celestialBodyRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the expected reason
        assertTrue(errors!![ErrorTag.CELESTIAL_BODY].isNotEmpty())
    }

    @Test
    fun testInvalidTelescopeId_Failure() {
        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        val requestCopy = baseRequest.copy(
                telescopeId = 311L
        )

        val (id, errors) = CelestialBodyAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo,
                celestialBodyRepo = celestialBodyRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the expected reason
        assertTrue(errors!![ErrorTag.TELESCOPE_ID].isNotEmpty())
    }

    @Test
    fun testInvalidAppointmentId_Failure() {
        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        // Create a copy of the request with an invalid id
        val requestCopy = baseRequest.copy(
                id = 311
        )

        val (id, errors) = CelestialBodyAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo,
                celestialBodyRepo = celestialBodyRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the expected reason
        assertTrue(errors!![ErrorTag.ID].isNotEmpty())
    }

    @Test
    fun testInvalidStartTimeAfterEnd_Failure() {
        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        // Create a copy of the request with a start time after the end time
        val requestCopy = baseRequest.copy(
                startTime = Date(appointment.endTime.time + 40000L),
                endTime = Date(appointment.endTime.time + 10000L)
        )

        val (id, errors) = CelestialBodyAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo,
                celestialBodyRepo = celestialBodyRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the expected reason
        assertTrue(errors!![ErrorTag.END_TIME].isNotEmpty())
    }

    @Test
    fun testInvalidAppointmentStatus_Failure() {
        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        // Modify the appointment status to not be scheduled/requested
        appointment.status = Appointment.Status.CANCELED
        appointmentRepo.save(appointment)

        val (id, errors) = CelestialBodyAppointmentUpdate(
                request = baseRequest,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo,
                celestialBodyRepo = celestialBodyRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the expected reason
        assertTrue(errors!![ErrorTag.STATUS].isNotEmpty())
    }

    @Test
    fun testInvalid_StartTimeInPast_Failure() {
        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        // Create a copy of the request with a start time in the past
        val requestCopy = baseRequest.copy(
                startTime = Date(appointment.startTime.time - 40000L),
                endTime = Date(appointment.endTime.time + 10000L)
        )

        val (id, errors) = CelestialBodyAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo,
                celestialBodyRepo = celestialBodyRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the expected reason
        assertTrue(errors!![ErrorTag.START_TIME].isNotEmpty())
    }

    @Test
    fun testInvalid_TelescopeDoesNotExist_Failure() {
        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        // Create a copy of the request with an invalid telescope
        val requestCopy = baseRequest.copy(
                telescopeId = 311L
        )

        val (id, errors) = CelestialBodyAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo,
                celestialBodyRepo = celestialBodyRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the expected reason
        assertTrue(errors!![ErrorTag.TELESCOPE_ID].isNotEmpty())
    }

    @Test
    fun testSchedulingConflict_BetweenEndAndStart_Failure() {
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
                type = Appointment.Type.POINT
        )

        val requestCopy = baseRequest.copy(
                startTime = Date(startTime),
                endTime = Date(startTime + 10000L)
        )

        val (id, errors) = CelestialBodyAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo,
                celestialBodyRepo = celestialBodyRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the expected reason
        assertTrue(errors!![ErrorTag.OVERLAP].isNotEmpty())
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

        // Set last communication to 30 minutes in the past
        val monitor = heartbeatMonitorRepo.findByRadioTelescopeId(1L)

        assertNotNull(monitor)

        monitor!!.lastCommunication = Date(System.currentTimeMillis() - (1000 * 60 * 30))
        heartbeatMonitorRepo.save(monitor)

        val (id, errors) = CelestialBodyAppointmentUpdate(
                request = baseRequest,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo,
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