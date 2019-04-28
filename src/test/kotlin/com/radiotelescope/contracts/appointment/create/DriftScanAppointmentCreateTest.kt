package com.radiotelescope.contracts.appointment.create

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.controller.model.Profile
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.heartbeatMonitor.IHeartbeatMonitorRepository
import com.radiotelescope.repository.orientation.IOrientationRepository
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
internal class DriftScanAppointmentCreateTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var radioTelescopeRepo: IRadioTelescopeRepository

    @Autowired
    private lateinit var orientationRepo: IOrientationRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    @Autowired
    private lateinit var heartbeatMonitorRepo: IHeartbeatMonitorRepository

    private val baseRequest = DriftScanAppointmentCreate.Request(
            userId = -1L,
            telescopeId = 1L,
            startTime = Date(System.currentTimeMillis() + 100000L),
            endTime = Date(System.currentTimeMillis() + 300000L),
            isPublic = true,
            priority = Appointment.Priority.PRIMARY,
            elevation = 90.0,
            azimuth = 180.0
    )

    private lateinit var user: User

    private val date = Date()
    private val twoHours = 2 * 60 * 60 * 1000

    @Before
    fun setUp() {
        user = testUtil.createUser("rpim@ycp.edu")
    }

    @Test
    fun testValidConstraints_EnoughTime_Success() {
        // Give the user 5 hours of allotted time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = 5 * 60 * 60 * 1000
        )
        // Make the user a Guest
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        // Create a copy of the baseRequest with the correct
        // user id
        val requestCopy = baseRequest.copy(userId = user.id)

        // Execute the command
        val (id, errors) = DriftScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                orientationRepo = orientationRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)

        // And make sure the appointment was persisted
        val theAppointment = appointmentRepo.findById(id!!)
        assertTrue(theAppointment.isPresent)

        // Make sure the correct information was persisted
        assertEquals(requestCopy.startTime, theAppointment.get().startTime)
        assertEquals(requestCopy.endTime, theAppointment.get().endTime)
        assertEquals(requestCopy.telescopeId, theAppointment.get().telescopeId)
        assertEquals(requestCopy.userId, theAppointment.get().user.id)
        assertTrue(theAppointment.get().isPublic)
        assertEquals(requestCopy.priority, theAppointment.get().priority)
        assertEquals(Appointment.Type.DRIFT_SCAN, theAppointment.get().type)
        assertNotNull(theAppointment.get().orientation)
        assertEquals(requestCopy.elevation ,theAppointment.get().orientation!!.elevation, 0.0001)
        assertEquals(requestCopy.azimuth, theAppointment.get().orientation!!.azimuth, 0.0001)
    }

    @Test
    fun test_unlimitedTime_EnoughTime_Success() {
        // Give the user an unlimited time cap
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )
        // Make the user a Researcher
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        // Create an appointment for two hours
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = date,
                endTime = Date(date.time + twoHours),
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.POINT
        )

        // 8 hour appointment
        val requestCopy = baseRequest.copy(
                userId = user.id,
                startTime = Date(date.time + (twoHours * 2)),
                endTime = Date( date.time + (twoHours * 2) + (twoHours * 5))
        )

        // Execute the command
        val (id, errors) = DriftScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                orientationRepo = orientationRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)
    }

    @Test
    fun testInvalidTelescopeId_Failure() {
        // Give the user unlimited time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )
        // Make the user a Researcher
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        // Create a copy of the baseRequest with the correct
        // user id but an invalid telescope id
        val requestCopy = baseRequest.copy(
                userId = user.id,
                telescopeId = 311L
        )

        // Execute the command
        val (id, errors) = DriftScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
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
    fun testInvalidUserId_Failure() {
        // Since the is in the base request is
        // already invalid, just execute the command
        // Execute the command
        val (id, errors) = DriftScanAppointmentCreate(
                request = baseRequest,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
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
        assertTrue(errors[ErrorTag.USER_ID].isNotEmpty())
    }

    @Test
    fun testStartAfterEnd_Failure() {
        // Give the user unlimited time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        // Create a copy of the baseRequest with the
        // start time before the end time
        val requestCopy = baseRequest.copy(
                userId = user.id,
                startTime = Date(System.currentTimeMillis() + 30000L),
                endTime = Date(System.currentTimeMillis() + 10000L)
        )

        // Execute the command
        val (id, errors) = DriftScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
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
        assertTrue(errors[ErrorTag.END_TIME].isNotEmpty())
    }

    @Test
    fun testStartBeforeNow_Failure() {
        // Give the user unlimited time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        val requestCopy = baseRequest.copy(
                userId = user.id,
                startTime = Date(System.currentTimeMillis() - 10000L)
        )

        // Execute the command
        val (id, errors) = DriftScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
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
        assertTrue(errors[ErrorTag.START_TIME].isNotEmpty())
    }

    @Test
    fun testNotEnoughTime_Failure() {
        // Give the user 5 hours of allotted time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = 5 * 60 * 60 * 1000
        )
        // Make the user a Guest
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        // 8 hour appointment
        val requestCopy = baseRequest.copy(
                userId = user.id,
                startTime = Date(date.time + twoHours),
                endTime = Date(date.time + (twoHours * 5))
        )

        // Execute the command
        val (id, errors) = DriftScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                orientationRepo = orientationRepo,
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
    fun testNotEnoughTime_Other_Failure() {
        // Make the user a researcher
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        // Give the user 20 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = 20
        )

        // 52 hour appointment
        val requestCopy = baseRequest.copy(
                userId = user.id,
                startTime = Date(date.time + twoHours),
                endTime = Date(date.time + (twoHours * 27))
        )

        // Execute the command
        val (id, errors) = DriftScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                orientationRepo = orientationRepo,
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
    fun testInvalidSC_StartAtStart_EndBeforeEnd_Failure(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L

        // Give the user unlimited time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )

        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.DRIFT_SCAN
        )

        val conflict = DriftScanAppointmentCreate.Request(
                userId = user.id,
                telescopeId = 1L,
                startTime = Date(startTime),
                endTime = Date(startTime + 1000L),
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                azimuth = 180.0,
                elevation = 90.0

        )

        // Execute the command
        val (id, errors) = DriftScanAppointmentCreate(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                orientationRepo = orientationRepo,
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
    fun testInvalidSC_BetweenEndAndStart_Failure(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L

        // Give the user unlimited time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )

        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.DRIFT_SCAN
        )

        val conflict = DriftScanAppointmentCreate.Request(
                userId = user.id,
                telescopeId = 1L,
                startTime = Date(startTime + 1000L),
                endTime = Date(endTime - 1000L),
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                azimuth = 180.0,
                elevation = 90.0
        )

        // Execute the command
        val (id, errors) = DriftScanAppointmentCreate(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                orientationRepo = orientationRepo,
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
    fun testInvalidSC_StartAfterStart_EndAtEnd_Failure(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L

        // Give the user unlimited time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )

        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.DRIFT_SCAN
        )

        val conflict = DriftScanAppointmentCreate.Request(
                userId = user.id,
                telescopeId = 1L,
                startTime = Date(endTime - 1000L),
                endTime = Date(endTime),
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                azimuth = 180.0,
                elevation = 90.0
        )

        // Execute the command
        val (id, errors) = DriftScanAppointmentCreate(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                orientationRepo = orientationRepo,
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
    fun testInvalidSC_StartBeforeStart_EndBeforeEnd_Failure(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L

        // Give the user unlimited time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )

        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                type = Appointment.Type.DRIFT_SCAN
        )

        val conflict = DriftScanAppointmentCreate.Request(
                userId = user.id,
                telescopeId = 1L,
                startTime = Date(startTime - 2000L),
                endTime = Date(startTime + 500L),
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                azimuth = 180.0,
                elevation = 90.0
        )

        // Execute the command
        val (id, errors) = DriftScanAppointmentCreate(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                orientationRepo = orientationRepo,
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
    fun testInvalidSC_StartBeforeEnd_EndAfterEnd_Failure(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L

        // Give the user unlimited time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )

        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.DRIFT_SCAN
        )

        val conflict = DriftScanAppointmentCreate.Request(
                userId = user.id,
                telescopeId = 1L,
                startTime = Date(endTime - 500L),
                endTime = Date(endTime + 1000L),
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                azimuth = 180.0,
                elevation = 90.0
        )

        // Execute the command
        val (id, errors) = DriftScanAppointmentCreate(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                orientationRepo = orientationRepo,
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
    fun testInvalidSC_EndAtStart_Failure(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L

        // Give the user unlimited time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )

        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.DRIFT_SCAN
        )

        val conflict = DriftScanAppointmentCreate.Request(
                userId = user.id,
                telescopeId = 1L,
                startTime = Date(startTime - 1000L),
                endTime = Date(startTime),
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                azimuth = 180.0,
                elevation = 90.0
        )

        // Execute the command
        val (id, errors) = DriftScanAppointmentCreate(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                orientationRepo = orientationRepo,
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
    fun testInvalidSC_StartAtEnd_Failure(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L

        // Give the user unlimited time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )

        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.DRIFT_SCAN
        )

        val conflict = DriftScanAppointmentCreate.Request(
                userId = user.id,
                telescopeId = 1L,
                startTime = Date(endTime),
                endTime = Date(endTime + 2000L),
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                azimuth = 180.0,
                elevation = 90.0
        )

        // Execute the command
        val (id, errors) = DriftScanAppointmentCreate(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                orientationRepo = orientationRepo,
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
    fun testInvalidSC_StartBeforeStart_EndAfterEnd_Failure(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L

        // Give the user unlimited time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )

        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.DRIFT_SCAN
        )

        val conflict = DriftScanAppointmentCreate.Request(
                userId = user.id,
                telescopeId = 1L,
                startTime = Date(startTime - 1111L),
                endTime = Date(endTime + 1111L),
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                azimuth = 180.0,
                elevation = 90.0
        )

        // Execute the command
        val (id, errors) = DriftScanAppointmentCreate(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                orientationRepo = orientationRepo,
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
    fun testInvalidSC_StartAtStart_EndAtEnd_Failure(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L

        // Give the user unlimited time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )

        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.DRIFT_SCAN
        )

        val conflict = DriftScanAppointmentCreate.Request(
                userId = user.id,
                telescopeId = 1L,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                azimuth = 180.0,
                elevation = 90.0
        )

        // Execute the command
        val (id, errors) = DriftScanAppointmentCreate(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                orientationRepo = orientationRepo,
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
    fun testValidSC_Requested_Success(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L

        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.REQUESTED,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.DRIFT_SCAN
        )

        val conflict = DriftScanAppointmentCreate.Request(
                userId = user.id,
                telescopeId = 1L,
                startTime = Date(startTime + 1L),
                endTime = Date(endTime + 1L),
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                azimuth = 180.0,
                elevation = 90.0
        )

        // Execute the command
        val (id, errors) = DriftScanAppointmentCreate(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                orientationRepo = orientationRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)
    }

    @Test
    fun testValidSC_Canceled_Success(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L

        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.CANCELED,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.DRIFT_SCAN
        )

        val conflict = DriftScanAppointmentCreate.Request(
                userId = user.id,
                telescopeId = 1L,
                startTime = Date(startTime + 1L),
                endTime = Date(endTime + 1L),
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                azimuth = 180.0,
                elevation = 90.0
        )

        // Execute the command
        val (id, errors) = DriftScanAppointmentCreate(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                orientationRepo = orientationRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)
    }

    @Test
    fun testAzimuthTooLow_Failure() {
        // Give the user unlimited time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = Appointment.GUEST_APPOINTMENT_TIME_CAP
        )

        // Make the user a guest
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        // Create a copy of the request with an invalid hours
        val requestCopy = baseRequest.copy(
                userId = user.id,
                azimuth = -311.0
        )

        // Execute the command
        val (id, errors) = DriftScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                orientationRepo = orientationRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.AZIMUTH].isNotEmpty())
    }

    @Test
    fun testAzimuthTooHigh_Failure() {
        // Give the user unlimited time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = Appointment.GUEST_APPOINTMENT_TIME_CAP
        )
        // Make the user a guest
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        // Create a copy of the request with an invalid hours
        val requestCopy = baseRequest.copy(
                userId = user.id,
                azimuth = 360.0
        )

        // Execute the command
        val (id, errors) = DriftScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                orientationRepo = orientationRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.AZIMUTH].isNotEmpty())
    }

    @Test
    fun testElevationTooLow_Failure() {
        // Give the user unlimited time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = Appointment.GUEST_APPOINTMENT_TIME_CAP
        )
        // Make the user a guest
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        // Create a copy of the request with an invalid hours
        val requestCopy = baseRequest.copy(
                userId = user.id,
                elevation = -311.0
        )

        // Execute the command
        val (id, errors) = DriftScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                orientationRepo = orientationRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.ELEVATION].isNotEmpty())
    }

    @Test
    fun testElevationTooHigh_Failure() {
        // Give the user a guest time cap
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = Appointment.GUEST_APPOINTMENT_TIME_CAP
        )
        // Make the user a guest
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        // Create a copy of the request with an invalid hours
        val requestCopy = baseRequest.copy(
                userId = user.id,
                elevation = 311.0
        )

        // Execute the command
        val (id, errors) = DriftScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                orientationRepo = orientationRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        ).execute()

        // Make sure the command was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.ELEVATION].isNotEmpty())
    }

    @Test
    fun testNoAllottedTimeCap_Failure() {
        // Make the user a Guest
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        // Create a copy of the baseRequest with the correct
        // user id
        val requestCopy = baseRequest.copy(userId = user.id)

        // Execute the command
        val (id, errors) = DriftScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
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
        assertTrue(errors[ErrorTag.ALLOTTED_TIME_CAP].isNotEmpty())
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

        // Create a copy of the baseRequest with the correct
        // user id
        val requestCopy = baseRequest.copy(userId = user.id)

        // Set last communication to 30 minutes in the past
        val monitor = heartbeatMonitorRepo.findByRadioTelescopeId(1L)

        assertNotNull(monitor)

        monitor!!.lastCommunication = Date(System.currentTimeMillis() - (1000 * 60 * 30))
        heartbeatMonitorRepo.save(monitor)

        val (id, errors) = DriftScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                orientationRepo = orientationRepo,
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