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

  val d =  Date()

  val dd = Date("2018-1-1")

    private var a:Appointment = Appointment(u, "appt-type1", d, dd, 2, 4, "1", true, Date(), 3, u.firstName, u.lastName, 0 )


    lateinit var factory: AppointmentFactory

    @Before
    fun init() {
        factory = BaseAppointmentFactory(apptRepo, apptInfo, userRepo)
        val apptStatus = Appointment.Status.InProgress;
        val apptStatus2 = Appointment.Status.Completed;

        val orientationId = 5;

        val uid: Long = 50
        u.id = uid


        @Query("insert into appointment(type, assocUserId, startTime, endTime, status, telescopeId, celestialBodyId, orientationId, receiver, isPublic)  values ('{a.type}', '{a.assocUserId}', '{a.d}', '{a.dd}', '{apptStatus}', '{a.telescopeId}', '{a.celestialBodyId}', '{a.orientationId}', '{a.receiver}', '{a.isPublic}')")
        queries.insertAppt()

    }




   // @Query("insert into appointment(type, assocUserId, startTime, endTime, status, telescopeId, celestialBodyId, orientationId, receiver, isPublic) values ( 'RandomDude2', 'WithAWeirdLastName2', 'piano1mano2@gmail.com', 'YCAS', '717-123-4568', 'pw2' , 0, 'apptStatus2', 60) ")


   @Test
    fun retrieveTest()
    {
        //@Query("insert into appointment(type, assocUserId, startTime, endTime, status, telescopeId, celestialBodyId, orientationId, receiver, isPublic)  values ('{a.type}', '{a.assocUserId}', '{a.d}', '{a.dd}', '{apptStatus}', '{a.telescopeId}', '{a.celestialBodyId}', '{a.orientationId}', '{a.receiver}', '{a.isPublic}')")

        //call sql insertion
        val aa:Appointment = queries.insertAppt()

        val retrieved = factory.retrieve(1)

        //Trying to figure out how to test the result of retrieve, because it returns a Command object

        //this is always true..
       // assertTrue(retrieved is Command<Long, Multimap<ErrorTag, String>>)

       // assertTrue()

    }



    interface queries
    {
        //is it going to see those values?
        @Query("insert into appointment(type, assocUserId, startTime, endTime, status, telescopeId, celestialBodyId, orientationId, receiver, isPublic)  values ('{a.type}', '{a.assocUserId}', '{a.d}', '{a.dd}', '{apptStatus}', '{a.telescopeId}', '{a.celestialBodyId}', '{a.orientationId}', '{a.receiver}', '{a.isPublic}')")
        fun insertAppt():Appointment


   //     @Query("insert into appointment(type, assocUserId, startTime, endTime, status, telescopeId, celestialBodyId, orientationId, receiver, isPublic) values ('type2', '500', ')

    }
}