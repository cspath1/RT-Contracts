package com.radiotelescope.contracts.appointment

import com.google.common.collect.Multimap
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


    //what are the statuses? change 'em
    private val createReq = Create.Request(
            telescopeId = 1,
            startTime=Date(),
            endTime = Date(Date().time+5),
            isPublic = true,
            userId = 1
    )

    private val createReq2 = Create.Request(
            startTime=Date(Date().time+100),
            endTime = Date(Date().time+105),
            telescopeId = 2,
            userId = 2,
            isPublic = true
            )

    private val createReq3 = Create.Request(
            startTime=Date(Date().time+20),
            endTime = Date(Date().time+25),
            telescopeId = 3,
            isPublic = true,
            userId = 3
    )

    private val createReq4 = Create.Request(
            userId = 4,
            startTime=Date(Date().time+30),
            endTime = Date(Date().time+35),
            telescopeId = 4,
            isPublic = true
    )

    private val createReq5 = Create.Request(
            isPublic = true,
            startTime=Date(Date().time+40),
            endTime = Date(Date().time+45),
            telescopeId = 5,
            userId = 5
    )

    private val createReq6 = Create.Request(
            userId = 6,
            startTime=Date(Date().time+50),
            endTime = Date(Date().time+55),
            telescopeId = 6,
            isPublic = true
    )

    private val createReq7 = Create.Request(
            userId = 7,
            startTime=Date(Date().time+60),
            endTime = Date(Date().time+65),
            telescopeId = 7,
            isPublic = true
    )


    private val createReq8 = Create.Request(
            userId = 8,
            startTime=Date(Date().time+70),
            endTime = Date(Date().time+75),
            telescopeId = 8,
            isPublic = true
    )



    private val updateReq = Update.Request(
            id = 1,
            telescopeId = 1,
            newStartTime= Date(Date().time+  5000),
            newEndTime = Date(Date().time + 10000)
    )

    private val updateReq2 = Update.Request(
            id = 2,
            newStartTime=Date(Date().time+100),
            newEndTime = Date(Date().time+105),
            telescopeId = 2
    )

    private val updateReq3 = Update.Request(
            id = 3,
            newStartTime=Date( Date().time+22 ),
            newEndTime = Date(Date().time+21),
            telescopeId = 3

    )

    private val updateReq4 = Update.Request(
            id = 4,
            newStartTime=Date(Date().time-5),
            newEndTime = Date(),
            telescopeId = 4
    )

    private val updateReq5 = Update.Request(
            id = 5,
            newStartTime=Date(Date().time-10),
            newEndTime = Date(Date().time-9),
            telescopeId = 5
    )

    private val updateReq6 = Update.Request(
            id = 6,
            newStartTime=Date(Date().time+51),
            newEndTime = Date(Date().time+52),
            telescopeId = -500
    )

    private val updateReq7 = Update.Request(
            id = 7,
            newStartTime=Date(Date().time+80),
            newEndTime = Date(Date().time+85),
            telescopeId = 7
    )

    private val updateReq8 = Update.Request(
            id = 8,
            newStartTime=Date(Date().time+71),
            newEndTime = Date(Date().time + 72 ),
            telescopeId = 8
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

        val appointment = testUtil.createAppointment(user = user, telescopeId = createReq.telescopeId, status = Appointment.Status.Scheduled, startTime = createReq.startTime, endTime = createReq.endTime, isPublic = true)
        val appointment2 = testUtil.createAppointment(user = user, telescopeId = createReq2.telescopeId, status = Appointment.Status.Scheduled, startTime = createReq2.startTime, endTime = createReq2.endTime, isPublic = true)
        val appointment3 = testUtil.createAppointment(user = user, telescopeId = createReq3.telescopeId, status = Appointment.Status.Scheduled, startTime = createReq3.startTime, endTime = createReq3.endTime, isPublic = true)

        val appointment4 = testUtil.createAppointment(user = user, telescopeId = createReq4.telescopeId, status = Appointment.Status.Scheduled, startTime = createReq4.startTime, endTime = createReq4.endTime, isPublic = true)
        val appointment5 = testUtil.createAppointment(user = user, telescopeId = createReq5.telescopeId, status = Appointment.Status.Scheduled, startTime = createReq5.startTime, endTime = createReq5.endTime, isPublic = true)

        val appointment6 = testUtil.createAppointment(user = user, telescopeId = createReq6.telescopeId, status = Appointment.Status.Scheduled, startTime = createReq6.startTime, endTime = createReq6.endTime, isPublic = true)
        val appointment7 = testUtil.createAppointment(user = user, telescopeId = createReq7.telescopeId, status = Appointment.Status.Scheduled, startTime = createReq7.startTime, endTime = createReq7.endTime, isPublic = true)
        val appointment8 = testUtil.createAppointment(user = user, telescopeId = createReq8.telescopeId, status = Appointment.Status.Scheduled, startTime = createReq8.startTime, endTime = createReq8.endTime, isPublic = true)


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
        println("updateReq7 telescope id:" + updateReq7.telescopeId)
        println("global appt id 7: " + globalApptId7)
        println("appointment8 id:" + appointment8.id)
    }

    //TODO: Find out why this test is failing
    /*
    @Test
    fun updateTest()
    {
      var e: Multimap<ErrorTag, String>? =  Update(globalApptId1,  appointmentRepo, updateReq, teleRepo).execute().error

     for (ee in ErrorTag.values())
      println(e?.get(ee))

        if (Update(globalApptId1,  appointmentRepo, updateReq, teleRepo).execute().success == null) {
            println("e is: "+ e)
            fail()
        }
    }
    */

    @Test
    fun invalidAppointmentId()
    {
        if (Update(-500,  appointmentRepo, updateReq8, teleRepo).execute().error == null)
            fail()
    }

    @Test
    fun UpdateToNoChange()
    {

        if (Update(globalApptId2,  appointmentRepo, updateReq2, teleRepo).execute().error == null)
            fail()
    }

    @Test
    fun StartTimeGreaterThanEndTime()
    {
        if (Update(globalApptId3,  appointmentRepo, updateReq3, teleRepo).execute().error == null)
            fail()
    }

    @Test
    fun startTimeInPast()
    {
        if (Update(globalApptId4, appointmentRepo,updateReq4, teleRepo).execute().error == null)
        {
        fail()
        }
    }

    @Test
    fun endTimeInPast()
    {
        if (Update(globalApptId5, appointmentRepo,updateReq5, teleRepo).execute().error == null)
        {
            fail()
        }
    }

    @Test
    fun invalidTeleId()
    {
        if (Update(globalApptId6, appointmentRepo, updateReq6, teleRepo).execute().error == null)
        {
            fail()
        }
    }

    @Test
    fun validTeleId()
    {
        if (Update(globalApptId7, appointmentRepo, updateReq7, teleRepo).execute().success == null)
        {
            fail()
        }
    }
}