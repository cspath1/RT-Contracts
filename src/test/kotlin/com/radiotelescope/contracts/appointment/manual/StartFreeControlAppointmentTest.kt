package com.radiotelescope.contracts.appointment.manual

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.contracts.appointment.ErrorTag
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
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
internal class StartFreeControlAppointmentTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var radioTelescopeRepo: IRadioTelescopeRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    private lateinit var baseRequest: StartFreeControlAppointment.Request

    private lateinit var user: User

    @Before
    fun setUp() {
        user = testUtil.createUser("cspath1@ycp.edu")

        baseRequest = StartFreeControlAppointment.Request(
                userId = user.id,
                telescopeId = 1L,
                duration = 30,
                hours = 5,
                minutes = 34,
                seconds = 32,
                declination = 22.0,
                isPublic = true
        )
    }

    @Test
    fun testValidConstraints_Success() {
        // Execute the command
        val (id, errors) = StartFreeControlAppointment(
                request = baseRequest,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRepo = userRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)

        // And make sure the appointment was persisted correctly
        val theAppointment = appointmentRepo.findById(id!!).get()

        assertEquals(baseRequest.telescopeId, theAppointment.telescopeId)
        assertEquals(baseRequest.userId, theAppointment.user.id)
        assertEquals(1, theAppointment.coordinateList.size)
        assertEquals(baseRequest.isPublic, theAppointment.isPublic)
        assertEquals(Appointment.Type.FREE_CONTROL, theAppointment.type)
        assertTrue(theAppointment.startTime.before(theAppointment.endTime))

        val startTime = theAppointment.startTime
        val endTime = Date(startTime.time + (baseRequest.duration * 1000 * 60))
        assertEquals(endTime, theAppointment.endTime)
    }

    @Test
    fun testInvalidTelescopeId_Failure() {
        // Create a copy of the base request with an invalid
        // telescope id
        val requestCopy = baseRequest.copy(
                telescopeId = 311L
        )

        val (id, errors) = StartFreeControlAppointment(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRepo = userRepo,
                coordinateRepo = coordinateRepo
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
                userId = 311L
        )

        val (id, errors) = StartFreeControlAppointment(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRepo = userRepo,
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
    fun testHoursTooLow_Failure() {
        // Create a copy of the request with an invalid hours field
        val requestCopy = baseRequest.copy(
                hours = -311
        )

        val (id, errors) = StartFreeControlAppointment(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRepo = userRepo,
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
        // Create a copy of the request with an invalid hours field
        val requestCopy = baseRequest.copy(
                hours = 311
        )

        val (id, errors) = StartFreeControlAppointment(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRepo = userRepo,
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
        // Create a copy of the request with an invalid minutes
        val requestCopy = baseRequest.copy(
                minutes = -311
        )

        val (id, errors) = StartFreeControlAppointment(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRepo = userRepo,
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
        // Create a copy of the request with an invalid minutes
        val requestCopy = baseRequest.copy(
                minutes = 311
        )

        val (id, errors) = StartFreeControlAppointment(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRepo = userRepo,
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
        // Create a copy of the request with invalid seconds
        val requestCopy = baseRequest.copy(
                seconds = -311
        )

        val (id, errors) = StartFreeControlAppointment(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRepo = userRepo,
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
        // Create a copy of the request with invalid seconds
        val requestCopy = baseRequest.copy(
                seconds = 311
        )

        val (id, errors) = StartFreeControlAppointment(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRepo = userRepo,
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
        // Create a copy of the request with an invalid declination
        val requestCopy = baseRequest.copy(
                declination = -311.0
        )

        val (id, errors) = StartFreeControlAppointment(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRepo = userRepo,
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
        // Create a copy of the request with an invalid declination
        val requestCopy = baseRequest.copy(
                declination = 311.0
        )

        val (id, errors) = StartFreeControlAppointment(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRepo = userRepo,
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
    fun testExistingFreeControlAppointment_Failure() {
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.IN_PROGRESS,
                startTime = Date(System.currentTimeMillis() - 150000L),
                endTime = Date(System.currentTimeMillis() + 150000L),
                isPublic = true,
                type = Appointment.Type.FREE_CONTROL
        )

        val (id, errors) = StartFreeControlAppointment(
                request = baseRequest,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRepo = userRepo,
                coordinateRepo = coordinateRepo
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.OVERLAP].isNotEmpty())
    }
}