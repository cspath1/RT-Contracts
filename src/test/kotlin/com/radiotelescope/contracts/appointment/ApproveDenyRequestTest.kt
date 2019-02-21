package com.radiotelescope.contracts.appointment

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.User
import liquibase.integration.spring.SpringLiquibase
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
internal class ApproveDenyRequestTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }

        @Bean
        fun liquibase(): SpringLiquibase {
            val liquibase = SpringLiquibase()
            liquibase.setShouldRun(false)
            return liquibase
        }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private lateinit var user: User
    private lateinit var appointment: Appointment

    @Before
    fun setUp() {
        // Persist User and Appointment
        user = testUtil.createUser(
                email = "rpim@ycp.edu",
                accountHash = "Test Account 1"
        )

        appointment = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                startTime = Date(System.currentTimeMillis() + 10000L),
                endTime = Date(System.currentTimeMillis() + 30000L),
                isPublic = true,
                status = Appointment.Status.REQUESTED
        )
    }

    @Test
    fun testValid_Approve_Success(){
        // Execute the command
        val(id, errors) = ApproveDenyRequest(
                request = ApproveDenyRequest.Request(
                        appointmentId = appointment.id,
                        isApprove = true
                ),
                appointmentRepo = appointmentRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)

        assertTrue(appointmentRepo.findById(id!!).get().status == Appointment.Status.SCHEDULED)
    }

    @Test
    fun testValid_Deny_Success(){
        // Execute the command
        val(id, errors) = ApproveDenyRequest(
                request = ApproveDenyRequest.Request(
                        appointmentId = appointment.id,
                        isApprove = false
                ),
                appointmentRepo = appointmentRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)

        assertTrue(appointmentRepo.findById(id!!).get().status == Appointment.Status.CANCELED)

    }

    @Test
    fun testInvalid_AppointmentDoesNotExist_Success(){
        // Execute the command
        val(id, errors) = ApproveDenyRequest(
                request = ApproveDenyRequest.Request(
                        appointmentId = 123456789,
                        isApprove = true
                ),
                appointmentRepo = appointmentRepo
        ).execute()

        // Make sure the command was a success
        assertNull(id)
        assertNotNull(errors)

        assertTrue(errors!![ErrorTag.ID].isNotEmpty())
    }

    @Test
    fun testInvalid_AppointmentNotRequested_Success(){
        val notRequested = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                startTime = Date(System.currentTimeMillis() + 10000L),
                endTime = Date(System.currentTimeMillis() + 30000L),
                isPublic = true,
                status = Appointment.Status.SCHEDULED
        )
        // Execute the command
        val(id, errors) = ApproveDenyRequest(
                request = ApproveDenyRequest.Request(
                        appointmentId = notRequested.id,
                        isApprove = true
                ),
                appointmentRepo = appointmentRepo
        ).execute()

        // Make sure the command was not a success
        assertNull(id)
        assertNotNull(errors)

        assertTrue(errors!![ErrorTag.STATUS].isNotEmpty())
    }

    @Test
    fun testInvalidSC_StartAtStart_EndBeforeEnd_Failure(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L

        testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED
        )

        val conflict = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(startTime + 1000L),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.REQUESTED
        )


        val (id, errors) = ApproveDenyRequest(
                request = ApproveDenyRequest.Request(
                        appointmentId = conflict.id,
                        isApprove = true
                ),
                appointmentRepo = appointmentRepo
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

        testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED
        )

        val conflict = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime + 1000L),
                endTime = Date(endTime - 1000L),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.REQUESTED
        )

        val (id, errors) = ApproveDenyRequest(
                request = ApproveDenyRequest.Request(
                        appointmentId = conflict.id,
                        isApprove = true
                ),
                appointmentRepo = appointmentRepo
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

        testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED
        )

        val conflict = testUtil.createAppointment(
                user = user,
                startTime = Date(endTime - 1000L),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.REQUESTED
        )

        val (id, errors) = ApproveDenyRequest(
                request = ApproveDenyRequest.Request(
                        appointmentId = conflict.id,
                        isApprove = true
                ),
                appointmentRepo = appointmentRepo
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

        testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED
        )

        val conflict = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime - 2000L),
                endTime = Date(startTime + 500L),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.REQUESTED
        )


        val (id, errors) = ApproveDenyRequest(
                request = ApproveDenyRequest.Request(
                        appointmentId = conflict.id,
                        isApprove = true
                ),
                appointmentRepo = appointmentRepo
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

        testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED
        )

        val conflict = testUtil.createAppointment(
                user = user,
                startTime = Date(endTime - 500L),
                endTime = Date(endTime + 1000L),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.REQUESTED
        )

        val (id, errors) = ApproveDenyRequest(
                request = ApproveDenyRequest.Request(
                        appointmentId = conflict.id,
                        isApprove = true
                ),
                appointmentRepo = appointmentRepo
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

        testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED
        )

        val conflict = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime - 1000L),
                endTime = Date(startTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.REQUESTED
        )

        val (id, errors) = ApproveDenyRequest(
                request = ApproveDenyRequest.Request(
                        appointmentId = conflict.id,
                        isApprove = true
                ),
                appointmentRepo = appointmentRepo
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

        testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED
        )

        val conflict = testUtil.createAppointment(
                user = user,
                startTime = Date(endTime),
                endTime = Date(endTime + 2000L),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.REQUESTED
        )

        val (id, errors) = ApproveDenyRequest(
                request = ApproveDenyRequest.Request(
                        appointmentId = conflict.id,
                        isApprove = true
                ),
                appointmentRepo = appointmentRepo
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

        testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED
        )

        val conflict = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime - 1111L),
                endTime = Date(endTime + 1111L),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.REQUESTED
        )


        val (id, errors) = ApproveDenyRequest(
                request = ApproveDenyRequest.Request(
                        appointmentId = conflict.id,
                        isApprove = true
                ),
                appointmentRepo = appointmentRepo
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

        testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED
        )

        val conflict = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.REQUESTED
        )

        val (id, errors) = ApproveDenyRequest(
                request = ApproveDenyRequest.Request(
                        appointmentId = conflict.id,
                        isApprove = true
                ),
                appointmentRepo = appointmentRepo
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
        testUtil.createUserRolesForUser(
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
                status = Appointment.Status.REQUESTED
        )

        val conflict = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime + 1L),
                endTime = Date(endTime + 1L),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.REQUESTED
        )


        val (id, errors) = ApproveDenyRequest(
                request = ApproveDenyRequest.Request(
                        appointmentId = conflict.id,
                        isApprove = true
                ),
                appointmentRepo = appointmentRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)
    }

    @Test
    fun testValidSC_Canceled_Success(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L
        testUtil.createUserRolesForUser(
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
                status = Appointment.Status.CANCELED
        )

        val conflict = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime + 1L),
                endTime = Date(endTime + 1L),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.REQUESTED
        )


        val (id, errors) = ApproveDenyRequest(
                request = ApproveDenyRequest.Request(
                        appointmentId = conflict.id,
                        isApprove = true
                ),
                appointmentRepo = appointmentRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)
    }

    @Test
    fun testValidSC_ConflictButApproveFalse_Success(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L

        testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED
        )

        val conflict = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.REQUESTED
        )

        val (id, errors) = ApproveDenyRequest(
                request = ApproveDenyRequest.Request(
                        appointmentId = conflict.id,
                        isApprove = false
                ),
                appointmentRepo = appointmentRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)
    }

}