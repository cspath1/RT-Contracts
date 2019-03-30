package com.radiotelescope.contracts.rfdata

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.rfdata.IRFDataRepository
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
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
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedAppointmentData.sql"])
@ActiveProfiles(value = ["test"])
internal class RetrieveAppointmentDataTest {
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
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var radioTelescopeRepo: IRadioTelescopeRepository

    @Autowired
    private lateinit var rfDataRepo: IRFDataRepository

    private lateinit var user: User
    private lateinit var uncompletedAppointment: Appointment

    @Before
    fun setUp() {
        // Ensure the sql script was executed
        assertEquals(1, radioTelescopeRepo.count())
        assertEquals(1, userRepo.count())
        assertEquals(1, appointmentRepo.count())
        assertEquals(10, rfDataRepo.count())

        // Persist another user
        user = testUtil.createUser("cspath617@gmail.com")

        // Persist a new, not completed appointment
        uncompletedAppointment = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis() + 10000L),
                endTime = Date(System.currentTimeMillis() + 30000L),
                isPublic = true,
                type = Appointment.Type.POINT
        )
    }

    @Test
    fun testValidConstraints_Success() {
        // Execute the command on the seeded appointment
        // that is completed and has RFData
        val (data, errors) = RetrieveAppointmentData(
                appointmentId = 1L,
                appointmentRepo = appointmentRepo,
                rfDataRepo = rfDataRepo
        ).execute()

        assertNotNull(data)
        assertNull(errors)

        assertEquals(10, data!!.size)
    }

    @Test
    fun testInvalidAppointmentId_Error() {
        // Execute the command on a non-existent
        // appointment id
        val (data, errors) = RetrieveAppointmentData(
                appointmentId = 311L,
                appointmentRepo = appointmentRepo,
                rfDataRepo = rfDataRepo
        ).execute()

        assertNull(data)
        assertNotNull(errors)

        assertTrue(errors!![ErrorTag.APPOINTMENT_ID].isNotEmpty())
    }

    @Test
    fun testAppointmentNotCompleted_Failure() {
        // Execute the command on the not completed appointment
        val (data, errors) = RetrieveAppointmentData(
                appointmentId = uncompletedAppointment.id,
                appointmentRepo = appointmentRepo,
                rfDataRepo = rfDataRepo
        ).execute()

        assertNull(data)
        assertNotNull(errors)

        assertTrue(errors!![ErrorTag.APPOINTMENT_STATUS].isNotEmpty())
    }
}