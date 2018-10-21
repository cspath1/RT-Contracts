package com.radiotelescope.contracts.appointment

import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository

import com.radiotelescope.repository.user.IUserRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class BaseAppointmentFactoryTest {

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var telescopeRepo: ITelescopeRepository

    private lateinit var factory: AppointmentFactory

    @Before
    fun init() {
        factory = BaseAppointmentFactory(
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo
        )
    }

    @Test
    fun create() {
        // Call the factory method
        val cmd = factory.create(
                request = Create.Request(
                        userId = 1L,
                        startTime = Date(System.currentTimeMillis() + 10000L),
                        endTime = Date(System.currentTimeMillis() + 30000L),
                        isPublic = true,
                        telescopeId = 1L
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is Create)
    }

    @Test
    fun retrieve() {
        // Call the factory method
        val cmd = factory.retrieve(
                id = 1L
        )
        // Ensure it is the correct command
        assertTrue(cmd is Retrieve)
    }

    @Test
    fun getFutureAppointmentsForUser(){
        // Call the factory method
        val cmd = factory.getFutureAppointmentsForUser(
                userId = 123456789123456,
                pageable = PageRequest.of(0,10)
        )
        //Ensure it is the correct command
        assertTrue(cmd is ListFutureAppointmentByUser)
    }

    @Test
    fun update(){
        // Call the factory method
        val cmd = factory.update(
                request = Update.Request(
                        id = 123456789,
                        startTime = Date(System.currentTimeMillis() + 10000L),
                        endTime = Date(System.currentTimeMillis() + 40000L),
                        telescopeId = 123456789,
                        isPublic = false
                )
        )

        //Ensure it is the correct command
        assertTrue(cmd is Update)
    }

}