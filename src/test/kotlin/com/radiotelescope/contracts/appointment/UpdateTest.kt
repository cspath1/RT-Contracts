package com.radiotelescope.contracts.appointment

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.telescope.Telescope
import com.radiotelescope.repository.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import java.util.*


@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class UpdateTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var telescopeRepo: ITelescopeRepository


    private lateinit var appointment: Appointment
    private lateinit var user: User
    private lateinit var telescope: Telescope

    private var appointmentId = -1L
    private var userId = -1L
    private var telescopeId = -1L


    @Before
    fun setUp() {
        // Persist the user
        user = testUtil.createUser(
                email = "rpim@ycp.edu"
        )
        userId = user.id

        // Persist the telescope
        telescope = testUtil.createTelescope()
        telescopeId = telescope.getId()

        // Persist the appointment
        appointment = testUtil.createAppointment(
                user = user,
                startTime = Date(System.currentTimeMillis() + 10000L),
                endTime = Date(System.currentTimeMillis() + 30000L),
                isPublic = true,
                status = Appointment.Status.Scheduled,
                telescopeId = telescopeId
        )
        appointmentId = appointment.id
    }


    //TODO: Add more tests when checking to scheduling conflict is done
    @Test
    fun testValid_CorrectConstraints_Success(){
        val (id, errors) = Update(
                request = Update.Request(
                        id = appointmentId,
                        startTime = Date(appointment.endTime.time + 10000L),
                        endTime = Date(appointment.endTime.time + 40000L),
                        telescopeId = telescopeId,
                        isPublic = false
                ),
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo

        ).execute()

        // Make sure it was not error
        assertNotNull(id)
        assertNull(errors)

    }

    @Test
    fun testInvalid_AppointmentDoesNotExist_Failure() {
        val (id, errors) = Update(
                request = Update.Request(
                        id = 123456789,
                        startTime = Date(appointment.endTime.time + 10000L),
                        endTime = Date(appointment.endTime.time + 40000L),
                        telescopeId = telescopeId,
                        isPublic = appointment.isPublic

                ),
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo

        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the expected reason
        assertTrue(errors!![ErrorTag.ID].isNotEmpty())
    }


    @Test
    fun testInvalid_StartTimeGreaterThanEndTime_Failure() {
        val (id, errors) = Update(
                request = Update.Request(
                        id = appointmentId,
                        startTime = Date(appointment.endTime.time + 40000L),
                        endTime = Date(appointment.endTime.time + 10000L),
                        telescopeId = telescopeId,
                        isPublic = appointment.isPublic

                ),
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo

        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the expected reason
        assertTrue(errors!![ErrorTag.END_TIME].isNotEmpty())
    }

    @Test
    fun testInvalid_StartTimeInPast_Failure() {
        val (id, errors) = Update(
                request = Update.Request(
                        id = appointmentId,
                        startTime = Date(System.currentTimeMillis() - 10000L),
                        endTime = Date(appointment.endTime.time + 40000L),
                        telescopeId = telescopeId,
                        isPublic = appointment.isPublic
                ),
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo

        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the expected reason
        assertTrue(errors!![ErrorTag.START_TIME].isNotEmpty())
    }


    @Test
    fun testInvalid_TelescopeDoesNotExist_Failure() {
        val (id, errors) = Update(
                request = Update.Request(
                        id = appointmentId,
                        startTime = Date(appointment.endTime.time + 10000L),
                        endTime = Date(appointment.endTime.time + 40000L),
                        telescopeId = 123456789,
                        isPublic = appointment.isPublic
                ),
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo

        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the expected reason
        assertTrue(errors!![ErrorTag.TELESCOPE_ID].isNotEmpty())
    }

}