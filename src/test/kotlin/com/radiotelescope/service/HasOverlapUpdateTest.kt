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
class HasOverlapUpdateTest
{
    @TestConfiguration
    internal class UtilTestContextConfiguration
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

    var u1:User = User("Josh", "123", "j@ycp.edu", "hahaHilarious")
    var u2:User = User("SupFool", "abc", "quixotic@ycp.edu", "hahaFinn")
    var d:Date = Date()
    var a1_id:Long = 0

    @Before
    fun setUp()
    {
        var ds: Date = Date(Date().time + 100000)
        val u1s: User = testUtil.createUser("jamoros@ycp.edu")
        val u2s: User = testUtil.createUser("piano1mano@gmail.com")

        testUtil.createUserRolesForUser(u1s.id, UserRole.Role.MEMBER, true)
        testUtil.createUserRolesForUser(u2s.id, UserRole.Role.MEMBER, true)

        u1 = u1s
        u2 = u2s
        d = ds

        //updates are called on a1
        val a1: Appointment = testUtil.createAppointment(u1, 1, Appointment.Status.SCHEDULED, d, Date(d.time + 50000), true)
        //sometimes tested against a2
        val a2: Appointment = testUtil.createAppointment(u2, 1, Appointment.Status.SCHEDULED, Date(d.time + 60000), Date(d.time + 100000), true)

        a1_id = a1.id
    }

    @Test
    fun updateToRightOverlap()
    {
        val hou = HasOverlap(appointmentRepo)
       val dd:Date = Date(d.time + 5000)

        //associated with a1
        val uR = Update.Request(a1_id, 1, Date(d.time + 90000), Date(dd.time + 110000), true)
        val uObject = Update(uR, appointmentRepo, telescopeRepo, userRoleRepo)

        //In this case, this appointment request should now NOT be scheduled because of the check in Create
        val executed = uObject.execute()
        val s = executed.success

        //e should be populated
        val e: Multimap<ErrorTag, String>? = executed.error
        if (s == null)
        {
            //no overlap case
            //if no overlap, either list is empty or list is 1 and listAppts.size == 1 && a.id == request.id
            if (hou.hasOverlapUpdate(uR).isEmpty() || (hou.hasOverlapUpdate(uR).size == 1 && uR.id == hou.hasOverlapUpdate(uR).get(0).id) ) {
                fail()
            }

            println("error should be the overlap error: " + e?.get(ErrorTag.OVERLAP).toString())
            if ((e?.get(ErrorTag.OVERLAP).toString().equals("[Appointment cannot be rescheduled: Conflict with an already-scheduled appointment]")))
            {
            //pass
            }
            else fail()

        } else {
            fail()
        }
    }

    @Test
    fun updateToLeftOverlap() {

        val hou = HasOverlap(appointmentRepo)
        val dd: Date = Date(d.time + 5000)
        val uR = Update.Request(a1_id, 1, dd, Date(dd.time + 70000), true)
        val uObject = Update(uR, appointmentRepo, telescopeRepo, userRoleRepo)

        //In this case, this appointment request should now NOT be scheduled because of the check in Create
        val executed = uObject.execute()
        val s = executed.success
        //e should be populated
        val e: Multimap<ErrorTag, String>? = executed.error
        if (s == null) //then e is populated
        {
            //no overlap case
            if (hou.hasOverlapUpdate(uR).isEmpty() || (hou.hasOverlapUpdate(uR).size == 1 && uR.id == hou.hasOverlapUpdate(uR).get(0).id)) {
                fail()
            }
            println("error should be the overlap error: " + e?.get(ErrorTag.OVERLAP).toString())
            if ((e?.get(ErrorTag.OVERLAP).toString().equals("[Appointment cannot be rescheduled: Conflict with an already-scheduled appointment]")))
            { //pass
            }
            else fail()
        } else {
            fail()
        }
    }

    @Test
    fun updateToFutureNoOverlap() {

        val uR = Update.Request(a1_id, 1, Date(d.time + 300000), Date(d.time + 350000), true)
        val hou = HasOverlap(appointmentRepo)
        if (hou.hasOverlapUpdate(uR).isEmpty() || (hou.hasOverlapUpdate(uR).size == 1 && uR.id == hou.hasOverlapUpdate(uR).get(0).id))
        {
        }
        else {
            fail()
        }

        println("isEmpty? : " + hou.hasOverlapUpdate(uR).isEmpty())

       val uE =  Update(uR, appointmentRepo, telescopeRepo, userRoleRepo).execute()
       //
       //the actual success object is a Long, the appointment ID that was updated

       val ss =  uE.success
        //we were entering the error, which means it was supposed to be a success
       val ee = uE.error
        //ss is null, which means that success is null, so we have errors
       if (ss == null) {

           println("request id " + uR.id)
           println("size: " + hou.hasOverlapUpdate(uR).size)
         //  println("id of appointment in index 0 in table " + hou.hasOverlapUpdate(uR).get(0).id)

           for (p in ErrorTag.values())
           {

               println("empty? : " + ee?.get(p) )
           }
            fail()
        }
        else //success is not null, so we do not fail
        {
        }

    }
    @Test
    fun updateToBeforeAnAppointmentNoOverlap()
    {
      val hou = HasOverlap(appointmentRepo)
      //associated with a1
      val uR = Update.Request(a1_id, 1, Date(Date().time + 3000), Date(d.time - 10000), true)

        if (hou.hasOverlapUpdate(uR).isEmpty() || (hou.hasOverlapUpdate(uR).size == 1 && uR.id == hou.hasOverlapUpdate(uR).get(0).id))
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

    @Test
    fun rescheduleSameAppointmentOnTopOfOriginalRangeNoConflict() {
        val hou = HasOverlap(appointmentRepo)
        //normally would be a conflict, but because the only appointment in conflict is the actual one being updated, we're good
        val uR = Update.Request(a1_id, 1, d, Date(d.time + 55000), true)
        val uE =  Update(uR, appointmentRepo, telescopeRepo, userRoleRepo).execute()
        val ss =  uE.success
        val ee = uE.error
        if (ss == null)
        {
            fail()
        }

        else //success is not null (we have a success), so we do not fail (test passes)
        {
        }
    }

    @Test
    fun requestedApptExtendsOverAnAppointment()
    {

        val hou = HasOverlap(appointmentRepo)
        val uR = Update.Request(a1_id, 1, Date(d.time + 40000), Date(d.time + 70000), true)
        val uObject = Update(uR, appointmentRepo, telescopeRepo, userRoleRepo)
        val executed = uObject.execute()
        val s = executed.success
        //e should be populated
        val e: Multimap<ErrorTag, String>? = executed.error
        if (s == null)
        {
            if (hou.hasOverlapUpdate(uR).isEmpty() || (hou.hasOverlapUpdate(uR).size == 1 && uR.id == hou.hasOverlapUpdate(uR).get(0).id)) {
                fail()
            }
            println("error should be the overlap error: " + e?.get(ErrorTag.OVERLAP).toString())
            if ((e?.get(ErrorTag.OVERLAP).toString().equals("[Appointment cannot be rescheduled: Conflict with an already-scheduled appointment]"))) {
            }
            else fail()
        } else {
            fail()
        }
    }

    @Test
    fun requestedApptInsideAnAppt()
    {
        val hou = HasOverlap(appointmentRepo)
        val uR = Update.Request(a1_id, 1, Date(d.time + 70000), Date(d.time + 80000), true)
        val uObject = Update(uR, appointmentRepo, telescopeRepo, userRoleRepo)
        val executed = uObject.execute()
        val s = executed.success
        //e should be populated
        val e: Multimap<ErrorTag, String>? = executed.error
        if (s == null)
        {
            if (hou.hasOverlapUpdate(uR).isEmpty() || (hou.hasOverlapUpdate(uR).size == 1 && uR.id == hou.hasOverlapUpdate(uR).get(0).id)) {
                fail()
            }
            println("error should be the overlap error: " + e?.get(ErrorTag.OVERLAP).toString())
            if ((e?.get(ErrorTag.OVERLAP).toString().equals("[Appointment cannot be rescheduled: Conflict with an already-scheduled appointment]"))) {
            }
            else fail()
        } else {
            fail()
        }
    }
}