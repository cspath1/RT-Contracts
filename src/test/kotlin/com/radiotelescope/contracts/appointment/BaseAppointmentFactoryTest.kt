package com.radiotelescope.contracts.appointment

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository

import com.radiotelescope.repository.user.IUserRepository
import liquibase.integration.spring.SpringLiquibase
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

        @Bean
        fun liquibase(): SpringLiquibase {
            val liquibase = SpringLiquibase()
            liquibase.setShouldRun(false)
            return liquibase
        }
    }

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

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
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo
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

    @Test
    fun listBetweenDates(){
        val cmd = factory.listBetweenDates(
                startTime = Date(System.currentTimeMillis()),
                endTime = Date(System.currentTimeMillis() + 10000L),
                telescopeId = 1L
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
    fun request() {
        // Call the factory method
        val cmd = factory.request(
                request = Request.Request(
                        userId = 1L,
                        startTime = Date(System.currentTimeMillis() + 10000L),
                        endTime = Date(System.currentTimeMillis() + 30000L),
                        isPublic = true,
                        telescopeId = 1L
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is Request)
    }

}