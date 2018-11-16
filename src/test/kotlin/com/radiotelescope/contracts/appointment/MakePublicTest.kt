package com.radiotelescope.contracts.appointment

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.user.User
import liquibase.integration.spring.SpringLiquibase
import org.junit.Assert
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
@ActiveProfiles(value = ["test"])
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
internal class MakePublicTest {
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

    @Autowired
    private lateinit var telescopeRepo: ITelescopeRepository


    private lateinit var appointment: Appointment
    private lateinit var appointmentAlreadyPublic: Appointment
    private lateinit var user: User

    private var appointmentId = -1L
    private var appointmentAlreadyPublicId = -1L

    @Before
    fun setUp() {
        // Make sure the sql script was executed
        Assert.assertEquals(1, telescopeRepo.count())

        // Persist the user
        user = testUtil.createUser(
                email = "rpim@ycp.edu",
                accountHash = "Test Account"
        )

        // Persist the appointment
        appointment = testUtil.createAppointment(
                user = user,
                startTime = Date(System.currentTimeMillis() + 10000L),
                endTime = Date(System.currentTimeMillis() + 30000L),
                isPublic = false,
                status = Appointment.Status.SCHEDULED,
                telescopeId = 1L
        )
        appointmentAlreadyPublic = testUtil.createAppointment(
                user = user,
                startTime = Date(System.currentTimeMillis() + 40000L),
                endTime = Date(System.currentTimeMillis() + 50000L),
                isPublic = true,
                status = Appointment.Status.SCHEDULED,
                telescopeId = 1L
        )
        appointmentId = appointment.id
        appointmentAlreadyPublicId = appointmentAlreadyPublic.id
    }

    @Test
    fun testValid_AppointmentExist_Success(){
        val (id, errors) = MakePublic(
                appointmentId = appointmentId,
                appointmentRepo = appointmentRepo
        ).execute()

        // Make sure it was not error
        Assert.assertNotNull(id)
        Assert.assertNull(errors)
    }

    @Test
    fun testInvalid_AppointmentDoesNotExist_Failure(){
        val (id, errors) = MakePublic(
                appointmentId = 311L,
                appointmentRepo = appointmentRepo
        ).execute()

        // Make sure it was an error
        Assert.assertNull(id)
        Assert.assertNotNull(errors)
        Assert.assertTrue(errors!![ErrorTag.ID].isNotEmpty())
    }

    @Test
    fun testInvalid_AppointmentAlreadyPublic_Failure(){
        val (id, errors) = MakePublic(
                appointmentId = appointmentAlreadyPublicId,
                appointmentRepo = appointmentRepo
        ).execute()

        // Make sure it was an error
        Assert.assertNull(id)
        Assert.assertNotNull(errors)
        Assert.assertTrue(errors!![ErrorTag.PUBLIC].isNotEmpty())
    }

}