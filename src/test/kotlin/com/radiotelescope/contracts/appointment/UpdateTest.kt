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
import com.radiotelescope.repository.telescope.ITelescopeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescopeForUpdateTest.sql"])
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

    @Autowired
    private lateinit var teleRepo: ITelescopeRepository

    private val updateReq = Update.Request(
            id = 1,
            telescope_id = 1,
            startTime=Date(),
            endTime = Date(Date().time+5)
    )

    private val updateReq2 = Update.Request(
            id = 2,
            startTime=Date(Date().time+100),
            endTime = Date(Date().time+105),
            telescope_id = 2
    )

    private val updateReq3 = Update.Request(
            id = 3,
            startTime=Date(Date().time+20),
            endTime = Date(Date().time+25),
            telescope_id = 3

    )

    private val updateReq4 = Update.Request(
            id = 4,
            startTime=Date(Date().time+30),
            endTime = Date(Date().time+35),
            telescope_id = 4
    )

    private val updateReq5 = Update.Request(
            id = 5,
            startTime=Date(Date().time+40),
            endTime = Date(Date().time+45),
            telescope_id = 5
    )

    private val updateReq6 = Update.Request(
            id = 6,
            startTime=Date(Date().time+50),
            endTime = Date(Date().time+55),
            telescope_id = 6
    )

    private val updateReq7 = Update.Request(
            id = 7,
            startTime=Date(Date().time+60),
            endTime = Date(Date().time+65),
            telescope_id = 7
    )

    private val updateReq8 = Update.Request(
            id = 8,
            startTime=Date(Date().time+70),
            endTime = Date(Date().time+75),
            telescope_id = 8
    )

    private var globalApptId1:Long = 0
    private var globalApptId2:Long = 0
    private var globalApptId3:Long = 0
    private var globalApptId4:Long = 0
    private var globalApptId5:Long = 0
    private var globalApptId6:Long = 0
    private var globalApptId7:Long = 0
    private var globalApptId8:Long = 0


    @Before
    fun setUp() {

        assertEquals(8, teleRepo.count())
        // Persist a user
        val user = testUtil.createUser("spathcody@gmail.com")
        //Persist appointments
        val appointment = testUtil.createAppointment(user = user, telescopeId = updateReq.telescope_id, status = Appointment.Status.Scheduled, startTime = updateReq.startTime, endTime = updateReq.endTime, isPublic = true)
        val appointment2 = testUtil.createAppointment(user = user, telescopeId = updateReq2.telescope_id, status = Appointment.Status.Scheduled, startTime = updateReq2.startTime, endTime = updateReq2.endTime, isPublic = true)
        val appointment3 = testUtil.createAppointment(user = user, telescopeId = updateReq3.telescope_id, status = Appointment.Status.Scheduled, startTime = updateReq3.startTime, endTime = updateReq3.endTime, isPublic = true)

        val appointment4 = testUtil.createAppointment(user = user, telescopeId = updateReq4.telescope_id, status = Appointment.Status.Scheduled, startTime = updateReq4.startTime, endTime = updateReq4.endTime, isPublic = true)
        val appointment5 = testUtil.createAppointment(user = user, telescopeId = updateReq5.telescope_id, status = Appointment.Status.Scheduled, startTime = updateReq5.startTime, endTime = updateReq5.endTime, isPublic = true)

        val appointment6 = testUtil.createAppointment(user = user, telescopeId = updateReq6.telescope_id, status = Appointment.Status.Scheduled, startTime = updateReq6.startTime, endTime = updateReq6.endTime, isPublic = true)
        val appointment7 = testUtil.createAppointment(user = user, telescopeId = updateReq7.telescope_id, status = Appointment.Status.Scheduled, startTime = updateReq7.startTime, endTime = updateReq7.endTime, isPublic = true)
        val appointment8 = testUtil.createAppointment(user = user, telescopeId = updateReq8.telescope_id, status = Appointment.Status.Scheduled, startTime = updateReq8.startTime, endTime = updateReq8.endTime, isPublic = true)


        globalApptId1 = appointment.id
        globalApptId2 = appointment2.id
        globalApptId3 = appointment3.id
        globalApptId4 = appointment4.id
        globalApptId5 = appointment5.id
        globalApptId6 = appointment6.id
        globalApptId7 = appointment7.id
        globalApptId8 = appointment8.id

        println("appointment id:" + appointment.id)
        println("appointment2 id:" + appointment2.id)
        println("appointment3 id:" + appointment3.id)
        println("appointment4 id:" + appointment4.id)
        println("appointment5 id:" + appointment5.id)
        println("appointment6 id:" + appointment6.id)
        println("appointment7 id:" + appointment7.id)
        println("updateReq7 telescope id:" + updateReq7.telescope_id)
        println("global appt id 7:" + globalApptId7)


        println("appointment8 id:" + appointment8.id)


    }

    @Test
    fun updateTest()
    {
        if (Update(globalApptId1,  appointmentRepo, Date(Date().time+11), Date(Date().time + 12), updateReq.telescope_id, teleRepo).execute().success == null)
            fail()
    }

    @Test
    fun invalidAppointmentId()
    {
        if (Update(-500,  appointmentRepo, Date(Date().time+71), Date(Date().time + 72 ), 100, teleRepo).execute().error == null)
            fail()
    }

    @Test
    fun UpdateToNoChange()
    {

        if (Update(globalApptId2,  appointmentRepo, updateReq2.startTime, updateReq2.endTime, updateReq2.telescope_id, teleRepo).execute().error == null)
            fail()
    }

    @Test
    fun StartTimeGreaterThanEndTime()
    {
        if (Update(globalApptId3,  appointmentRepo, Date( Date().time+22 ), Date(Date().time+ 21), updateReq3.telescope_id , teleRepo).execute().error == null)
            fail()
    }

    @Test
    fun startTimeInPast()
    {
        if (Update(globalApptId4, appointmentRepo, Date(Date().time - 5), Date() , updateReq4.telescope_id, teleRepo ).execute().error == null)
        {
        fail()
        }
    }

    @Test
    fun endTimeInPast()
    {
        if (Update(globalApptId5, appointmentRepo, Date(Date().time - 10), Date(Date().time - 9) , updateReq5.telescope_id, teleRepo ).execute().error == null)
        {
            fail()
        }
    }

    @Test
    fun invalidTeleId()
    {
        if (Update(globalApptId6, appointmentRepo, Date(Date().time + 51), Date(Date().time +52) , -400, teleRepo ).execute().error == null)
        {
            fail()
        }
    }

    @Test
    fun validTeleId()
    {
        if (Update(globalApptId7, appointmentRepo, Date(Date().time + 80), Date(Date().time + 85) ,4, teleRepo).execute().success == null)
        {
            fail()
        }
    }
}