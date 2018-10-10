package com.radiotelescope.contracts.appointment

import com.example.project.contracts.appointment.BaseAppointmentFactory
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
    val startDate =  Date()
    val endDate = Date("2019-1-1")
    private var a:Appointment = Appointment(u, "appt-type1", startDate, endDate, 2, 4, "1", true, 500, u.firstName, u.lastName, 5 )

    private lateinit var factory: AppointmentFactory

    @Before
    fun init() {
        factory = BaseAppointmentFactory(apptRepo, apptInfo, userRepo)
        val uid: Long = 50
        u.id = uid
    }
   @Test
    fun retrieveTest()
    {
        val retrieved = factory.retrieve(a.id)
        //fail
        if (retrieved.execute().success == null)
        return assertTrue(false)
        //else pass
    }
}