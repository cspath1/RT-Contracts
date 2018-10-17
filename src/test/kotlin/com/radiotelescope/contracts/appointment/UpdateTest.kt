package com.radiotelescope.contracts.appointment

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class UpdateTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private val createReq = Create.Request(
            startTime=Date(),
            endTime = Date(Date().time+100000),
            isPublic = true,
            telescopeId = 1,
            userId = 50
    )

    @Before
    fun setUp() {
        // Persist a user
        val user = testUtil.createUser("spathcody@gmail.com")

        val appointment = testUtil.createAppointment(user = user, telescopeId = createReq.telescopeId, status = Appointment.Status.Scheduled, startTime = createReq.startTime, endTime = createReq.endTime, isPublic = createReq.isPublic)
    }

    @Test
    fun updatetest()
    {
        //Is the id going to be 1?
        if (Update(appointmentRepo.findById(1).get().id,  appointmentRepo, Date(Date().time+500000), Date(Date().time + 700000 )).execute().success == null)
            fail()
    }
}