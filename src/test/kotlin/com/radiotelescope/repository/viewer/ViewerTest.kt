package com.radiotelescope.repository.viewer

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.User
import liquibase.integration.spring.SpringLiquibase
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
internal class ViewerTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }

        @Bean
        fun liquibase(): SpringLiquibase {
            val liquibase = SpringLiquibase()
            liquibase.setShouldRun(false)
            return liquibase
        }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var viewerRepo: IViewerRepository

    private lateinit var user: User
    private lateinit var otherUser: User
    private lateinit var appointment: Appointment


    @Before
    fun setUp(){
        user = testUtil.createUser("rpim@ycp.edu")
        otherUser = testUtil.createUser("rpim1@ycp.edu")

        appointment = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis() + 100000L),
                endTime = Date(System.currentTimeMillis() + 300000L),
                isPublic = false
        )

        testUtil.createViewer(otherUser, appointment)

    }

    @Test
    fun testFindAllByUser() {
        val viewer = viewerRepo.findAllByUser(otherUser.id)

        assertEquals(1, viewer.size)
        assertEquals(otherUser.id, viewer[0].user.id)
        assertEquals(appointment.id, viewer[0].appointment.id)
    }

    @Test
    fun testFindAllByAppointment() {
        val viewer = viewerRepo.findAllByAppointment(appointment.id)

        assertEquals(1, viewer.size)
        assertEquals(otherUser.id, viewer[0].user.id)
        assertEquals(appointment.id, viewer[0].appointment.id)
    }

    @Test
    fun testIsSharedWithUser() {

        val isShared = viewerRepo.isAppointmentSharedWithUser(
                userId = otherUser.id,
                appointmentId = appointment.id
        )

        assertTrue(isShared)
    }
}