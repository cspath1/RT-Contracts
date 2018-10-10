package com.radiotelescope.repository.appointment

import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
internal class AppointmentTest {

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository
    private var user: User? = null

    @Before
    fun setUp() {
        user = userRepo.save(User("Someone", "LastName123", "piano1mano@gmail.com","123456" ))
    }
    @Test
    fun testFindFutureAppointmentsByUser() {
        // This is just a quick hack to see if the query works
        appointmentRepo.save(
                Appointment(
                        user = user!!,
                        type = "type",
                        startTime = Date(),
                        endTime = Date(Date().time + 15000),
                        telescopeId = 1,
                        celestialBodyId = 1,
                        receiver = "Receiver",
                        isPublic = true,
                        userId = user!!.id,
                        uFirstName = "Cody",
                        uLastName = "Spath",
                        state = 1
                )
        )
        val pageOfAppointments = appointmentRepo.findFutureAppointmentsByUser(user!!.id, PageRequest.of(0, 5))
        Assert.assertEquals(1, pageOfAppointments.content.size)

    }
}
