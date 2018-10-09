package com.radiotelescope.contracts.appointment

import com.google.common.collect.HashMultimap
import com.radiotelescope.repository.appointment.Appointment
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import java.util.*
import org.springframework.beans.factory.annotation.Autowired


@DataJpaTest
@RunWith(SpringRunner::class)

internal class RetrieveListTest
{
    private var u: User = User("Someone", "LastName123", "piano1mano@gmail.com","123456" )
    val d =  Date()
    val dd = Date("2018-1-1")

    private var a:Appointment = Appointment(u, "appt-type1", d, dd, 2, 4, "1", true, Date(), 3, u.firstName, u.lastName, 0 )

    @Autowired
    private lateinit var uRepo:IUserRepository
    private lateinit var aRepo:IAppointmentRepository

   private var rL:RetrieveList = RetrieveList(aRepo, u.id, uRepo)

    @Test
    fun RetrieveTest()
    {
        var errors = HashMultimap.create<ErrorTag,String>()
        var s: SimpleResult<Long, Multimap<ErrorTag, String>> =  SimpleResult(null, errors)

        s =  rL.execute()

        //pass if errors Multimap remains empty
        if ( errors.isEmpty() )
        {

        }
        //fail if otherwise
        else assertTrue(false)



    }


}