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


@DataJpaTest
@RunWith(SpringRunner::class)

internal class RetrieveTelescopeByIdTest
{

    @Autowired
    private lateinit var apptRepo: IAppointmentRepository
    @Autowired
    private lateinit var userRepo: IUserRepository

    private var u: User = User("Someone", "LastName123", "piano1mano@gmail.com","123456" )
    val startDate =  Date(9000)
    val endDate = Date(10000)
    private var a:Appointment = Appointment(u,  startDate, endDate, 2, 4,  true, 500, u.firstName, u.lastName )
    private var apptInfo: AppointmentInfo = AppointmentInfo(a)
    private var t: Telescope = Telescope()
    private var pageable = PageRequest.of(0, 5)

    @Test
    fun getApptsByTelescopeIdTest()
    {
        t.setId(0)
        //fail case
       if (RetrieveByTelescopeId(apptRepo, apptInfo, t.getId(), pageable, userRepo, u.id).execute().success == null)
        assertTrue(false)

        //else pass

    }



}