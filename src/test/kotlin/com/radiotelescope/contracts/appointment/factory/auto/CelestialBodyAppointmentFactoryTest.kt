package com.radiotelescope.contracts.appointment.factory.auto

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.contracts.appointment.create.CelestialBodyAppointmentCreate
import com.radiotelescope.contracts.appointment.request.CelestialBodyAppointmentRequest
import com.radiotelescope.contracts.appointment.update.CelestialBodyAppointmentUpdate
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.orientation.IOrientationRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
internal class CelestialBodyAppointmentFactoryTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var radioTelescopeRepo: IRadioTelescopeRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    @Autowired
    private lateinit var celestialBodyRepo: ICelestialBodyRepository

    @Autowired
    private lateinit var orientationRepo: IOrientationRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    private lateinit var factory: CelestialBodyAppointmentFactory

    @Before
    fun setUp() {
        factory = CelestialBodyAppointmentFactory(
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRoleRepo = userRoleRepo,
                celestialBodyRepo = celestialBodyRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
    }

    @Test
    fun celestial_body_create() {
        val cmd = factory.create(
                request = CelestialBodyAppointmentCreate.Request(
                        userId = 1L,
                        startTime = Date(System.currentTimeMillis() + 10000L),
                        endTime = Date(System.currentTimeMillis() + 30000L),
                        isPublic = true,
                        telescopeId = 1L,
                        celestialBodyId = 1L
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is CelestialBodyAppointmentCreate)
    }

    @Test
    fun celestial_body_update() {
        val cmd = factory.update(
                request = CelestialBodyAppointmentUpdate.Request(
                        id = 1L,
                        telescopeId = 1L,
                        startTime = Date(System.currentTimeMillis() + 10000L),
                        endTime = Date(System.currentTimeMillis() + 20000L),
                        isPublic = true,
                        celestialBodyId = 1L
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is CelestialBodyAppointmentUpdate)
    }

    @Test
    fun celestial_body_request() {
        val cmd = factory.request(
                request = CelestialBodyAppointmentRequest.Request(
                        userId = 1L,
                        startTime = Date(System.currentTimeMillis() + 10000L),
                        endTime = Date(System.currentTimeMillis() + 30000L),
                        isPublic = true,
                        telescopeId = 1L,
                        celestialBodyId = 1L
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is CelestialBodyAppointmentRequest)
    }
}