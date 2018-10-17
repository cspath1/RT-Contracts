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

        testUtil.createAppointment(user = user,
                telescopeId = appointmentRequest.telescopeId,
                status = Appointment.Status.Scheduled,
                startTime = appointmentRequest.startTime,
                endTime = appointmentRequest.endTime,
                isPublic = appointmentRequest.isPublic
                )

        //InProgress to Canceled
        testUtil.createAppointment(user = user,
                telescopeId = appointmentRequest2.telescopeId,
                status = Appointment.Status.InProgress,
                startTime = appointmentRequest2.startTime,
                endTime = appointmentRequest2.endTime,
                isPublic = appointmentRequest2.isPublic
        )
    }


    // TODO - Add unit tests here

@Test
fun CancelExecuteTest()
{
    var cancelObject: Cancel = Cancel(appointmentRepo.findById(1).get().id, appointmentRepo)
    var cancelObject2: Cancel = Cancel(appointmentRepo.findById(2).get().id, appointmentRepo)

    if (cancelObject.execute().success == null)
        fail()

    if (cancelObject2.execute().success == null)
        fail()
}

}