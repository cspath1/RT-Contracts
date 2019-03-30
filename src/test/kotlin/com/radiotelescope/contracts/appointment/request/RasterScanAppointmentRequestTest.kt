package com.radiotelescope.contracts.appointment.request

import com.radiotelescope.TestUtil
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.coordinate.CoordinateRequest
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
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
internal class RasterScanAppointmentRequestTest {
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
    private lateinit var coordinateRepo: ICoordinateRepository

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

    private val baseRequest = RasterScanAppointmentRequest.Request(
            userId = -1L,
            telescopeId = 1L,
            startTime = Date(System.currentTimeMillis() + 100000L),
            endTime = Date(System.currentTimeMillis() + 300000L),
            isPublic = true,
            coordinates = coordinateRequests
    )

    private lateinit var user: User

    @Before
    fun setUp() {
        user = testUtil.createUser("cspath1@ycp.edu")
    }

    @Test
    fun testValidConstraints_Success() {
        // Create a copy of the base request with the correct user id
        val requestCopy = baseRequest.copy(userId = user.id)

        val (id, errors) =  RasterScanAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)

        // And make sure the appointment was persisted properly
        val theAppointment = appointmentRepo.findById(id!!).get()
        assertNotNull(theAppointment)

        assertEquals(requestCopy.startTime, theAppointment.startTime)
        assertEquals(requestCopy.endTime, theAppointment.endTime)
        assertEquals(requestCopy.telescopeId, theAppointment.telescopeId)
        assertEquals(requestCopy.userId, theAppointment.user.id)
        assertTrue(theAppointment.isPublic)
        assertEquals(Appointment.Status.REQUESTED, theAppointment.status)
        assertEquals(Appointment.Type.RASTER_SCAN, theAppointment.type)

        assertEquals(2, theAppointment.coordinateList.size)

        val theCoordinateList = theAppointment.coordinateList
        theCoordinateList.forEach {
            assertNotNull(it.appointment)
            assertEquals(theAppointment.id, it.appointment!!.id)
        }
    }

    @Test
    fun testInvalidUserId_Failure() {
        // Create a copy of the baseRequest with an invalid user id
        val requestCopy = baseRequest.copy(
                userId = 311L
        )

        val (id, errors) =  RasterScanAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo
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
        // Create a copy of the baseRequest with the
        // start time before the end time
        val requestCopy = baseRequest.copy(
                userId = user.id,
                startTime = Date(System.currentTimeMillis() + 30000L),
                endTime = Date(System.currentTimeMillis() + 10000L)
        )

        val (id, errors) =  RasterScanAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo
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
        val requestCopy = baseRequest.copy(
                userId = user.id,
                startTime = Date(System.currentTimeMillis() - 10000L)
        )

        val (id, errors) =  RasterScanAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.START_TIME].isNotEmpty())
    }

    @Test
    fun testHoursTooLow_Failure() {
        val coordinateCopy = coordinateRequestOne.copy(
                hours = -311
        )

        val requestCopy = baseRequest.copy(
                userId = user.id,
                coordinates = mutableListOf(coordinateCopy, coordinateRequestTwo)
        )

        val (id, errors) =  RasterScanAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo
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
        val coordinateCopy = coordinateRequestOne.copy(
                hours = 311
        )

        val requestCopy = baseRequest.copy(
                userId = user.id,
                coordinates = mutableListOf(coordinateCopy, coordinateRequestTwo)
        )

        val (id, errors) =  RasterScanAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo
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
        val coordinateCopy = coordinateRequestOne.copy(
                minutes = -311
        )

        val requestCopy = baseRequest.copy(
                userId = user.id,
                coordinates = mutableListOf(coordinateCopy, coordinateRequestTwo)
        )

        val (id, errors) =  RasterScanAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo
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
        val coordinateCopy = coordinateRequestOne.copy(
                minutes = 311
        )

        val requestCopy = baseRequest.copy(
                userId = user.id,
                coordinates = mutableListOf(coordinateCopy, coordinateRequestTwo)
        )

        val (id, errors) =  RasterScanAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo
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
        val coordinateCopy = coordinateRequestOne.copy(
                seconds = -311
        )

        val requestCopy = baseRequest.copy(
                userId = user.id,
                coordinates = mutableListOf(coordinateCopy, coordinateRequestTwo)
        )

        val (id, errors) =  RasterScanAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo
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
        val coordinateCopy = coordinateRequestOne.copy(
                seconds = 311
        )

        val requestCopy = baseRequest.copy(
                userId = user.id,
                coordinates = mutableListOf(coordinateCopy, coordinateRequestTwo)
        )

        val (id, errors) =  RasterScanAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo
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
        val coordinateCopy = coordinateRequestOne.copy(
                declination = -311.0
        )

        val requestCopy = baseRequest.copy(
                userId = user.id,
                coordinates = mutableListOf(coordinateCopy, coordinateRequestTwo)
        )

        val (id, errors) =  RasterScanAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo
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
        val coordinateCopy = coordinateRequestOne.copy(
                declination = 311.0
        )

        val requestCopy = baseRequest.copy(
                userId = user.id,
                coordinates = mutableListOf(coordinateCopy, coordinateRequestTwo)
        )

        val (id, errors) =  RasterScanAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo
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
        val requestCopy = baseRequest.copy(
                userId = user.id,
                coordinates = mutableListOf()
        )

        val (id, errors) =  RasterScanAppointmentRequest(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.COORDINATES].isNotEmpty())
    }
}