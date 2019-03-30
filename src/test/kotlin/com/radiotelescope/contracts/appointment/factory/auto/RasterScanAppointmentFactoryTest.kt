package com.radiotelescope.contracts.appointment.factory.auto

import com.radiotelescope.contracts.appointment.create.RasterScanAppointmentCreate
import com.radiotelescope.contracts.appointment.request.RasterScanAppointmentRequest
import com.radiotelescope.contracts.appointment.update.RasterScanAppointmentUpdate
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
internal class RasterScanAppointmentFactoryTest {
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
    private lateinit var orientationRepo: IOrientationRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    private lateinit var factory: RasterScanAppointmentFactory

    @Before
    fun setUp() {
        factory = RasterScanAppointmentFactory(
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                orientationRepo = orientationRepo
        )
    }

    @Test
    fun raster_scan_create() {
        val cmd = factory.create(
                request = RasterScanAppointmentCreate.Request(
                        userId = 1L,
                        startTime = Date(System.currentTimeMillis() + 10000L),
                        endTime = Date(System.currentTimeMillis() + 30000L),
                        isPublic = true,
                        telescopeId = 1L,
                        coordinates = listOf()
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is RasterScanAppointmentCreate)
    }

    @Test
    fun raster_scan_update() {
        val cmd = factory.update(
                request = RasterScanAppointmentUpdate.Request(
                        id = 1L,
                        startTime = Date(System.currentTimeMillis() + 100000L),
                        endTime = Date(System.currentTimeMillis() + 200000L),
                        telescopeId = 1L,
                        isPublic = true,
                        coordinates = mutableListOf()
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is RasterScanAppointmentUpdate)
    }

    @Test
    fun raster_scan_request() {
        val cmd = factory.request(
                request = RasterScanAppointmentRequest.Request(
                        userId = 1L,
                        startTime = Date(System.currentTimeMillis() + 10000L),
                        endTime = Date(System.currentTimeMillis() + 30000L),
                        isPublic = true,
                        telescopeId = 1L,
                        coordinates = mutableListOf()
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is RasterScanAppointmentRequest)
    }
}