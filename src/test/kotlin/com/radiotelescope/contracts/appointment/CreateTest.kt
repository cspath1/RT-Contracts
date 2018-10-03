package com.radiotelescope.contracts.appointment

import com.example.project.contracts.appointment.BaseAppointmentFactory
import com.google.common.collect.HashMultimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.appointment.Appointment


import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import com.radiotelescope.contracts.appointment.Create.Request
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.Query
import org.springframework.test.context.junit4.SpringRunner
import java.util.*
import com.radiotelescope.contracts.appointment.ErrorTag
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult

@DataJpaTest
@RunWith(SpringRunner::class)
internal class CreateTest
{

    private lateinit var apptRepo: IAppointmentRepository
    private lateinit var appt: Appointment
    var u:User= User("Someone", "LastName123", "piano1mano@gmail.com","123456" )
    var request:Request  = Create.Request(u, "type1", Date(), Date("2018-7-7"), 1, 2, "1", true, Date(), 3, "John", "Doe", 1, Appointment.Status.InProgress, 1 )
    var Createt:Create = Create(request, apptRepo)

    @Before
    fun init() {


    }


    @Test
    fun CreateTest()
    {
      var errors = HashMultimap.create<ErrorTag,String>()

     var s: SimpleResult<Long, Multimap<ErrorTag, String>> =  SimpleResult(null, errors)
        s =  Createt.execute()

      //  if (!errors.isEmpty)


        //pass if errors Multimap remains empty
          if ( errors.isEmpty() )
          {

          }
        //fail if otherwise
        else assertTrue(false)


    }




}