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
internal class RetrieveByTelescopeIdTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil {
            return TestUtil()
        }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var telescopeRepo: ITelescopeRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private var userId: Long = 0

    @Before
    fun setUp() {
        // Ensure the sql script was executed
        assertEquals(1, telescopeRepo.count())

        // Persist a user
        val user = testUtil.createUser("spathcody@gmail.com")
        userId = user.id

        // Create two appointments for the telescope
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.Scheduled,
                startTime = Date(System.currentTimeMillis() + 15000L),
                endTime = Date(System.currentTimeMillis() + 30000L),
                isPublic = true
        )

        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.Scheduled,
                startTime = Date(System.currentTimeMillis() + 5000L),
                endTime = Date(System.currentTimeMillis() + 14000L),
                isPublic = true
        )
    }

    @Test
    fun retrieveByTelescopeIdTest() {
        val (page, error) = RetrieveByTelescopeId(
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                telescopeId = 1L,
                pageRequest = PageRequest.of(0, 20)
        ).execute()

        assertNotNull(page)
        assertNull(error)

        assertEquals(2, page!!.content.size)
    }

    @Test
    fun invalidTelescopeId() {
        val (page, error) = RetrieveByTelescopeId(
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                telescopeId = 311L,
                pageRequest = PageRequest.of(0, 30)
        ).execute()

        assertNotNull(error)
        assertNull(page)

        assertEquals(1, error!!.size())
        assertTrue(error[ErrorTag.TELESCOPE_ID].isNotEmpty())
    }
}