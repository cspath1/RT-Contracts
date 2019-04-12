package com.radiotelescope.repository.rfdata

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedAppointmentData.sql"])
internal class RFDataTest : AbstractSpringTest() {
    @Autowired
    private lateinit var radioTelescopeRepo: IRadioTelescopeRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var rfDataRepo: IRFDataRepository

    @Before
    fun setUp() {
        // Ensure the SQL Script correctly persisted everything
        assertEquals(1, radioTelescopeRepo.count())
        assertEquals(1, userRepo.count())
        assertEquals(1, appointmentRepo.count())
        assertEquals(10, rfDataRepo.count())
    }

    @Test
    fun testFindRFDataForAppointment() {
        val rfDataList = rfDataRepo.findRFDataForAppointment(1)

        assertNotNull(rfDataList)
        assertEquals(10, rfDataList.size)
    }
}