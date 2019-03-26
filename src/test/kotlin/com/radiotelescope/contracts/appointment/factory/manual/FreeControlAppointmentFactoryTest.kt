package com.radiotelescope.contracts.appointment.factory.manual

import com.radiotelescope.contracts.appointment.manual.StartFreeControlAppointment
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
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

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class FreeControlAppointmentFactoryTest {
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
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    private lateinit var factory: FreeControlAppointmentFactory

    @Before
    fun setUp() {
        factory = FreeControlAppointmentFactory(
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
    }

    @Test
    fun start_free_control_appointment() {
        val cmd = factory.startAppointment(
                request = StartFreeControlAppointment.Request(
                        userId = 1L,
                        telescopeId = 1L,
                        duration = 30,
                        hours = 5,
                        minutes = 34,
                        seconds = 32,
                        declination = 22.0,
                        isPublic = true
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is StartFreeControlAppointment)
    }
}