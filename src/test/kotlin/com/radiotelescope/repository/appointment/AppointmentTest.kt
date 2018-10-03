package com.radiotelescope.repository.appointment

import com.radiotelescope.repository.user.User
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import java.util.*


@DataJpaTest
@RunWith(SpringRunner::class)
internal class AppointmentTest {


    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private var userId: Int = -1
    var u: User = User("Someone", "LastName123", "piano1mano@gmail.com","123456" )

    val d =  Date()
    val dd = Date("2018-1-1")
    // Instantiate and persist an Appointment Entity Object
    var a:Appointment = Appointment(u, "appt-type1", d, dd, 2, 4, "1", true, Date(), 1, u.firstName, u.lastName, 0 )

    @Before
    fun setUp() {
        appointmentRepo.save(a)
        userId = a.assocUserId
    }

    @Test
    fun testFindByUserId() {

        // Use the variable set in the set up method
        //Get all the appointments by this user

        val appts: List<Appointment> = appointmentRepo.findByUser(u)

        for (aa:Appointment in appts) {

            if (aa.assocUserId == 1)
            {
                Assert.assertEquals(aa.type, a.type)
                Assert.assertEquals(aa.user, a.user)
                Assert.assertEquals(aa.startTime, a.startTime)
                Assert.assertEquals(aa.endTime, a.endTime)
                Assert.assertEquals(aa.telescopeId, a.telescopeId)
                Assert.assertEquals(aa.celestialBodyId, a.celestialBodyId)
                Assert.assertEquals(aa.receiver, a.receiver)
                Assert.assertEquals(aa.isPublic, a.isPublic)
                Assert.assertEquals(aa.date, a.date)
                Assert.assertEquals(aa.assocUserId, a.assocUserId)
                Assert.assertEquals(aa.uFirstName, a.uLastName)
                Assert.assertEquals(aa.state, a.state)
            }
        }

    }
}
