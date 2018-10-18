package com.radiotelescope.contracts.appointment


import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
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
internal class CreateTest {
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

    private val baseRequest = Create.Request(
            userId = -1L,
            telescopeId = 1L,
            startTime = Date(System.currentTimeMillis() + 10000L),
            endTime = Date(System.currentTimeMillis() + 30000L),
            isPublic = true
    )

    private lateinit var user: User

    @Before
    fun setUp() {
        user = testUtil.createUser("cspath1@ycp.edu")
    }

    @Test
    fun testValidConstraints_Success() {
        // Create a copy of the baseRequest with the correct
        // user id
        val requestCopy = baseRequest.copy(userId = user.id)

        // Execute the command
        val (id, errors) = Create(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo
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
        assertEquals(requestCopy.userId, theAppointment.get().user!!.id)
        assertTrue(theAppointment.get().isPublic)
    }

    @Test
    fun testInvalidTelescopeId_Failure() {
        // Create a copy of the baseRequest with the correct
        // user id but an invalid telescope id
        val requestCopy = baseRequest.copy(
                userId = user.id,
                telescopeId = 311L
        )

        val (id, errors) = Create(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo
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
        val (id, errors) = Create(
                request = baseRequest,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo
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

        // Execute the command
        val (id, errors) = Create(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo
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

        // Execute the command
        val (id, errors) = Create(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.START_TIME].isNotEmpty())
    }

}