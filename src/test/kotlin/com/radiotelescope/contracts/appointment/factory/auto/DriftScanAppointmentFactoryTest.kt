package com.radiotelescope.contracts.appointment.factory.auto

import com.radiotelescope.contracts.appointment.create.DriftScanAppointmentCreate
import com.radiotelescope.contracts.appointment.update.DriftScanAppointmentUpdate
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.orientation.IOrientationRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
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
internal class DriftScanAppointmentFactoryTest {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var radioTelescopeRepo: IRadioTelescopeRepository

    @Autowired
    private lateinit var orientationRepo: IOrientationRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    private lateinit var factory: DriftScanAppointmentFactory

    @Before
    fun setUp() {
        factory = DriftScanAppointmentFactory(
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRepo = userRepo,
                orientationRepo = orientationRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                coordinateRepo = coordinateRepo
        )
    }

    @Test
    fun driftScan_create() {
        // Call the factory method
        val cmd = factory.create(
                request = DriftScanAppointmentCreate.Request(
                        userId = 1L,
                        startTime = Date(System.currentTimeMillis() + 10000L),
                        endTime = Date(System.currentTimeMillis() + 30000L),
                        isPublic = true,
                        telescopeId = 1L,
                        elevation = 90.0,
                        azimuth = 180.0
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is DriftScanAppointmentCreate)
    }

    @Test
    fun driftScan_update() {
        // Call the factory method
        val cmd = factory.update(
                request = DriftScanAppointmentUpdate.Request(
                        id = 1L,
                        startTime = Date(System.currentTimeMillis() + 10000L),
                        endTime = Date(System.currentTimeMillis() + 30000L),
                        isPublic = true,
                        telescopeId = 1L,
                        elevation = 90.0,
                        azimuth = 180.0
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is DriftScanAppointmentUpdate)
    }
}