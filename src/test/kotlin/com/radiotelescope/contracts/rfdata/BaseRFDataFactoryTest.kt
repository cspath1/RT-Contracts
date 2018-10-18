package com.radiotelescope.contracts.rfdata

import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.rfdata.IRFDataRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class BaseRFDataFactoryTest {
    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var rfDataRepo: IRFDataRepository

    private lateinit var factory: RFDataFactory

    @Before
    fun init() {
        // Instantiate the factory
        factory = BaseRFDataFactory(
                appointmentRepo = appointmentRepo,
                rfDataRepo = rfDataRepo
        )
    }

    @Test
    fun retrieveAppointmentData() {
        // Call the factory method
        val cmd = factory.retrieveAppointmentData(
                appointmentId = 1L
        )

        // Ensure it is the correct command
        assertTrue(cmd is RetrieveAppointmentData)
    }
}