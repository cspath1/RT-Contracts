package com.radiotelescope.service

import com.radiotelescope.TestUtil
import com.radiotelescope.contracts.appointment.Create
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.role.UserRole
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
    class HasOverlapCreateTest
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


        var d:Date = Date()
        var u:User = User("Tom", "Sawyer", "kid@marktwain.com", "inLoveWithGloria" )
        var u_id:Long = 0

    @Before
    fun setUp()
    {
        Assert.assertEquals(1, telescopeRepo.count())
        val ds: Date = Date(Date().time+ 100000)
        d = ds
        val us: User = testUtil.createUser("jamoros@ycp.edu")
        var a: Appointment = testUtil.createAppointment(us, 1, Appointment.Status.SCHEDULED, d, Date(d.time + 50000), true)
        testUtil.createUserRolesForUser(us.id, UserRole.Role.MEMBER, true)
        u = us
        u_id = u.id
    }

    @Test
     fun leftOverlap() {

        val uC = Create.Request(u_id, Date(d.time - 10000), Date(d.time + 10000), 1, true)

        val hoc = HasOverlap(appointmentRepo)
        if (!hoc.hasOverlapCreate(uC).isEmpty())
        {
        }
        else if (hoc.hasOverlapCreate(uC).isNotEmpty())
        {
            fail()
        }
    }

        @Test
        fun rightOverlap() {

            val uC = Create.Request(u_id, Date(d.time + 30000), Date(d.time + 60000), 1, true)
            val hoc = HasOverlap(appointmentRepo)
            if (hoc.hasOverlapCreate(uC).isNotEmpty())
            {
            }
            else if (hoc.hasOverlapCreate(uC).isEmpty())
            {
                fail()
            }
        }
        @Test
        fun BothEndsOverlap() {

            val uC = Create.Request(u_id, Date(d.time - 10000), Date(d.time + 60000), 1, true)
            val hoc = HasOverlap(appointmentRepo)
            if (hoc.hasOverlapCreate(uC).isNotEmpty())
            {
            }
            else if (hoc.hasOverlapCreate(uC).isEmpty())
            {
                fail()
            }
        }

        @Test
        fun InnerOverlap() {

            val uC = Create.Request(u_id, Date(d.time + 30000), Date(d.time + 40000), 1, true)
            val hoc = HasOverlap(appointmentRepo)
            if (hoc.hasOverlapCreate(uC).isNotEmpty())
            {

            }
            else if (hoc.hasOverlapCreate(uC).isEmpty())
            {
                fail()
            }
        }

        @Test
         fun noOverlapLeft() {

            val uC = Create.Request(u_id, Date(d.time - 10000), Date(d.time - 5000), 1, true)
            val hoc = HasOverlap(appointmentRepo)
            if (hoc.hasOverlapCreate(uC).isNotEmpty())
            {
                fail()
            }
            else if (hoc.hasOverlapCreate(uC).isEmpty())
            {
            }
        }

        @Test
          fun noOverlapRight() {
            val uC = Create.Request(u_id, Date(d.time + 60000), Date(d.time +70000), 1, true)
            val hoc = HasOverlap(appointmentRepo)
            if (hoc.hasOverlapCreate(uC).isNotEmpty())
            {
                fail()
            }
            else if (hoc.hasOverlapCreate(uC).isEmpty())
            {
            }
        }

}