package com.radiotelescope.contracts.viewer

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.viewer.IViewerRepository
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class BaseViewerFactoryTest : AbstractSpringTest() {
    @Autowired
    private lateinit var viewerRepo: IViewerRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private lateinit var factory: ViewerFactory

    @Before
    fun init(){
        factory = BaseViewerFactory(
                viewerRepo = viewerRepo,
                userRepo = userRepo,
                appointmentRepo = appointmentRepo
        )
    }

    @Test
    fun sharePrivateAppointment(){
        // Call the factory method
        val cmd = factory.sharePrivateAppointment(
                SharePrivateAppointment.Request(
                        email = "cspath1@ycp.edu",
                        appointmentId = 1L
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is SharePrivateAppointment)
    }

    @Test
    fun listSharedAppointment(){
        // Call the factory method
        val cmd = factory.listSharedAppointment(
                userId = 1L,
                pageable = PageRequest.of(0, 25)
        )

        // Ensure it is the correct command
        assertTrue(cmd is ListSharedAppointment)
    }

    @Test
    fun listSharedUser(){
        // Call the factory method
        val cmd = factory.listSharedUser(
                appointmentId = 1L,
                pageable = PageRequest.of(0, 25)
        )

        // Ensure it is the correct command
        assertTrue(cmd is ListSharedUser)
    }

    @Test
    fun unSharePrivateAppointment(){
        // Call the factory method
        val cmd = factory.unsharePrivateAppointment(
                UnsharePrivateAppointment.Request(
                        userId = 1L,
                        appointmentId = 1L
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is UnsharePrivateAppointment)
    }
}