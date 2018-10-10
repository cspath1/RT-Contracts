package com.radiotelescope.contracts.appointment

import com.google.common.collect.HashMultimap
import com.radiotelescope.repository.appointment.Appointment
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.springframework.data.domain.PageRequest
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)

internal class RetrieveListTest
{
    private var pageable = PageRequest.of(0, 5)
    private var u: User = User("Someone", "LastName123", "piano1mano@gmail.com","123456" )
    val startDate =  Date()
    val endDate = Date("2019-1-1")
    private var a:Appointment = Appointment(u, "appt-type1", startDate, endDate, 2, 4, "1", true, 500, u.firstName, u.lastName, 5 )
    @Autowired
    private lateinit var userRepo: IUserRepository
    @Autowired
    private lateinit var apptRepo: IAppointmentRepository
    private var retrieveList : RetrieveList = RetrieveList(apptRepo, u.id, userRepo, pageable)

   private var rL:RetrieveList = RetrieveList(apptRepo, u.id, userRepo, pageable)

    @Test
    fun retrieveListTest()
    {
        var errors = HashMultimap.create<ErrorTag,String>()
        var s: SimpleResult<Long, Multimap<ErrorTag, String>> = retrieveList.execute()
        //fail case
        if (s.success == null)
            return assertTrue(false)
        //else pass
    }


}