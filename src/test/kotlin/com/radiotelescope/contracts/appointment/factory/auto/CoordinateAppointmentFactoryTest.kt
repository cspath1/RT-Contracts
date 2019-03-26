package com.radiotelescope.contracts.appointment.factory.auto

import com.radiotelescope.contracts.appointment.create.CoordinateAppointmentCreate
import com.radiotelescope.contracts.appointment.request.CoordinateAppointmentRequest
import com.radiotelescope.contracts.appointment.update.CoordinateAppointmentUpdate
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.orientation.IOrientationRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class CoordinateAppointmentFactoryTest {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var telescopeRepo: ITelescopeRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    @Autowired
    private lateinit var orientationRepo: IOrientationRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    private lateinit var factory: CoordinateAppointmentFactory

    @Before
    fun setUp() {
        factory = CoordinateAppointmentFactory(
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo,
                userRepo = userRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
    }

    @Test
    fun coordinate_create() {
        // Call the factory method
        val cmd = factory.create(
                request = CoordinateAppointmentCreate.Request(
                        userId = 1L,
                        startTime = Date(System.currentTimeMillis() + 10000L),
                        endTime = Date(System.currentTimeMillis() + 30000L),
                        isPublic = true,
                        telescopeId = 1L,
                        hours = 12,
                        minutes = 12,
                        seconds = 12,
                        declination = 69.0
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is CoordinateAppointmentCreate)
    }

    @Test
    fun coordinate_update(){
        // Call the factory method
        val cmd = factory.update(
                request = CoordinateAppointmentUpdate.Request(
                        id = 123456789,
                        startTime = Date(System.currentTimeMillis() + 10000L),
                        endTime = Date(System.currentTimeMillis() + 40000L),
                        telescopeId = 123456789,
                        isPublic = false,
                        hours = 12,
                        minutes = 12,
                        seconds = 12,
                        declination = 42.0
                )
        )

        //Ensure it is the correct command
        assertTrue(cmd is CoordinateAppointmentUpdate)
    }

    @Test
    fun coordinate_request() {
        // Call the factory method
        val cmd = factory.request(
                request = CoordinateAppointmentRequest.Request(
                        userId = 1L,
                        startTime = Date(System.currentTimeMillis() + 10000L),
                        endTime = Date(System.currentTimeMillis() + 30000L),
                        isPublic = true,
                        telescopeId = 1L,
                        hours = 12,
                        minutes = 12,
                        seconds = 12,
                        declination = 69.0
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is CoordinateAppointmentRequest)
    }
}