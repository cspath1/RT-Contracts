package com.radiotelescope.service

import com.radiotelescope.TestUtil
import com.radiotelescope.contracts.appointment.Create
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.user.User
import junit.framework.Assert.fail
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
    class HasOverlapCreateTest()
    {
        @TestConfiguration
        internal class UtilTestContextConfiguration()
        {
            @Bean
            fun utilService(): TestUtil {return TestUtil() }

            @Bean
            fun liquibase():SpringLiquibase
            {
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

    @Before
    public fun setUp()
    {
        Assert.assertEquals(1, telescopeRepo.count())
    }

    @Test
    public fun hasOverlapCreateTest() {
        val d: Date = Date()
        val u: User = testUtil.createUser("jamoros@ycp.edu")
        val a: Appointment = testUtil.createAppointment(u, 1, Appointment.Status.Scheduled, d, Date(d.time + 5000), true)

        val uC = Create.Request(1, d, Date(d.time + 10000), 1, true)

        println("telescopeId of appointment requested: " + uC.telescopeId)

        val hoc = HasOverlapCreate(appointmentRepo)
        if (hoc.hasOverlap(uC))
        {

        }
        else if (!hoc.hasOverlap( uC))
        {
            fail()
        }
    }
}