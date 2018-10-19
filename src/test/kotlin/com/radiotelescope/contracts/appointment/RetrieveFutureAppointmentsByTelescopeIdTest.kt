package com.radiotelescope.contracts.appointment

import com.radiotelescope.repository.appointment.Appointment
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import com.google.common.collect.Multimap
import com.radiotelescope.TestUtil
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import java.util.*


@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescopeForRetrieveFutureAppointmentsByTelescopeIdTest.sql"])

internal class RetrieveFutureAppointmentsByTelescopeIdTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil {
            return TestUtil()
        }
    }

        @Autowired
        private lateinit var testUtil: TestUtil

        @Autowired
        private lateinit var apptRepo: IAppointmentRepository
        @Autowired
        private lateinit var teleRepo: ITelescopeRepository

        private var globalUserId:Long = 0
        private var globalApptId:Long = 0
        private var globalstartTime:Date = Date()
        @Before
        fun setUp()
        {   //persist a user
            val user = testUtil.createUser("jamoros@ycp.edu")
            //persist an appointment
            val appt = testUtil.createAppointment(user, 10, Appointment.Status.Scheduled, Date(), Date(Date().time+9999999), true)
            globalstartTime = appt.startTime
            globalApptId = appt.id
            globalUserId = user.id
        }
        @Test
        fun retrieveFutureAppointmentsByTelescopeIdTest()
        {
            println("This is user id:" + globalUserId)
            assertEquals(1, teleRepo.count())
            val page: SimpleResult<Page<AppointmentInfo>, Multimap<ErrorTag,String>> =   RetrieveFutureAppointmentsByTelescopeId(apptRepo,10 , PageRequest.of(0, 10), teleRepo).execute()
            val pageS:Page<AppointmentInfo>? = page.success
            val pageE:Multimap<ErrorTag, String>? = page.error
          if (pageS == null)
          {
              println("This is the pageE get: " + pageE?.get(ErrorTag.ID))
              for (e in ErrorTag.values())
              {
                println("error tag:" + pageE?.get(e))
              }
              fail()
          }
          else
          {
              assertTrue(pageS.first().id == globalApptId)
              assertTrue(pageS.first().startTime == globalstartTime)
              assertTrue(pageS.hasContent())
          }
          }
        }