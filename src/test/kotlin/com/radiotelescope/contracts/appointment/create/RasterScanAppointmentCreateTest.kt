package com.radiotelescope.contracts.appointment.create

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.coordinate.CoordinateRequest
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
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
internal class RasterScanAppointmentCreateTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

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

    private val baseRequest = RasterScanAppointmentCreate.Request(
            userId = -1L,
            telescopeId = 1L,
            startTime = Date(System.currentTimeMillis() + 100000L),
            endTime = Date(System.currentTimeMillis() + 300000L),
            isPublic = true,
            priority = Appointment.Priority.PRIMARY,
            coordinates = coordinateRequests
    )

    private lateinit var user: User

    private val date = Date()
    private val twoHours = 2 * 60 * 60 * 1000

    @Before
    fun setUp() {
        user = testUtil.createUser("cspath1@ycp.edu")
    }

    @Test
    fun testValidConstraintsGuest_EnoughTime_Success() {
        // Make the user a guest
        testUtil.createUserRoleForUser(
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

        val (id, errors) = RasterScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
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
        assertEquals(Appointment.Type.RASTER_SCAN, theAppointment.get().type)

        assertEquals(2, theAppointment.get().coordinateList.size)

        val theCoordinateList = theAppointment.get().coordinateList
        theCoordinateList.forEach {
            assertNotNull(it.appointment)
            assertEquals(theAppointment.get().id, it.appointment!!.id)
        }
    }

    @Test
    fun testValidConstraints_Researcher_EnoughTime_Success() {
        // Make the user a researcher
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        // Give the user 20 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (20 * 60 * 60 * 1000)
        )

        // 8 hour appointment
        val requestCopy = baseRequest.copy(
                userId = user.id,
                startTime = Date(date.time + (twoHours * 2)),
                endTime = Date( date.time + (twoHours * 2) + (twoHours * 5))
        )

        val (id, errors) = RasterScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
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
        assertEquals(Appointment.Type.RASTER_SCAN, theAppointment.get().type)

        assertEquals(2, theAppointment.get().coordinateList.size)

        val theCoordinateList = theAppointment.get().coordinateList
        theCoordinateList.forEach {
            assertNotNull(it.appointment)
            assertEquals(theAppointment.get().id, it.appointment!!.id)
        }
    }

    @Test
    fun testInvalidTelescopeId_Failure() {
        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        // Create a copy of the baseRequest with the correct
        // user id but an invalid telescope id
        val requestCopy = baseRequest.copy(
                userId = user.id,
                telescopeId = 311L
        )

        val (id, errors) = RasterScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
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
        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        // Create a copy of the baseRequest with an invalid user id
        val requestCopy = baseRequest.copy(
                userId = 311L
        )

        val (id, errors) = RasterScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
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
        // Give the user 5 hours time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = (5 * 60 * 60 * 1000)
        )

        // Create a copy of the baseRequest with the
        // start time before the end time
        val requestCopy = baseRequest.copy(
                userId = user.id,
                startTime = Date(System.currentTimeMillis() + 30000L),
                endTime = Date(System.currentTimeMillis() + 10000L)
        )

        val (id, errors) = RasterScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
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
                userId = user.id,
                startTime = Date(System.currentTimeMillis() - 10000L)
        )

        val (id, errors) = RasterScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
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
                endTime = Date(date.time + (twoHours * 5))
        )

        val (id, errors) = RasterScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
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
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.POINT
        )

        val requestCopy = baseRequest.copy(
                userId = user.id,
                startTime = Date(endTime),
                endTime = Date(endTime + 20000L)
        )

        val (id, errors) = RasterScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
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
                userId = user.id,
                coordinates = mutableListOf(coordinateCopy, coordinateRequestTwo)
        )

        val (id, errors) = RasterScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
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
                userId = user.id,
                coordinates = mutableListOf(coordinateCopy, coordinateRequestTwo)
        )

        val (id, errors) = RasterScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
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
                userId = user.id,
                coordinates = mutableListOf(coordinateCopy, coordinateRequestTwo)
        )

        val (id, errors) = RasterScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
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
                minutes = 311
        )

        val requestCopy = baseRequest.copy(
                userId = user.id,
                coordinates = mutableListOf(coordinateCopy, coordinateRequestTwo)
        )

        val (id, errors) = RasterScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
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
                userId = user.id,
                coordinates = mutableListOf(coordinateCopy, coordinateRequestTwo)
        )

        val (id, errors) = RasterScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
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
                userId = user.id,
                coordinates = mutableListOf(coordinateCopy, coordinateRequestTwo)
        )

        val (id, errors) = RasterScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
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
                userId = user.id,
                coordinates = mutableListOf(coordinateCopy, coordinateRequestTwo)
        )

        val (id, errors) = RasterScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
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
                userId = user.id,
                coordinates = mutableListOf(coordinateCopy, coordinateRequestTwo)
        )

        val (id, errors) = RasterScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
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
                userId = user.id,
                coordinates = mutableListOf()
        )

        val (id, errors) = RasterScanAppointmentCreate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.COORDINATES].isNotEmpty())
    }
}