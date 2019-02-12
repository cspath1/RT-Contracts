package com.radiotelescope.contracts.viewer

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.repository.viewer.IViewerRepository
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
internal class SharePrivateAppointmentTest {
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
    private lateinit var viewerRepo: IViewerRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private lateinit var user: User
    private lateinit var otherUser: User
    private lateinit var appointment: Appointment

    @Before
    fun setUp() {
        user = testUtil.createUser("rpim@ycp.edu")
        otherUser = testUtil.createUser("rpim1@ycp.edu")
        appointment = testUtil.createAppointment(
                user = otherUser,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis() + 100000L),
                endTime = Date(System.currentTimeMillis() + 300000L),
                isPublic = false
        )
    }

    @Test
    fun testValidConstraints_Success() {
        //Create the request
        val request = SharePrivateAppointment.Request(
                userId = user.id,
                appointmentId = appointment.id
        )

        // Execute the command
        val (id, errors) = SharePrivateAppointment(
                request = request,
                viewerRepo = viewerRepo,
                userRepo = userRepo,
                appointmentRepo = appointmentRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)

        // Make sure the viewer was persisted
        val theViewer = viewerRepo.findById(id!!)
        assertTrue(theViewer.isPresent)

        // Make sure the correct information was persisted
        assertEquals(request.userId, theViewer.get().user.id)
        assertEquals(request.appointmentId, theViewer.get().appointment.id)
    }

    @Test
    fun testInvalid_UserDoesNotExist_Failure(){
        //Create the request
        val request = SharePrivateAppointment.Request(
                userId = -1L,
                appointmentId = appointment.id
        )

        // Execute the command
        val (id, errors) = SharePrivateAppointment(
                request = request,
                viewerRepo = viewerRepo,
                userRepo = userRepo,
                appointmentRepo = appointmentRepo
        ).execute()

        // Make sure the command was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.USER_ID].isNotEmpty())
    }

    @Test
    fun testInvalid_AppointmentDoesNotExist_Failure(){
        //Create the request
        val request = SharePrivateAppointment.Request(
                userId = user.id,
                appointmentId = -1L
        )

        // Execute the command
        val (id, errors) = SharePrivateAppointment(
                request = request,
                viewerRepo = viewerRepo,
                userRepo = userRepo,
                appointmentRepo = appointmentRepo
        ).execute()

        // Make sure the command was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.ID].isNotEmpty())
    }

    @Test
    fun testInvalid_AppointmentIsNotPrivate_Failure(){
        val publicAppointment = testUtil.createAppointment(
                user = otherUser,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis() + 400000L),
                endTime = Date(System.currentTimeMillis() + 500000L),
                isPublic = true
        )
        //Create the request
        val request = SharePrivateAppointment.Request(
                userId = user.id,
                appointmentId = publicAppointment.id
        )

        // Execute the command
        val (id, errors) = SharePrivateAppointment(
                request = request,
                viewerRepo = viewerRepo,
                userRepo = userRepo,
                appointmentRepo = appointmentRepo
        ).execute()

        // Make sure the command was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.PRIVATE].isNotEmpty())
    }


}