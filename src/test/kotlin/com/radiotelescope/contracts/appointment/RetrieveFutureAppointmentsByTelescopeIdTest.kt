package com.radiotelescope.contracts.appointment


import com.google.common.collect.HashMultimap
import com.google.common.collect.Iterables
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
import com.radiotelescope.repository.telescope.Telescope
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
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
    private lateinit var userRepo: IUserRepository

        @Autowired
       private lateinit var teleRepo: ITelescopeRepository


        private var globalUserId:Long = 0
      private var globalApptId:Long = 0


        @Before
        fun setUp()
        {

            //persist a user

           var user = testUtil.createUser("jamoros@ycp.edu")
            //persist an appointment

           var appt = testUtil.createAppointment(user, 1, Appointment.Status.Scheduled, Date(), Date(Date().time+9999999), true)


            globalUserId = user.id
            globalApptId = appt.id

        }


//teleRepo.findById(10).get().getId() -- why doesn't it like 'get' ?
        @Test
        fun RetrieveFutureAppointmentsByTelescopeIdTest()
        {
            println("This is user id:" + globalUserId)
            assertEquals(1, teleRepo.count())

         var page: SimpleResult<Page<AppointmentInfo>, Multimap<ErrorTag,String>> =   RetrieveFutureAppointmentsByTelescopeId(apptRepo,10 , PageRequest.of(0, 10), userRepo, globalUserId, teleRepo).execute()
            var pageS = page.success
            var pageE:Multimap<ErrorTag, String>? = page.error
            //could actually test the success parameter itself
          if ( pageS == null)
              //so pageS is null which means pageE should be not null, so pageE should have something
              //it is entering here
              //figure this out
          {  //   println(Iterables.get(pageE.get(ErrorTag.ID), 0)  )
              println("This is the pageE get: " + pageE?.get(ErrorTag.ID))

              for ( e in ErrorTag.values())
              {

                  println("This is another possible value in the pageE multimap")
                  println(
                  pageE?.get(e));
//What I'm getting printed out is: [Telescope id 10 not found]


            //      PageRequest.of()
              }


              //      println(pageE?[ErrorTag.ID])
              fail()
          }
              else
          {

             println(pageS.totalElements)



              println("size of pageS, as an Int:" + pageS.size)

              println(pageS.content)

              println("globalApptId is:" + globalApptId)
              println("Is the above appt Id the same as the below? If so, we have a success")
   //        println(   pageS.content[0].id)

            //  pageS.
//pageS.
         //     for (a:AppointmentInfo in pageS.first())
           //   {
              //    println("pageS.first() id is: " + pageS.first().id)

              assertTrue(pageS.first().id == globalApptId)
            //  }


              if (pageS.hasContent())
              {
                  println("pageS has content")
              }

              else println("pageS does not have content")


              if(pageE == null)
              {
                  println("pageE is null")
              }
              else println("pageE is not null")



          }
        }





    }