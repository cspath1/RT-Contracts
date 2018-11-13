package com.radiotelescope.contracts.appointment

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.telescope.Telescope
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
internal class RequestTest {
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

    @Autowired
    private lateinit var telescopeRepo: ITelescopeRepository

    private val baseRequest = Request.Request(
            userId = -1L,
            telescopeId = 1L,
            startTime = Date(System.currentTimeMillis() + 10000L),
            endTime = Date(System.currentTimeMillis() + 30000L),
            isPublic = true
    )

    private lateinit var user: User

    private lateinit var telescope: Telescope

    @Before
    fun setUp() {
        // Persist User and Telescope
        user = testUtil.createUser("rpim@ycp.edu")
        telescope = testUtil.createTelescope()
    }

    @Test
    fun testValid_CorrectConstraints_Success(){
        // Execute the command
        val (id, errors) = Request(
                request =  Request.Request(
                        userId = user.id,
                        telescopeId = telescope.getId(),
                        startTime = baseRequest.startTime,
                        endTime = baseRequest.endTime,
                        isPublic = baseRequest.isPublic
                ),
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)
    }

    @Test
    fun testInvalid_UserDoesNotExist_Failure(){
        // Execute the command
        val (id, errors) = Request(
                request =  Request.Request(
                        userId = 123456789,
                        telescopeId = telescope.getId(),
                        startTime = baseRequest.startTime,
                        endTime = baseRequest.endTime,
                        isPublic = baseRequest.isPublic
                ),
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo
        ).execute()

        // Make sure the command was a success
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertTrue(errors!![ErrorTag.USER_ID].isNotEmpty())
    }

    @Test
    fun testInvalid_TelescopeDoesNotExist_Failure(){
        // Execute the command
        val (id, errors) = Request(
                request =  Request.Request(
                        userId = user.id,
                        telescopeId = 123456789,
                        startTime = baseRequest.startTime,
                        endTime = baseRequest.endTime,
                        isPublic = baseRequest.isPublic
                ),
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo
        ).execute()

        // Make sure the command was a success
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertTrue(errors!![ErrorTag.TELESCOPE_ID].isNotEmpty())
    }

    @Test
    fun testInvalid_StartTimeIsBeforeCurrentTime_Failure(){
        // Execute the command
        val (id, errors) = Request(
                request =  Request.Request(
                        userId = user.id,
                        telescopeId = telescope.getId(),
                        startTime = Date(System.currentTimeMillis() - 1000L),
                        endTime = baseRequest.endTime,
                        isPublic = baseRequest.isPublic
                ),
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo
        ).execute()

        // Make sure the command was a success
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertTrue(errors!![ErrorTag.START_TIME].isNotEmpty())
    }

    @Test
    fun testInvalid_StartTimeIsAfterEndTime_Failure(){
        // Execute the command
        val (id, errors) = Request(
                request =  Request.Request(
                        userId = user.id,
                        telescopeId = telescope.getId(),
                        startTime = baseRequest.endTime,
                        endTime = baseRequest.startTime,
                        isPublic = baseRequest.isPublic
                ),
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo
        ).execute()

        // Make sure the command was a success
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertTrue(errors!![ErrorTag.END_TIME].isNotEmpty())
    }
}