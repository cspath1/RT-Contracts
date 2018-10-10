package com.radiotelescope.contracts.appointment

import com.google.common.collect.HashMultimap
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import com.radiotelescope.contracts.appointment.Create.Request
import org.junit.runner.RunWith
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import java.util.*
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import org.springframework.beans.factory.annotation.Autowired

@DataJpaTest
@RunWith(SpringRunner::class)
internal class CreateTest
{
    @Autowired
    private lateinit var apptRepo: IAppointmentRepository
    var u:User= User("Someone", "LastName123", "piano1mano@gmail.com","123456" )
    var request:Request  = Create.Request(u, "type1", Date(), Date("2019-7-7"), 1, 2, "1", true, 500, u.firstName, u.lastName, 500,  Appointment.Status.InProgress, 1 )
    var CreateObj:Create = Create(request, apptRepo)

    @Before
    fun init() {
    }

    @Test
    fun CreateTest()
    {
        var errors = HashMultimap.create<ErrorTag,String>()
        var s: SimpleResult<Long, Multimap<ErrorTag, String>> =  SimpleResult(null, errors)
        s =  CreateObj.execute()
        //fail
        if (s.success == null)
            return assertTrue(false)
        //else pass
    }
}