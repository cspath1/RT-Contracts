package com.radiotelescope.contracts.appointment

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
@ActiveProfiles(value = ["test"])
internal class UserAvailableTimeTest {
    @TestConfiguration
    internal class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    private lateinit var user: User
    private val oneHour = 60 * 60 * 1000

    @Before
    fun setUp() {
        user = testUtil.createUser("rpim@ycp.edu")
    }

    @Test
    fun testValid_Guess_Success(){
        // Make the user a guest
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis() + oneHour),
                endTime = Date(System.currentTimeMillis() + oneHour + Appointment.GUEST_APPOINTMENT_TIME_CAP),
                isPublic = true
        )

        val(time, errors) = UserAvailableTime(
                userId = user.id,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Make sure it was a success
        assertNotNull(time)
        assertNull(errors)

        // Make sure the available time is correct
        assertEquals(0L, time)
    }

    @Test
    fun testValid_OtherUser_Success(){
        // Make the user role other than guess
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis() + oneHour),
                endTime = Date(System.currentTimeMillis() + oneHour + oneHour),
                isPublic = true
        )

        val(time, errors) = UserAvailableTime(
                userId = user.id,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Make sure it was a success
        assertNotNull(time)
        assertNull(errors)

        // Make sure the available time is correct
        assertEquals(Appointment.OTHER_USERS_APPOINTMENT_TIME_CAP - oneHour, time)
    }

    @Test
    fun testInvalid_UserDoesNotExist_Failure(){
        // Make a user role
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis() + oneHour),
                endTime = Date(System.currentTimeMillis() + oneHour + oneHour),
                isPublic = true
        )

        val(time, errors) = UserAvailableTime(
                userId = 123456789,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Make sure it was a failure
        assertNull(time)
        assertNotNull(errors)

        // Make sure it failed because of the correct reason
        assertTrue(errors!![ErrorTag.USER_ID].isNotEmpty())
    }

    @Test
    fun testInvalid_NoUserRole_Failure(){
        // Do not make the user role

        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis() + oneHour),
                endTime = Date(System.currentTimeMillis() + oneHour + oneHour),
                isPublic = true
        )

        val(time, errors) = UserAvailableTime(
                userId = user.id,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Make sure it was a failure
        assertNull(time)
        assertNotNull(errors)

        // Make sure it failed because of the correct reason
        assertTrue(errors!![ErrorTag.CATEGORY_OF_SERVICE].isNotEmpty())
    }

}