package com.radiotelescope.contracts.appointment

import com.radiotelescope.repository.appointment.IAppointmentRepository
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.Appointment
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class CancelTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private var appointmentRequest = Create.Request(startTime = Date(Date().time+ 5000),
            endTime = Date(Date().time + 10000),
            isPublic = true,
            telescopeId = 456,
            userId = 23)

    private var appointmentRequest2 = Create.Request(startTime = Date(),
            endTime = Date(Date().time + 2500),
            isPublic = true,
            telescopeId = 512,
            userId = 54)

    @Before
    fun setUp() {
        // Persist a user
        val user = testUtil.createUser("spathcody@gmail.com")

        // TODO - Add test setup here

//do one for in-progress also
        testUtil.createAppointment(user = user,
                telescopeId = 1,
                status = Appointment.Status.Scheduled,
                startTime = appointmentRequest.startTime,
                endTime = appointmentRequest.endTime,
                isPublic = appointmentRequest.isPublic
                )


        testUtil.createAppointment(user = user,
                telescopeId = 2,
                status = Appointment.Status.InProgress,
                startTime = appointmentRequest2.startTime,
                endTime = appointmentRequest2.endTime,
                isPublic = appointmentRequest2.isPublic
        )


    }

    private var cancelObject: Cancel = Cancel(appointmentRepo.findById(456).get().id, appointmentRepo)
    private var cancelObject2: Cancel = Cancel(appointmentRepo.findById(512).get().id, appointmentRepo)

    // TODO - Add unit tests here


@Test
fun CancelExecuteTest()
{

    if (cancelObject.execute().success == null)
        fail()

    if (cancelObject2.execute().success == null)
        fail()

}

}