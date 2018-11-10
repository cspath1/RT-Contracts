package com.radiotelescope.contracts.appointment

import com.radiotelescope.repository.appointment.Appointment
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
import liquibase.integration.spring.SpringLiquibase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import java.util.*


@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
internal class RetrieveFutureAppointmentsByTelescopeIdTest {
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

    private var userId: Long = 0

    @Before
    fun setUp() {
        // Ensure the sql script was executed
        assertEquals(1, telescopeRepo.count())

        // persist a user
        val user = testUtil.createUser(
                email = "jamoros@ycp.edu",
                accountHash = "Test Account"
        )
        userId = user.id

        // persist two future appointment
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis() + 100000L),
                endTime = Date(Date().time + 200000L),
                isPublic = true
        )

        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis() + 300000L),
                endTime = Date(System.currentTimeMillis() + 400000L),
                isPublic = true
        )
    }

    @Test
    fun testValidConstraints_Success() {
        val (page, error) = RetrieveFutureAppointmentsByTelescopeId(
                appointmentRepo = appointmentRepo,
                telescopeId = 1L,
                pageable = PageRequest.of(0, 2),
                telescopeRepo = telescopeRepo
        ).execute()

        assertNotNull(page)
        assertNull(error)

        assertEquals(2, page!!.content.size)
    }

    @Test
    fun testNonExistentTelescopeId_Failure() {
        val (page, error) = RetrieveFutureAppointmentsByTelescopeId(
                appointmentRepo = appointmentRepo,
                telescopeId = 311L,
                pageable = PageRequest.of(0, 30),
                telescopeRepo = telescopeRepo
        ).execute()

        assertNotNull(error)
        assertNull(page)

        assertEquals(1, error!!.size())
        assertTrue(error[ErrorTag.TELESCOPE_ID].isNotEmpty())
    }
}