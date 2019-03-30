package com.radiotelescope.contracts.appointment.create

import com.radiotelescope.TestUtil
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
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
internal class CreateTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

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

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    private lateinit var user: User

    private lateinit var create: CoordinateAppointmentCreate

    private val baseRequest = CoordinateAppointmentCreate.Request(
            userId = -1L,
            startTime = Date(System.currentTimeMillis() + 100000L),
            endTime = Date(System.currentTimeMillis() + 300000L),
            telescopeId = 1L,
            isPublic = true,
            hours = 12,
            minutes = 12,
            seconds = 12,
            declination = 69.0
    )


    @Before
    fun setUp(){
        user = testUtil.createUser("rpim@ycp.edu")
        create = CoordinateAppointmentCreate(
                request = baseRequest,
                userRepo = userRepo,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                coordinateRepo = coordinateRepo

        )
    }

    @Test
    fun testIsOverlap_ValidConstraints_Success(){
        // Make the user a guest
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        val requestCopy = baseRequest.copy(userId = user.id)

        val overlap = create.isOverlap(
                request = requestCopy,
                appointmentRepo = appointmentRepo
        )
        assertFalse(overlap)
    }

    @Test
    fun testIsOverlap_Overlap_Failure(){
        // Make the user a guest
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        val requestCopy = baseRequest.copy(userId = user.id)

        // Create an appointment that exist during the request time
        testUtil.createAppointment(
                user = user,
                startTime = requestCopy.startTime,
                endTime = requestCopy.endTime,
                telescopeId = 1L,
                isPublic = true,
                status = Appointment.Status.SCHEDULED,
                type = Appointment.Type.POINT
        )

        val overlap = create.isOverlap(
                request = requestCopy,
                appointmentRepo = appointmentRepo
        )

        assertTrue(overlap)
    }

    @Test
    fun testValidateAvailableAllotedTime_ValidConstraints_Success(){
        // Give the user unlimited time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        val requestCopy = baseRequest.copy(userId = user.id)

        val errors = create.validateAvailableAllottedTime(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )

        assertTrue(errors.isEmpty)
    }

    @Test
    fun testValidateAvailableAllotedTime_NotEnough_Failure(){
        // Give the user 5 hours
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = 5 * 60 * 60 * 1000
        )
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        val requestCopy = baseRequest.copy(
                userId = user.id,
                startTime = Date(Date().time),
                endTime = Date(Date().time + 6 * 60 * 60 *1000)
        )

        val errors = create.validateAvailableAllottedTime(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )

        assertFalse(errors.isEmpty)
        assertEquals(1, errors.size())
        assertTrue(errors[ErrorTag.ALLOTTED_TIME].isNotEmpty())
    }

    @Test
    fun testBasicValidateRequest_ValidConstraints_Success(){
        // Give the user unlimited time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )

        // Make the user a guest
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        val requestCopy = baseRequest.copy(userId = user.id)

        val errors = create.basicValidateRequest(
                request = requestCopy,
                userRepo = userRepo,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
        assertNull(errors)
    }

    @Test
    fun testBasicValidateRequest_UserDoesNotExist_Failure(){
        // Give the user unlimited time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )

        // Make the user a guest
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        val requestCopy = baseRequest.copy(userId = -1L)

        val errors = create.basicValidateRequest(
                request = requestCopy,
                userRepo = userRepo,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
        assertNotNull(errors)
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.USER_ID].isNotEmpty())
    }

    @Test
    fun testBasicValidateRequest_TelescopeDoesNotExist_Failure(){
        // Give the user unlimited time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )

        // Make the user a guest
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        val requestCopy = baseRequest.copy(
                userId = user.id,
                telescopeId = -1L
        )

        val errors = create.basicValidateRequest(
                request = requestCopy,
                userRepo = userRepo,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
        assertNotNull(errors)
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.TELESCOPE_ID].isNotEmpty())
    }

    @Test
    fun testBasicValidateRequest_AllotedTimeCapDoesNotExist_Failure(){
        // Make the user a guest
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        val requestCopy = baseRequest.copy(userId = user.id)

        val errors = create.basicValidateRequest(
                request = requestCopy,
                userRepo = userRepo,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
        assertNotNull(errors)
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.ALLOTTED_TIME_CAP].isNotEmpty())
    }

    @Test
    fun testBasicValidateRequest_StartTimeAfterEndTime_Failure(){
        // Give the user unlimited time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )

        // Make the user a guest
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        val requestCopy = baseRequest.copy(
                userId = user.id,
                startTime = Date(baseRequest.endTime.time),
                endTime = Date(baseRequest.startTime.time)
        )

        val errors = create.basicValidateRequest(
                request = requestCopy,
                userRepo = userRepo,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
        assertNotNull(errors)
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.END_TIME].isNotEmpty())
    }

    @Test
    fun testBasicValidateRequest_StartTimeBeforeDate_Failure(){
        // Give the user unlimited time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )

        // Make the user a guest
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        val requestCopy = baseRequest.copy(
                userId = user.id,
                startTime = Date(Date().time - 10000L)
        )

        val errors = create.basicValidateRequest(
                request = requestCopy,
                userRepo = userRepo,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
        assertNotNull(errors)
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.START_TIME].isNotEmpty())
    }

    @Test
    fun testBasicValidateRequest_IsOverlap_Failure(){
        // Give the user unlimited time
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )

        // Make the user a guest
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        val requestCopy = baseRequest.copy(userId = user.id)

        // Create an appointment that exist during the request time
        testUtil.createAppointment(
                user = user,
                startTime = requestCopy.startTime,
                endTime = requestCopy.endTime,
                telescopeId = 1L,
                isPublic = true,
                status = Appointment.Status.SCHEDULED,
                type = Appointment.Type.POINT
        )

        val errors = create.basicValidateRequest(
                request = requestCopy,
                userRepo = userRepo,
                appointmentRepo = appointmentRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
        assertNotNull(errors)
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.OVERLAP].isNotEmpty())
    }
}