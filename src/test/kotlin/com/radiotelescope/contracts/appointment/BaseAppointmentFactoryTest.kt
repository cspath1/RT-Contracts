package com.radiotelescope.contracts.appointment


import com.example.project.contracts.appointment.BaseAppointmentFactory
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.appointment.Appointment

import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.Query
import org.springframework.test.context.junit4.SpringRunner
import java.util.*


@DataJpaTest
@RunWith(SpringRunner::class)
internal class BaseAppointmentFactoryTest
{
    @Autowired
    private lateinit var apptRepo: IAppointmentRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    private lateinit var apptInfo: AppointmentInfo

    private var u:User = User("Someone", "LastName123", "piano1mano@gmail.com","123456" )

    val d =  Date(99990)

    val dd = Date(99999)

    private var a:Appointment = Appointment(u, "appt-type1", d, dd, 2, 4, "1", true, Date(), 3, u.firstName, u.lastName, 0 )

    private val baseRequest = Create.Request(
            user = u,
            type = "appt-type1",
            startTime = d,
            endTime = dd,
            telescopeId = 2,
            celestialBodyId = 4,
            receiver = "1",
            isPublic = true,
            date = d,
            assocUserId = 3,
            uFirstName = u.firstName,
            uLastName =  u.lastName,
            state = 1,
            apptId = 10,
            status = Appointment.Status.InProgress
    )

    lateinit var factory: AppointmentFactory

    @Before
    fun init() {
        factory = BaseAppointmentFactory(apptRepo, apptInfo, userRepo)
        //val apptStatus = Appointment.Status.InProgress
       // val apptStatus2 = Appointment.Status.Completed

        //val orientationId = 5;

       // val uid: Long = 50
       // u.id = uid

        factory.create(baseRequest)


    }

   @Test
    fun retrieveTest()
    {


    }

    @Test
    fun createTest(){
        val cmd = factory.create(baseRequest)

        assertTrue(cmd is Create)

    }

    @Test
    fun deleteTest(){

    }

    @Test
    fun retrieveListTest(){

    }

}
