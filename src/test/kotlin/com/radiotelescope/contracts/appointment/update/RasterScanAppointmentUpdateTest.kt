package com.radiotelescope.contracts.appointment.update

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.coordinate.CoordinateRequest
import com.radiotelescope.controller.model.Profile
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.Coordinate
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
internal class RasterScanAppointmentUpdateTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var radioTelescopeRepo: IRadioTelescopeRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    @Autowired
    private lateinit var orientationRepo: IOrientationRepository

    @Autowired
    private lateinit var heartbeatMonitorRepo: IHeartbeatMonitorRepository

    private var coordinateRequestOne = CoordinateRequest(
            hours = 12,
            minutes = 12,
            seconds = 12,
            declination = 45.0
    )

    private var coordinateRequestTwo = CoordinateRequest(
            hours = 13,
            minutes = 13,
            seconds = 13,
            declination = 25.0
    )

    private var coordinateRequests = arrayListOf(coordinateRequestOne, coordinateRequestTwo)


    private val twoHours = 2 * 60 * 60 * 1000

    private lateinit var originalCoordinates: MutableList<Coordinate>
    private lateinit var baseRequest: RasterScanAppointmentUpdate.Request
    private lateinit var appointment: Appointment
    private lateinit var user: User

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
                priority = Appointment.Priority.PRIMARY,
                status = Appointment.Status.SCHEDULED,
                telescopeId = 1L,
                type = Appointment.Type.RASTER_SCAN
        )
        originalCoordinates = appointment.coordinateList
        assertEquals(2, appointment.coordinateList.size)

        baseRequest = RasterScanAppointmentUpdate.Request(
                id = appointment.id,
                startTime = Date(appointment.endTime.time + twoHours),
                endTime = Date(appointment.endTime.time + (twoHours * 2)),
                telescopeId = 1L,
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                coordinates = coordinateRequests
        )
    }

    @Test
    fun testValidConstraints_Success() {
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

        val (id, errors) = RasterScanAppointmentUpdate(
                request = baseRequest,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo,
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
        assertNull(theAppointment.celestialBody)
        assertNull(theAppointment.orientation)
        assertEquals(baseRequest.endTime, theAppointment.endTime)
        assertEquals(baseRequest.startTime, theAppointment.startTime)
        assertEquals(baseRequest.isPublic, theAppointment.isPublic)
        assertEquals(baseRequest.telescopeId, theAppointment.telescopeId)
        assertEquals(2, theAppointment.coordinateList.size)

        // New coordinates should have been persisted
        originalCoordinates.none { original ->
            theAppointment.coordinateList.none {new ->
                original.id == new.id
            }
        }
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
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.DRIFT_SCAN
        )
        assertNotNull(appointment.orientation)

        val requestCopy = baseRequest.copy(
                id = appointment.id
        )

        val (id, errors) = RasterScanAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        assertNotNull(id)
        assertNull(errors)

        // Make sure the information was updated correctly
        val theAppointment = appointmentRepo.findById(id!!).get()

        assertNotEquals(baseRequest.id, theAppointment.id)
        assertNull(theAppointment.orientation)
        assertNull(theAppointment.celestialBody)
        assertEquals(baseRequest.endTime, theAppointment.endTime)
        assertEquals(baseRequest.startTime, theAppointment.startTime)
        assertEquals(baseRequest.isPublic, theAppointment.isPublic)
        assertEquals(baseRequest.telescopeId, theAppointment.telescopeId)
        assertEquals(2, theAppointment.coordinateList.size)
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

        val (id, errors) = RasterScanAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo,
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
    fun testInvalidId_Failure() {
        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        val requestCopy = baseRequest.copy(
                id = 311L
        )

        val (id, errors) = RasterScanAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                orientationRepo = orientationRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.ID].isNotEmpty())
    }

    @Test
    fun testStartAfterEnd_Failure() {
        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        val requestCopy = baseRequest.copy(
                startTime = Date(System.currentTimeMillis() + 30000L),
                endTime = Date(System.currentTimeMillis() + 10000L)
        )

        val (id, errors) = RasterScanAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                orientationRepo = orientationRepo,
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

        val requestCopy = baseRequest.copy(
                startTime = Date(System.currentTimeMillis() - 10000L)
        )

        val (id, errors) = RasterScanAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                orientationRepo = orientationRepo,
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
    fun testNotEnoughTime_Failure() {
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
                startTime = Date(System.currentTimeMillis() + twoHours),
                endTime = Date(System.currentTimeMillis() + (twoHours * 5))
        )

        val (id, errors) = RasterScanAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                orientationRepo = orientationRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.ALLOTTED_TIME].isNotEmpty())
    }

    @Test
    fun testSchedulingConflict_EndAtStart_Failure() {
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
                priority = Appointment.Priority.PRIMARY,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                type = Appointment.Type.POINT
        )

        val requestCopy = baseRequest.copy(
                startTime = Date(endTime),
                endTime = Date(endTime + 10000L)
        )

        val (id, errors) = RasterScanAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                orientationRepo = orientationRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.OVERLAP].isNotEmpty())
    }

    @Test
    fun testHoursTooLow_Failure() {
        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        val coordinateCopy = coordinateRequestOne.copy(
                hours = -311
        )

        val requestCopy = baseRequest.copy(
                coordinates = mutableListOf(coordinateCopy, coordinateRequestTwo)
        )

        val (id, errors) = RasterScanAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                orientationRepo = orientationRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.HOURS].isNotEmpty())
    }

    @Test
    fun testHoursTooHigh_Failure() {
        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        val coordinateCopy = coordinateRequestOne.copy(
                hours = 311
        )

        val requestCopy = baseRequest.copy(
                coordinates = mutableListOf(coordinateCopy, coordinateRequestTwo)
        )

        val (id, errors) = RasterScanAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                orientationRepo = orientationRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.HOURS].isNotEmpty())
    }

    @Test
    fun testMinutesTooLow_Failure() {
        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        val coordinateCopy = coordinateRequestOne.copy(
                minutes = -311
        )

        val requestCopy = baseRequest.copy(
                coordinates = mutableListOf(coordinateCopy, coordinateRequestTwo)
        )

        val (id, errors) = RasterScanAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                orientationRepo = orientationRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.MINUTES].isNotEmpty())
    }

    @Test
    fun testMinutesTooHigh_Failure() {
        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        val coordinateCopy = coordinateRequestOne.copy(
                minutes = -311
        )

        val requestCopy = baseRequest.copy(
                coordinates = mutableListOf(coordinateCopy, coordinateRequestTwo)
        )

        val (id, errors) = RasterScanAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                orientationRepo = orientationRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.MINUTES].isNotEmpty())
    }

    @Test
    fun testSecondsTooLow_Failure() {
        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        val coordinateCopy = coordinateRequestOne.copy(
                seconds = -311
        )

        val requestCopy = baseRequest.copy(
                coordinates = mutableListOf(coordinateCopy, coordinateRequestTwo)
        )

        val (id, errors) = RasterScanAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                orientationRepo = orientationRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.SECONDS].isNotEmpty())
    }

    @Test
    fun testSecondsTooHigh_Failure() {
        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        val coordinateCopy = coordinateRequestOne.copy(
                seconds = 311
        )

        val requestCopy = baseRequest.copy(
                coordinates = mutableListOf(coordinateCopy, coordinateRequestTwo)
        )

        val (id, errors) = RasterScanAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                orientationRepo = orientationRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.SECONDS].isNotEmpty())
    }

    @Test
    fun testDeclinationTooLow_Failure() {
        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        val coordinateCopy = coordinateRequestOne.copy(
                declination = -311.0
        )

        val requestCopy = baseRequest.copy(
                coordinates = mutableListOf(coordinateCopy, coordinateRequestTwo)
        )

        val (id, errors) = RasterScanAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                orientationRepo = orientationRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.DECLINATION].isNotEmpty())
    }

    @Test
    fun testDeclinationTooHigh_Failure() {
        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        val coordinateCopy = coordinateRequestOne.copy(
                declination = 311.0
        )

        val requestCopy = baseRequest.copy(
                coordinates = mutableListOf(coordinateCopy, coordinateRequestTwo)
        )

        val (id, errors) = RasterScanAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                orientationRepo = orientationRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.DECLINATION].isNotEmpty())
    }

    @Test
    fun testNotTwoCoordinates_Failure() {
        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        val requestCopy = baseRequest.copy(
                coordinates = mutableListOf()
        )

        val (id, errors) = RasterScanAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                orientationRepo = orientationRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.COORDINATES].isNotEmpty())
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

        val (id, errors) = RasterScanAppointmentUpdate(
                request = baseRequest,
                appointmentRepo = appointmentRepo,
                orientationRepo = orientationRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
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