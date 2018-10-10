package com.radiotelescope.contracts.appointment

import com.google.common.collect.HashMultimap
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.appointment.Appointment
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.user.User
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
internal class CancelTest
{
    @Autowired
    private lateinit var apptRepo: IAppointmentRepository
    var u: User = User("Someone", "LastName123", "piano1mano@gmail.com","123456" )
    var request: Create.Request = Create.Request(u,  Date(), Date("2019-7-7"), 1, 2,  true, u.id,  u.firstName, u.lastName, 500,  Appointment.Status.InProgress)
    var cancelObj:Cancel = Cancel(request.apptId, apptRepo)

    @Test
    fun deleteTest()
    {
        var errors = HashMultimap.create<ErrorTag,String>()
        var s: SimpleResult<Long, Multimap<ErrorTag, String>> = cancelObj.execute()
        //fail case
        if (s.success== null)
            assertTrue(false)
        //else pass
    }
}