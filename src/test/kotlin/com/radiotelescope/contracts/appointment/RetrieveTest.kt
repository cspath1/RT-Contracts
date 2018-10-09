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
import com.radiotelescope.repository.user.User
import java.util.*
import org.springframework.beans.factory.annotation.Autowired
import com.radiotelescope.repository.appointment.IAppointmentRepository

@DataJpaTest
@RunWith(SpringRunner::class)

internal class RetrieveTest
{

    private var u: User = User("Someone", "LastName123", "piano1mano@gmail.com","123456" )
    val d =  Date()
    val dd = Date("2018-1-1")

    private var a:Appointment = Appointment(u, "appt-type1", d, dd, 2, 4, "1", true, Date(), 3, u.firstName, u.lastName, 0 )

    private var aI: AppointmentInfo = AppointmentInfo( a.id, a.startTime, a.endTime, a.telescopeId, a.celestialBodyId, a.isPublic, a.assocUserId, a.uFirstName, a.uLastName, a.status)


    //How do you initialize a Repository object?
    //if I need to pass in a repository object to Retrieve?
    @Autowired
    private lateinit var aRepo:IAppointmentRepository

    private val retrieveT:Retrieve = Retrieve(a, aI, aRepo, a.id)

    @Test
fun RetrieveTest()
    {
        var errors = HashMultimap.create<ErrorTag,String>()
        var s: SimpleResult<Long, Multimap<ErrorTag, String>> =  SimpleResult(null, errors)

        s =  retrieveT.execute()

        //pass if errors Multimap remains empty
        if ( errors.isEmpty() )
        {

        }
        //fail if otherwise
        else assertTrue(false)


    }
}