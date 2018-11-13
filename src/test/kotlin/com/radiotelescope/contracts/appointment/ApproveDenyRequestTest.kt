package com.radiotelescope.contracts.appointment

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository
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
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private lateinit var user: User
    private lateinit var appointment: Appointment

    @Before
    fun setUp() {
        // Persist User and Appointment
        user = testUtil.createUser("rpim@ycp.edu")
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
        var notRequested = testUtil.createAppointment(
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

        // Make sure the command was a success
        assertNull(id)
        assertNotNull(errors)

        assertTrue(errors!![ErrorTag.STATUS].isNotEmpty())
    }
}