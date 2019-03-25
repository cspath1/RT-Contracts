package com.radiotelescope.contracts.appointment

import com.radiotelescope.TestUtil
import com.radiotelescope.contracts.appointment.create.CelestialBodyAppointmentCreate
import com.radiotelescope.contracts.appointment.create.CoordinateAppointmentCreate
import com.radiotelescope.contracts.appointment.create.RasterScanAppointmentCreate
import com.radiotelescope.contracts.appointment.factory.AppointmentFactory
import com.radiotelescope.contracts.appointment.factory.CelestialBodyAppointmentFactory
import com.radiotelescope.contracts.appointment.factory.CoordinateAppointmentFactory
import com.radiotelescope.contracts.appointment.factory.RasterScanAppointmentFactory
import com.radiotelescope.contracts.appointment.request.CelestialBodyAppointmentRequest
import com.radiotelescope.contracts.appointment.request.CoordinateAppointmentRequest
import com.radiotelescope.contracts.appointment.request.RasterScanAppointmentRequest
import com.radiotelescope.contracts.appointment.update.CelestialBodyAppointmentUpdate
import com.radiotelescope.contracts.appointment.update.CoordinateAppointmentUpdate
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
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
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class BaseAppointmentFactoryTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

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
    private lateinit var celestialBodyRepo: ICelestialBodyRepository

    @Autowired
    private lateinit var orientationRepo: IOrientationRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    private lateinit var factory: AppointmentFactory

    @Before
    fun init() {
        factory = CoordinateAppointmentFactory(
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
    }

    // NOTE: For other appointment creates, we need to instantiate
    // a different factory

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
    fun celestial_body_create() {
        // Instantiate the proper factory
        factory = CelestialBodyAppointmentFactory(
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                celestialBodyRepo = celestialBodyRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                orientationRepo = orientationRepo
        )

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
    fun raster_scan_create() {
        // Instantiate the proper factory
        factory = RasterScanAppointmentFactory(
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )

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
        val cmd = factory.userFutureList(
                userId = 123456789123456,
                pageable = PageRequest.of(0,10)
        )

        //Ensure it is the correct command
        assertTrue(cmd is UserFutureList)
    }

    @Test
    fun pastAppointmentListForUser() {
        // Call the factory method
        val cmd = factory.userCompletedList(
                userId = 1L,
                pageable = PageRequest.of(0, 20)
        )

        // Ensure it is the correct command
        assertTrue(cmd is UserCompletedList)
    }

    @Test
    fun cancel() {
        // Call the factory method
        val cmd = factory.cancel(
                appointmentId = 1L
        )

        // Ensure it is the correct command
        assertTrue(cmd is Cancel)
    }

    @Test
    fun retrieveFutureAppointmentsByTelescopeId() {
        // Call the factory method
        val cmd = factory.retrieveFutureAppointmentsByTelescopeId(
                telescopeId = 1L,
                pageable = PageRequest.of(0, 20)
        )

        // Ensure it is the correct command
        assertTrue(cmd is RetrieveFutureAppointmentsByTelescopeId)
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
    fun celestial_body_update() {
        // Instantiate the proper factory
        factory = CelestialBodyAppointmentFactory(
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                celestialBodyRepo = celestialBodyRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                orientationRepo = orientationRepo
        )

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
    fun listBetweenDates(){
        val cmd = factory.listBetweenDates(
                request = ListBetweenDates.Request(
                    startTime = Date(System.currentTimeMillis()),
                    endTime = Date(System.currentTimeMillis() + 10000L),
                    telescopeId = 1L
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is ListBetweenDates)
    }

    @Test
    fun makePublic(){
        val cmd = factory.makePublic(
                appointmentId = 1L
        )

        // Ensure it is the correct command
        assertTrue(cmd is MakePublic)
    }

    @Test
    fun publicCompletedAppointments() {
        val cmd = factory.publicCompletedAppointments(
                pageable = PageRequest.of(0, 5)
        )

        // Ensure it is the correct command
        assertTrue(cmd is PublicCompletedAppointments)
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

    @Test
    fun celestial_body_request() {
        // Instantiate the proper factory
        factory = CelestialBodyAppointmentFactory(
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                celestialBodyRepo = celestialBodyRepo,
                orientationRepo = orientationRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )

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

    @Test
    fun raster_scan_request() {
        // Instantiate the proper factory
        factory = RasterScanAppointmentFactory(
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )

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

    @Test
    fun listRequest() {
        // Call the factory method
        val cmd = factory.requestedList(
                pageable = PageRequest.of(0, 10)
        )

        // Ensure it is the correct command
        assertTrue(cmd is RequestedList)
    }

    @Test
    fun approveDenyRequest() {
        // Call the factory method
        val cmd = factory.approveDenyRequest(
                request = ApproveDenyRequest.Request(
                        appointmentId = 1L,
                        isApprove = true
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is ApproveDenyRequest)
    }

    @Test
    fun userAvailableTime() {
        // Call the factory method
        val cmd = factory.userAvailableTime(
                userId = 1L
        )

        // Ensure it is the correct command
        assertTrue(cmd is UserAvailableTime)
    }

    @Test
    fun search() {
        // Call the factory method
        val cmd = factory.search(
                searchCriteria = listOf(),
                pageable = PageRequest.of(0, 10)
        )

        // Ensure it is the correct command
        assertTrue(cmd is Search)
    }
}