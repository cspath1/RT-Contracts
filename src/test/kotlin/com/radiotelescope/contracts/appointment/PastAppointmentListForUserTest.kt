package com.radiotelescope.contracts.appointment

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Page
import org.springframework.test.context.ActiveProfiles
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class PastAppointmentListForUserTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private lateinit var apptList: Page<AppointmentInfo>

    private var user_id:Long = 0

    @Before
    fun setUp() {
        // Persist a user
        val user = testUtil.createUser("spathcody@gmail.com")
        testUtil.createAppointment(
                user= user,
                telescopeId= 0,
                status= Appointment.Status.Requested,
                startTime = Date(System.currentTimeMillis() - 30000L),
                endTime = Date(System.currentTimeMillis() - 10000L),
                isPublic= true
        )
        user_id = user.id

    }

    @Test
    fun retrieveListTest()
    {
        if ( PastAppointmentListForUser(appointmentRepo, user_id, userRepo, PageRequest.of(0, 10)).execute().success  == null)
            fail()
    }

    @Test
    fun invalidUserId()
    {
        if ( PastAppointmentListForUser(appointmentRepo, -500, userRepo, PageRequest.of(0, 10)).execute().error  == null)
            fail()
    }

    @Test
    fun nonEmptyListTest()
    {
        apptList = PastAppointmentListForUser(appointmentRepo, user_id, userRepo, PageRequest.of(0, 10)).execute().success!!

        //page is nonempty
        assertTrue(apptList.totalElements > 0)
        //content of page is accurate to the appointment
        assertTrue(apptList.first().userId == user_id)

    }



}