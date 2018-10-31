package com.radiotelescope.contracts.appointment

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import liquibase.integration.spring.SpringLiquibase
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
class AppointmentListBetweenDatesTest {
    @TestConfiguration
    internal class UtilTestContextConfiguration {
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
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private lateinit var user1: User
    private lateinit var user2: User
    private var user1Id = -1L
    private var user2Id = -1L

    private var startTime = System.currentTimeMillis() + 10000L
    private var endTime =   System.currentTimeMillis() + 50000L

    @Before
    fun init(){
        // Persist the users
        user1 = testUtil.createUser("rpim@ycp.edu")
        user2 = testUtil.createUser("rpim1@ycp.edu")

        // Set the userId
        user1Id = userRepo.findByEmail(user1.email)!!.id
        user2Id = userRepo.findByEmail(user2.email)!!.id

        // Persist the appointments
        testUtil.createAppointment(
                user = user1,
                telescopeId = 1,
                status = Appointment.Status.Scheduled,
                startTime = Date(startTime + 100L),
                endTime = Date(startTime + 200L),
                isPublic = true
        )

        testUtil.createAppointment(
                user = user2,
                telescopeId = 1,
                status = Appointment.Status.Scheduled,
                startTime = Date(startTime + 300L),
                endTime = Date(startTime + 400L),
                isPublic = true
        )
    }


    @Test
    fun testValid_ValidConstraints_Success(){
        val (infoPage, error) = AppointmentListBetweenDates(
                startTime = Date(startTime),
                endTime = Date(endTime),
                pageable = PageRequest.of(0, 10),
                appointmentRepo = appointmentRepo,
                userRepo = userRepo
        ).execute()

        // Should not have failed
        Assert.assertNull(error)
        Assert.assertNotNull(infoPage)

        // should only have 1 AppointmentInfo
        Assert.assertEquals(2, infoPage!!.content.size)
    }

    @Test
    fun testInvalid_EndTimeIsLessThanStartTime_Failure() {
        val (infoPage, error) = AppointmentListBetweenDates(
                startTime = Date(endTime),
                endTime = Date(startTime),
                pageable = PageRequest.of(0, 10),
                appointmentRepo = appointmentRepo,
                userRepo = userRepo
        ).execute()

        // Should have failed
        Assert.assertNotNull(error)
        Assert.assertNull(infoPage)

        // Ensure it failed because of the endTime
        Assert.assertTrue(error!![ErrorTag.END_TIME].isNotEmpty())
    }
}