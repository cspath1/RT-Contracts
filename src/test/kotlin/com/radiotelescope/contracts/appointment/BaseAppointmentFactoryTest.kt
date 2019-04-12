package com.radiotelescope.contracts.appointment

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.contracts.appointment.factory.BaseAppointmentFactory
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
internal class BaseAppointmentFactoryTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var radioTelescopeRepo: IRadioTelescopeRepository
    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    private lateinit var factory: BaseAppointmentFactory

    @Before
    fun init() {
        factory = BaseAppointmentFactory(
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
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