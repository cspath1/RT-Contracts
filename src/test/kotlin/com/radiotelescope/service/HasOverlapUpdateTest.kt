package com.radiotelescope.service

import com.google.common.collect.Multimap
import com.radiotelescope.TestUtil
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.Update
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import junit.framework.Assert.fail
import liquibase.integration.spring.SpringLiquibase
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
class HasOverlapUpdateTest()
{
    @TestConfiguration
    internal class UtilTestContextConfiguration()
    {

        @Bean
        fun utilService(): TestUtil {return TestUtil() }

        @Bean
        fun liquibase(): SpringLiquibase
        {
            val liquibase = SpringLiquibase()
            liquibase.setShouldRun(false)
            return liquibase
        }
    }

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var telescopeRepo: ITelescopeRepository

    @Autowired
    private lateinit var testUtil:TestUtil

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var userRepo: IUserRepository



    @Before
    public fun setUp()
    {



    }



    @Test
    public fun hasOverlapUpdateTest()
    {


        val u1: User = testUtil.createUser("jamoros@ycp.edu")
        val u2: User = testUtil.createUser("piano1mano@gmail.com")


    //    val uR1: UserRole = UserRole(u1.id, UserRole.Role.MEMBER)
     //   val uR2: UserRole = UserRole(u2.id, UserRole.Role.MEMBER)

        testUtil.createUserRolesForUser(u1.id, UserRole.Role.MEMBER, true)
        testUtil.createUserRolesForUser(u2.id, UserRole.Role.MEMBER, true)




        val hou = HasOverlapUpdate(appointmentRepo)


        val d:Date = Date()
        println("d is: " + d)
        val a1: Appointment = testUtil.createAppointment(u1, 1, Appointment.Status.Scheduled, d, Date(d.time + 50000), true)
        val a2: Appointment = testUtil.createAppointment(u2, 1, Appointment.Status.Scheduled, Date(d.time + 60000), Date(d.time + 100000), true)
        val dd:Date = Date(d.time + 5000)

        //this one actually does get inserted into the database appointment table
        var uR = Update.Request(1, 1, dd, Date(dd.time + 70000), true)

        //At this point, two appointments are in the table. Starttime d to d+5, endtime d+6 to d+10-- no conflict
      //  println("a1 aid: " + a1.id)
     //   println("a2 aid: " + a2.id)
    //uR is associated with a1


        val uObject = Update(uR, appointmentRepo, telescopeRepo, userRoleRepo )

        //In this case, this appointment request should now not be scheduled because of the check in Create
        val executed = uObject.execute()

        var s =  executed.success

        //e should be populated
        var e: Multimap<ErrorTag, String>? = executed.error

        if (s == null) //then e is populated
        {

           if (!hou.hasOverlap(uR))
           {
               fail()
           }


            println("error should be the overlap error: " + e?.get(ErrorTag.OVERLAP).toString())
           if ((e?.get(ErrorTag.OVERLAP).toString().equals("[Appointment cannot be rescheduled: Conflict with an already-scheduled appointment]")))
           {

           }
            else fail()

            /*
            for (ee in ErrorTag.values())
            {
                println("is this populated? " + e?.get(ee))
            }

            println()
            fail()

            */
        }

        else
        {
      fail()

        }


        //At this point, we still should have the original two appointments in the database table, with the original start
        //and end Times

        println("Beginning second test:")

        println("Date() is: " + Date())
        uR = Update.Request(1, 1, Date(Date().time + 300000), Date(Date().time + 350000), true)
        println("d is: " + d)
        println("Date() is: " + Date())

        if (!hou.hasOverlap(uR))
        {

        }
        else {
            fail()
        }

       val uE =  Update(uR, appointmentRepo, telescopeRepo, userRoleRepo).execute()
       val ss =  uE.success
       val ee = uE.error


        if (ss == null)
        {
            fail()
        }

        else //success is not null, so we do not fail
        {
        }



    }
}