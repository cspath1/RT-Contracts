package com.radiotelescope.repository.appointment

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner


@DataJpaTest
@RunWith(SpringRunner::class)
internal class AppointmentTest {


    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private var userId: Long?

    @Before
    fun setUp() {
        // Instantiate and persist an Appointment Entity Object
        val appointment = Appointment(1,"type", Date(100),Date(200),1,2,100,"receiver",true)
        appointmentRepo.save(appointment)

        userId = appointment.userId
    }

    @Test
    fun testFindByUserId() {


        // Use the variable set in the set up method
        val userAppointment: List<Appointment> = appointmentRepo.findByUserId(UserId).first()

        // The found appointment should have a user id of 1
        Assert.assertTrue(userAppointment.userId = 1)
    }
}
}