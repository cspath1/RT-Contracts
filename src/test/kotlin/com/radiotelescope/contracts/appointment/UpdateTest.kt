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

    private val updateReq = Update.Request(
            id = 1,
            telescope_id = 1,
            startTime=Date(),
            endTime = Date(Date().time+100000)
    )

    private val updateReq2 = Update.Request(
            id = 2,
            startTime=Date(),
            endTime = Date(Date().time+50),
            telescope_id = 2
    )

    private val updateReq3 = Update.Request(
            id = 3,
            startTime=Date(Date().time+51),
            endTime = Date(Date().time+100),
            telescope_id = 3

    )

    private val updateReq4 = Update.Request(
            id = 4,
            startTime=Date(Date().time+101),
            endTime = Date(Date().time+150),
            telescope_id = 4
    )

    private val updateReq5 = Update.Request(
            id = 5,
            startTime=Date(Date().time+151),
            endTime = Date(Date().time+200),
            telescope_id = 5
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
        val appointment = testUtil.createAppointment(user = user, telescopeId = updateReq.telescope_id, status = Appointment.Status.Scheduled, startTime = updateReq.startTime, endTime = updateReq.endTime, isPublic = true)
        val appointment2 = testUtil.createAppointment(user = user, telescopeId = updateReq2.telescope_id, status = Appointment.Status.Scheduled, startTime = updateReq2.startTime, endTime = updateReq2.endTime, isPublic = true)
        val appointment3 = testUtil.createAppointment(user = user, telescopeId = updateReq3.telescope_id, status = Appointment.Status.Scheduled, startTime = updateReq3.startTime, endTime = updateReq3.endTime, isPublic = true)

        val appointment4 = testUtil.createAppointment(user = user, telescopeId = updateReq4.telescope_id, status = Appointment.Status.Scheduled, startTime = updateReq4.startTime, endTime = updateReq4.endTime, isPublic = true)
        val appointment5 = testUtil.createAppointment(user = user, telescopeId = updateReq5.telescope_id, status = Appointment.Status.Scheduled, startTime = updateReq5.startTime, endTime = updateReq5.endTime, isPublic = true)

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
        if (Update(globalApptId1,  appointmentRepo, Date(Date().time+500000), Date(Date().time + 700000 ), updateReq.telescope_id).execute().success == null)
            fail()
    }

    @Test
    fun invalidAppointmentId()
    {
        if (Update(-500,  appointmentRepo, Date(Date().time+500000), Date(Date().time + 700000 ), 100).execute().error == null)
            fail()
    }

    @Test
    fun UpdateToNoChange()
    {

        if (Update(globalApptId2,  appointmentRepo, Date(), Date(Date().time+50), updateReq2.telescope_id).execute().error == null)
            fail()
    }

    @Test
    fun StartTimeGreaterThanEndTime()
    {
        if (Update(globalApptId3,  appointmentRepo, Date( Date().time+50 ), Date(Date().time+ 25), updateReq3.telescope_id ).execute().error == null)
            fail()
    }

    @Test
    fun startTimeInPast()
    {
        if (Update(globalApptId4, appointmentRepo, Date(Date().time - 10000), Date() , updateReq4.telescope_id ).execute().error == null)
        {
        fail()
        }
    }

    @Test
    fun endTimeInPast()
    {
        if (Update(globalApptId5, appointmentRepo, Date(Date().time - 10000), Date(Date().time - 5000) , updateReq5.telescope_id ).execute().error == null)
        {
            fail()
        }
    }
}