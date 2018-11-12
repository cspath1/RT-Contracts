package com.radiotelescope.repository.viewer

import com.radiotelescope.TestUtil
import com.radiotelescope.contracts.viewer.*
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.role.UserRole
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
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner
import java.util.*


@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
internal class ViewerTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil {
            return TestUtil()
        }

        @Bean
        fun liquibase(): SpringLiquibase {
            val liquibase = SpringLiquibase()
            liquibase.setShouldRun(false)
            return liquibase
        }
    }


    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var viewerRepo: IViewerRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    var u1_id:Long = -1
    var u2_id:Long = -1
    var a1:Appointment = Appointment(Date(), Date(Date().time + 1000), 1L, false)

    @Before
    fun setUp()
    {
        //u1 shares, u2 is viewer
        val u1: User = testUtil.createUser("123@crimescene.com")
        val u2: User = testUtil.createUser("illustrativepurposes@poetical.com")
        u1_id = u1.id
        u2_id = u2.id
        testUtil.createUserRolesForUser(u1.id, UserRole.Role.RESEARCHER, true)
        testUtil.createUserRolesForUser(u2.id, UserRole.Role.RESEARCHER, true)

        val a = testUtil.createAppointment(u1, 1L, Appointment.Status.COMPLETED, Date(), Date(Date().time + 1000), false)
        a1 = a


    }
    @Test
    fun testGetViewers()
    {
        //first add the viewer
        val request = Create.Request(u2_id, u1_id, a1)

        val cObject = Create(request, viewerRepo, userRepo)
        val executed = cObject.execute()

        if (executed.success != null)//success, as it should be
        {
            assert(executed.success == u1_id)
        }

        else fail()
        //Now viewers table should be populated
       val retrieved = RetrieveViewersByUserId(viewerRepo, userRepo, request, PageRequest.of(0,5)).execute()
       val mutableListAppointments = retrieved.success



        //should now contain the appointment which can be viewed by u1
        //yup it's null
        println("mutableListAppointments: " + mutableListAppointments)
        //size itself is null
        println("size of mutableListAppointments: " + mutableListAppointments?.size)
        //success is indeed equal to null, so there are errors
        //is it possible it's actually returning a null mutable list for some reason?
        if (mutableListAppointments == null) {


            val ee = retrieved.error

            for (v in ErrorTag.values())
            {
                println(ee?.get(v))
            }

            fail()
        }
        if (mutableListAppointments!!.isEmpty())
        {
            fail()
        }
        else
        {
            //ensure details are the same
            for (appt in mutableListAppointments)
            {
                assert(appt.id == a1.id)
                assert(appt.startTime == a1.startTime)
                assert(appt.isPublic == a1.isPublic)
            }
        }

    }


    @Test
    fun getViewerByAppointmentId()
    {
      //  viewerRepo.getViewersByAppointmentId(a1.id, pageable = ).execute()

        val result = RetrieveViewersByAppointmentId(appointmentRepo, viewerRepo, a1.id, PageRequest.of(0,5)).execute()
        val suc = result.success
        val err = result.error

        //error case
        if (suc == null)
        {
            fail()
        }


            //success case
            if (err == null)
            {

            }

    }
}