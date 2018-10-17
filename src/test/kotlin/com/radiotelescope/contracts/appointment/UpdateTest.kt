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

    private val createReq2 = Create.Request(
            startTime=Date(),
            endTime = Date(Date().time+50),
            isPublic = true,
            telescopeId = 2,
            userId = 51
    )

    private val createReq3 = Create.Request(
            startTime=Date(Date().time+51),
            endTime = Date(Date().time+100),
            isPublic = true,
            telescopeId = 3,
            userId = 52
    )

    private val createReq4 = Create.Request(
            startTime=Date(Date().time+101),
            endTime = Date(Date().time+150),
            isPublic = true,
            telescopeId = 4,
            userId = 53
    )

    private val createReq5 = Create.Request(
            startTime=Date(Date().time+151),
            endTime = Date(Date().time+200),
            isPublic = true,
            telescopeId = 4,
            userId = 53
    )

    private var globalApptId1:Long = 0
    private var globalApptId2:Long = 0
    private var globalApptId3:Long = 0
    private var globalApptId4:Long = 0
    private var globalApptId5:Long = 0


    @Before
    fun setUp() {
        // Persist a user
        val user = testUtil.createUser("spathcody@gmail.com")
        //Persist appointments
        val appointment = testUtil.createAppointment(user = user, telescopeId = createReq.telescopeId, status = Appointment.Status.Scheduled, startTime = createReq.startTime, endTime = createReq.endTime, isPublic = createReq.isPublic)
        val appointment2 = testUtil.createAppointment(user = user, telescopeId = createReq2.telescopeId, status = Appointment.Status.Scheduled, startTime = createReq2.startTime, endTime = createReq2.endTime, isPublic = createReq2.isPublic)
        val appointment3 = testUtil.createAppointment(user = user, telescopeId = createReq3.telescopeId, status = Appointment.Status.Scheduled, startTime = createReq3.startTime, endTime = createReq3.endTime, isPublic = createReq3.isPublic)

        val appointment4 = testUtil.createAppointment(user = user, telescopeId = createReq4.telescopeId, status = Appointment.Status.Scheduled, startTime = createReq4.startTime, endTime = createReq4.endTime, isPublic = createReq4.isPublic)
        val appointment5 = testUtil.createAppointment(user = user, telescopeId = createReq5.telescopeId, status = Appointment.Status.Scheduled, startTime = createReq5.startTime, endTime = createReq5.endTime, isPublic = createReq5.isPublic)

        globalApptId1 = appointment.id
        globalApptId2 = appointment2.id
        globalApptId3 = appointment3.id
        globalApptId4 = appointment4.id
        globalApptId5 = appointment5.id

        println("appointment id:" + appointment.id)
        println("appointment2 id:" + appointment2.id)
        println("appointment3 id:" + appointment3.id)
        println("appointment4 id:" + appointment4.id)
        println("appointment5 id:" + appointment5.id)

    }

    @Test
    fun updateTest()
    {
        if (Update(globalApptId1,  appointmentRepo, Date(Date().time+500000), Date(Date().time + 700000 )).execute().success == null)
            fail()
    }

    @Test
    fun invalidAppointmentId()
    {
        if (Update(-500,  appointmentRepo, Date(Date().time+500000), Date(Date().time + 700000 )).execute().error == null)
            fail()
    }

    @Test
    fun UpdateToNoChange()
    {

        if (Update(globalApptId2,  appointmentRepo, Date(), Date(Date().time+50)).execute().error == null)
            fail()
    }

    @Test
    fun StartTimeGreaterThanEndTime()
    {
        if (Update(globalApptId3,  appointmentRepo, Date( Date().time+50 ), Date(Date().time+ 25) ).execute().error == null)
            fail()
    }

    @Test
    fun startTimeInPast()
    {
        if (Update(globalApptId4, appointmentRepo, Date(Date().time - 10000), Date()  ).execute().error == null)
        {
fail()
        }
    }

    @Test
    fun endTimeInPast()
    {
        if (Update(globalApptId5, appointmentRepo, Date(Date().time - 10000), Date(Date().time - 5000)  ).execute().error == null)
        {
            fail()
        }
    }
}