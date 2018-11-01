package com.radiotelescope.contracts.rfdata

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.rfdata.IRFDataRepository
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
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class BaseRFDataFactoryTest {
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