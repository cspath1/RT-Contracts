package com.radiotelescope.contracts.appointment

import com.google.common.collect.HashMultimap
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
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import java.util.*


@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescopeForRetrieveByTelescopeIdTest.sql"])
internal class RetrieveByTelescopeIdTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil {
            return TestUtil()
        }
    }

    //Test to see if you can get all appointments BY telescope Id

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var telescopeRepo: ITelescopeRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private var user_id: Long = 0
    private var appt_id: Long = 0

    @Before
    fun setUp() {
        // Persist a user
        val user = testUtil.createUser("spathcody@gmail.com")
        //count tells you how many telescopes are in the Repo
        assertEquals(1, telescopeRepo.count())
        user_id = user.id


        //persist an appointment

        var appointmentRequest = Create.Request(startTime = Date(Date().time+ 5000),
                endTime = Date(Date().time + 10000),
                isPublic = true,
                telescopeId = 456,
                userId = 23)


        var appt =  testUtil.createAppointment(user = user,
                telescopeId = appointmentRequest.telescopeId,
                status = Appointment.Status.Scheduled,
                startTime = appointmentRequest.startTime,
                endTime = appointmentRequest.endTime,
                isPublic = appointmentRequest.isPublic)


      appt_id = appt.id
    }

    @Test
    fun testIfAppointmentIsInDB()
    {
       assertEquals(1, appointmentRepo.count())
    }


    @Test
    fun retrieveByTelescopeIdTest() {

        var telescope = telescopeRepo.findById(2)
        if (RetrieveByTelescopeId(appointmentRepo, telescope.get().getId(), PageRequest.of(0, 10), telescopeRepo).execute().success == null)
            //What I could do even further is test to make sure I can get the actual appointment details in Kotlin
            fail()

        else //if success
        {
            println("Appointment ID is:" + appt_id)
        }

    }

    @Test
    fun invalidTelescopeId()
    {
       if (RetrieveByTelescopeId(appointmentRepo, -600, PageRequest.of(0, 10), telescopeRepo).execute().error == null)
           fail()
    }

}