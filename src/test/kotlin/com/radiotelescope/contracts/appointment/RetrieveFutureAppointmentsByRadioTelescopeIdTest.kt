package com.radiotelescope.contracts.appointment

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner
import java.util.*


@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
internal class RetrieveFutureAppointmentsByRadioTelescopeIdTest : AbstractSpringTest() {
    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository
    @Autowired
    private lateinit var radioTelescopeRepo: IRadioTelescopeRepository

    private var userId: Long = 0

    @Before
    fun setUp() {
        // Ensure the sql script was executed
        assertEquals(1, radioTelescopeRepo.count())

        // persist a user
        val user = testUtil.createUser("jamoros@ycp.edu")
        userId = user.id

        // persist two future appointment
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis() + 100000L),
                endTime = Date(Date().time + 200000L),
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.POINT
        )

        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis() + 300000L),
                endTime = Date(System.currentTimeMillis() + 400000L),
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.POINT
        )
    }

    @Test
    fun testValidConstraints_Success() {
        val (page, error) = RetrieveFutureAppointmentsByTelescopeId(
                appointmentRepo = appointmentRepo,
                telescopeId = 1L,
                pageable = PageRequest.of(0, 2),
                radioTelescopeRepo = radioTelescopeRepo
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
                radioTelescopeRepo = radioTelescopeRepo
        ).execute()

        assertNotNull(error)
        assertNull(page)

        assertEquals(1, error!!.size())
        assertTrue(error[ErrorTag.TELESCOPE_ID].isNotEmpty())
    }
}