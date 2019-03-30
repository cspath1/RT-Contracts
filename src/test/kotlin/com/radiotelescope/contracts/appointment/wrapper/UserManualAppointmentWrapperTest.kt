package com.radiotelescope.contracts.appointment.wrapper

import com.radiotelescope.TestUtil
import com.radiotelescope.contracts.appointment.factory.manual.FreeControlAppointmentFactory
import com.radiotelescope.contracts.appointment.factory.manual.ManualAppointmentFactory
import com.radiotelescope.contracts.appointment.manual.AddFreeControlAppointmentCommand
import com.radiotelescope.contracts.appointment.manual.StartFreeControlAppointment
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.orientation.IOrientationRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.repository.viewer.IViewerRepository
import com.radiotelescope.security.FakeUserContext
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
internal class UserManualAppointmentWrapperTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil {
            return TestUtil()
        }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    @Autowired
    private lateinit var viewerRepo: IViewerRepository

    @Autowired
    private lateinit var radioTelescopeRepo: IRadioTelescopeRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    @Autowired
    private lateinit var orientationRepo: IOrientationRepository

    private lateinit var user: User
    private lateinit var appointment: Appointment

    private val context = FakeUserContext()
    private lateinit var factory: ManualAppointmentFactory
    private lateinit var wrapper: UserManualAppointmentWrapper

    private lateinit var baseStartRequest: StartFreeControlAppointment.Request

    @Before
    fun setUp() {
        user = testUtil.createUser("cspath1@ycp.edu")

        // Make the user an admin
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.ADMIN,
                isApproved = true
        )



        factory = FreeControlAppointmentFactory(
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                coordinateRepo = coordinateRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                orientationRepo = orientationRepo
        )

        wrapper = UserManualAppointmentWrapper(
                context = context,
                factory = factory,
                appointmentRepo = appointmentRepo,
                viewerRepo = viewerRepo
        )

        appointment = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.IN_PROGRESS,
                startTime = Date(System.currentTimeMillis() - 150000L),
                endTime = Date(System.currentTimeMillis() + 150000L),
                isPublic = true,
                type = Appointment.Type.FREE_CONTROL
        )

        baseStartRequest = StartFreeControlAppointment.Request(
                userId = user.id,
                telescopeId = 1L,
                duration = 30,
                hours = 1,
                minutes = 2,
                seconds = 3,
                declination = 4.20,
                isPublic = true
        )
    }

    @Test
    fun testStartAppointment_Admin_Success() {
        // Delete the appointment persisted above
        coordinateRepo.deleteAll()
        appointmentRepo.deleteAll()

        // Simulate a login as an admin
        context.login(user.id)
        context.currentRoles = mutableListOf(UserRole.Role.USER, UserRole.Role.ADMIN)

        val error = wrapper.startAppointment(
                request = baseStartRequest
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testStartAppointment_NotLoggedIn_Failure() {
        val error = wrapper.startAppointment(
                request = baseStartRequest
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testStartAppointment_NotAdmin_Failure() {
        // Simulate a login as a student
        context.login(user.id)
        context.currentRoles = mutableListOf(UserRole.Role.USER, UserRole.Role.STUDENT)

        val error = wrapper.startAppointment(
                request = baseStartRequest
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testStopAppointment_Admin_Success() {
        // Simulate a login as an admin
        context.login(user.id)
        context.currentRoles = mutableListOf(UserRole.Role.USER, UserRole.Role.ADMIN)

        val error = wrapper.stopAppointment(appointment.id) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testStopAppointment_NotAdmin_Failure() {
        // Simulate a login as a student
        context.login(user.id)
        context.currentRoles = mutableListOf(UserRole.Role.USER, UserRole.Role.STUDENT)

        val error = wrapper.stopAppointment(appointment.id) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testStopAppointment_NotLoggedIn_Failure() {
        val error = wrapper.stopAppointment(appointment.id) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testAddCommand_Admin_Success() {
        // Simulate a login as an admin
        context.login(user.id)
        context.currentRoles = mutableListOf(UserRole.Role.USER, UserRole.Role.ADMIN)

        val request = AddFreeControlAppointmentCommand.Request(
                appointmentId = appointment.id,
                hours = 2,
                minutes = 3,
                seconds = 4,
                declination = 4.20
        )

        val error = wrapper.addCommand(request) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testAddCommand_NotAdmin_Failure() {
        // Simulate a login as a student
        context.login(user.id)
        context.currentRoles = mutableListOf(UserRole.Role.USER, UserRole.Role.STUDENT)

        val request = AddFreeControlAppointmentCommand.Request(
                appointmentId = appointment.id,
                hours = 2,
                minutes = 3,
                seconds = 4,
                declination = 4.20
        )

        val error = wrapper.addCommand(request) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testAddCommand_NotLoggedIn_Failure() {
        val request = AddFreeControlAppointmentCommand.Request(
                appointmentId = appointment.id,
                hours = 2,
                minutes = 3,
                seconds = 4,
                declination = 4.20
        )

        val error = wrapper.addCommand(request) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testCalibrateAppointment_Admin_Success() {
        // Simulate a login as an admin
        context.login(user.id)
        context.currentRoles = mutableListOf(UserRole.Role.USER, UserRole.Role.ADMIN)

        val error = wrapper.calibrateAppointment(appointment.id) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testCalibrateAppointment_NotAdmin_Failure() {
        // Simulate a login as a student
        context.login(user.id)
        context.currentRoles = mutableListOf(UserRole.Role.USER, UserRole.Role.STUDENT)

        val error = wrapper.calibrateAppointment(appointment.id) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testCalibrateAppointment_NotLoggedIn_Failure() {
        // Do not log the user in
        val error = wrapper.calibrateAppointment(appointment.id) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }
}