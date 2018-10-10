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
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)

internal class RetrieveTest
{

    private var u: User = User("Someone", "LastName123", "piano1mano@gmail.com","123456" )
    val startDate =  Date(9000)
    val endDate = Date(10000)
    private var a:Appointment = Appointment(u, "appt-type1", startDate, endDate, 2, 4, "1", true, 500, u.firstName, u.lastName, 5)
    private var apptInfo: AppointmentInfo = AppointmentInfo(a)
    @Autowired
    private lateinit var apptRepo: IAppointmentRepository
    private val retrieveObj:Retrieve = Retrieve(a, apptInfo, apptRepo, a.id)

    @Test
    fun retrieveTest()
    {
        var errors = HashMultimap.create<ErrorTag,String>()
        var s: SimpleResult<Long, Multimap<ErrorTag, String>> =  retrieveObj.execute()
        //fail case
        if ( s.success == null )
         assertTrue(false)
//else pass
    }

    @Test
    fun retrieveTest2()
    {
        val (info, error) = Retrieve(
                appt = a,
                appt_id = 10,
                apptInfo = apptInfo,
                apptRepo = apptRepo
        ).execute()

        assertNull(error)
        assertNotNull(info)
    }
    @Test
    fun failRetrieveTest(){
        val (info, error) = Retrieve(
                appt = a,
                appt_id = 11,
                apptInfo = apptInfo,
                apptRepo = apptRepo
        ).execute()

        assertNull(info)
        assertNotNull(error)
    }


}