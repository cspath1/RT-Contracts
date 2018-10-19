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

    private var appointmentRequest = Create.Request(startTime = Date(Date().time + 5000),
            endTime = Date(Date().time + 10000),
            isPublic = true,
            telescopeId = 456,
            userId = 23)

    private var appointmentRequest2 = Create.Request(startTime = Date(),
            endTime = Date(Date().time + 2500),
            isPublic = true,
            telescopeId = 512,
            userId = 54)


    private var appointmentRequest3 = Create.Request(startTime = Date(Date().time + 12500) ,
            endTime = Date(Date().time + 15000),
            isPublic = true,
            telescopeId = 513,
            userId = 55)

    private var appointmentRequest4 = Create.Request(startTime = Date(Date().time + 16000) ,
            endTime = Date(Date().time + 17000),
            isPublic = true,
            telescopeId = 514,
            userId = 56)

    var globalId1:Long = 0
    var globalId2:Long = 0
    var globalId3:Long = 0
    var globalId4:Long = 0

    @Before
    fun setUp() {
        // Persist a user
        val user = testUtil.createUser("spathcody@gmail.com")

        //Scheduled to Canceled
      var appt1 =  testUtil.createAppointment(user = user,
                telescopeId = appointmentRequest.telescopeId,
                status = Appointment.Status.Scheduled,
                startTime = appointmentRequest.startTime,
                endTime = appointmentRequest.endTime,
                isPublic = appointmentRequest.isPublic
                )

        //InProgress to Canceled
      var appt2 =  testUtil.createAppointment(user = user,
                telescopeId = appointmentRequest2.telescopeId,
                status = Appointment.Status.InProgress,
                startTime = appointmentRequest2.startTime,
                endTime = appointmentRequest2.endTime,
                isPublic = appointmentRequest2.isPublic
        )
        //Should result in error: Canceled to Canceled
     var appt3 =   testUtil.createAppointment(user = user,
                telescopeId = appointmentRequest3.telescopeId,
                status = Appointment.Status.Canceled,
                startTime = appointmentRequest3.startTime,
                endTime = appointmentRequest3.endTime,
                isPublic = appointmentRequest3.isPublic)

        var appt4 =   testUtil.createAppointment(user = user,
                telescopeId = appointmentRequest4.telescopeId,
                status = Appointment.Status.Completed,
                startTime = appointmentRequest4.startTime,
                endTime = appointmentRequest4.endTime,
                isPublic = appointmentRequest4.isPublic)

        globalId1 = appt1.id
        globalId2 = appt2.id
        globalId3 = appt3.id
        globalId4 = appt4.id
    }

@Test
fun CancelExecuteTest()
{
    var cancelObject: Cancel = Cancel(globalId1, appointmentRepo)
    var cancelObject2: Cancel = Cancel(globalId2, appointmentRepo)

    if (cancelObject.execute().success == null)
        fail()

    if (cancelObject2.execute().success == null)
        fail()
}

@Test
//an attempt to Cancel an already Canceled appointment should result in an error-- see Cancel.kt
fun CanceledToCanceled()
{
    val cancelObject3: Cancel = Cancel(globalId3, appointmentRepo)

    //to fail, error should be null, because in this test case, upon execute(), error will be populated
    if (cancelObject3.execute().error == null)
        fail()

}
    @Test
//an attempt to Cancel an already Canceled appointment should result in an error-- see Cancel.kt
    fun CompletedToCanceled()
    {
        val cancelObject4: Cancel = Cancel(globalId4, appointmentRepo)

        //to fail, error should be null, because in this test case, upon execute(), error will be populated
        if (cancelObject4.execute().error == null)
            fail()
    }

@Test
//should cause an error, so fail if error is null
fun NonexistentAppointmentId()
{
    val cancelObject5: Cancel = Cancel(-500, appointmentRepo)
    if (cancelObject5.execute().error == null)
        fail()
}
}