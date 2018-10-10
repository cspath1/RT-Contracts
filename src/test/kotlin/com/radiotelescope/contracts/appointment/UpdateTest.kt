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
import com.radiotelescope.repository.telescope.Telescope
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import java.util.*

internal class UpdateTest()
{
    @Autowired
    private lateinit var apptRepo: IAppointmentRepository
    private var u: User = User("Someone", "LastName123", "piano1mano@gmail.com","123456" )
    val startDate =  Date(9000)
    val endDate = Date(10000)
    private var a:Appointment = Appointment(u, "appt-type1", startDate, endDate, 2, 4, "1", true, 500, u.firstName, u.lastName, 5)

    @Test
    fun updatetest()
    {
         if (Update(a.id, apptRepo ).execute().success == null)
             fail("updatetest failed")
        //else pass
    }
}