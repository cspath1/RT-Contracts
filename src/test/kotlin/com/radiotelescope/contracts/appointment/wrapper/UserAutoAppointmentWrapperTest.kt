package com.radiotelescope.contracts.appointment.wrapper

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.contracts.appointment.create.CoordinateAppointmentCreate
import com.radiotelescope.contracts.appointment.factory.BaseAppointmentFactory
import com.radiotelescope.contracts.appointment.factory.auto.CoordinateAppointmentFactory
import com.radiotelescope.contracts.appointment.request.CoordinateAppointmentRequest
import com.radiotelescope.contracts.appointment.update.CoordinateAppointmentUpdate
import com.radiotelescope.controller.model.Profile
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.heartbeatMonitor.IHeartbeatMonitorRepository
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
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
internal class UserAutoAppointmentWrapperTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var radioTelescopeRepo: IRadioTelescopeRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    @Autowired
    private lateinit var viewerRepo: IViewerRepository

    @Autowired
    private lateinit var orientationRepo: IOrientationRepository

    @Autowired
    private lateinit var heartbeatMonitorRepo: IHeartbeatMonitorRepository

    private val baseCreateRequest = CoordinateAppointmentCreate.Request(
            userId = -1L,
            startTime = Date(System.currentTimeMillis() + 10000L),
            endTime = Date(System.currentTimeMillis() + 30000L),
            telescopeId = 1L,
            isPublic = true,
            priority = Appointment.Priority.PRIMARY,
            hours = 12,
            minutes = 12,
            seconds = 12,
            declination = 69.0
    )

    private val baseRequestRequest = CoordinateAppointmentRequest.Request(
            userId = -1L,
            startTime = Date(System.currentTimeMillis() + 10000L),
            endTime = Date(System.currentTimeMillis() + 30000L),
            telescopeId = 1L,
            isPublic = true,
            priority = Appointment.Priority.PRIMARY,
            hours = 12,
            minutes = 12,
            seconds = 12,
            declination = 69.0
    )

    private lateinit var user: User
    private lateinit var admin: User
    private lateinit var user2: User
    private lateinit var notAdminYet: User
    private lateinit var appointment: Appointment
    private lateinit var appointmentNotPublic: Appointment
    private lateinit var appointmentRequested: Appointment

    private val context = FakeUserContext()
    private lateinit var factory: BaseAppointmentFactory
    private lateinit var wrapper: UserAutoAppointmentWrapper

    @Before
    fun setUp() {
        // Persist a user
        user = testUtil.createUser("cspath1@ycp.edu")
        user.firstName = "Cody"
        user.lastName = "Spath"
        userRepo.save(user)

        admin = testUtil.createUser("rpim@ycp.edu")
        user2 = testUtil.createUser("rathanapim@yahoo.com")
        notAdminYet = testUtil.createUser("rathanapim1@yahoo.com")

        // Persist Role.ADMIN for admin
        testUtil.createUserRolesForUser(
                isApproved = true,
                role = UserRole.Role.ADMIN,
                user = admin
        )

        // Persis the Role.ADMIN for notAdminYet, but is not approved
        testUtil.createUserRolesForUser(
                isApproved = false,
                role = UserRole.Role.ADMIN,
                user = notAdminYet
        )

        // Persist an appointment for the user
        appointment = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis() + 10000L),
                endTime = Date(System.currentTimeMillis() + 30000L),
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.POINT
        )

        appointmentNotPublic = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis() + 40000L),
                endTime = Date(System.currentTimeMillis() + 50000L),
                isPublic = false,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.POINT
        )

        appointmentRequested = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.REQUESTED,
                startTime = Date(System.currentTimeMillis() + 60000L),
                endTime = Date(System.currentTimeMillis() + 70000L),
                isPublic = false,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.POINT
        )

        factory = CoordinateAppointmentFactory(
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                radioTelescopeRepo = radioTelescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                orientationRepo = orientationRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo,
                profile = Profile.TEST
        )

        wrapper = UserAutoAppointmentWrapper(
                context = context,
                factory = factory as CoordinateAppointmentFactory,
                appointmentRepo = appointmentRepo,
                viewerRepo = viewerRepo
        )
    }

    @Test
    fun testCreatePublic_NotUser_Failure() {
        // Do not log the user in

        // Create a base request copy with a valid id
        val requestCopy = baseCreateRequest.copy(
                userId = user.id
        )

        val error = wrapper.create(
                request = requestCopy
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testCreatePublic_User_Success() {
        // Give the user a 5 hour time cap
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = 5 * 60 * 60 * 1000
        )

        // Make the user a Guest
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        // Simulate a login
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        // Create a base request copy with a valid id
        val requestCopy = baseCreateRequest.copy(
                userId = user.id,
                startTime = Date(Date().time + 100000),
                endTime = Date(Date().time + 150000)
        )

        val error = wrapper.create(
                request = requestCopy
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testCreate_DifferentUser_Failure() {
        // Simulate a login as a different user
        context.login(user2.id)
        context.currentRoles.add(UserRole.Role.USER)

        // Create a base request copy with a valid id
        val requestCopy = baseCreateRequest.copy(
                userId = user.id
        )

        val error = wrapper.create(
                request = requestCopy
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testCreatePrivate_NotResearcher_Failure() {
        // Simulate a login, but do not make them a researcher
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        // Create a base request copy with a valid id that
        // is also private
        val requestCopy = baseCreateRequest.copy(
                userId = user.id,
                isPublic = false
        )

        val error = wrapper.create(
                request = requestCopy
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.RESEARCHER))
    }

    @Test
    fun testCreatePrivate_Researcher_Success() {
        // Give the user an unlimited time cap
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )

        // Make the user a Researcher
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        // Simulate a login and make the user a researcher
        context.login(user.id)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.RESEARCHER))

        // Create a base request copy with a valid id that
        // is also private
        val requestCopy = baseCreateRequest.copy(
                userId = user.id,
                startTime = Date(Date().time + 100000),
                endTime = Date(Date().time+ 150000),
                isPublic = false
        )

        val error = wrapper.create(
                request = requestCopy
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testCreatePrivate_Admin_Success() {
        // Give the user an unlimited time cap
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )

        // Make the user an Admin
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.ADMIN,
                isApproved = true
        )

        // Simulate a login and make the user an admin
        context.login(user.id)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.ADMIN))

        // Create a base request copy with a valid id that
        // is also private
        val requestCopy = baseCreateRequest.copy(
                userId = user.id,
                startTime = Date(Date().time + 100000),
                endTime = Date(Date().time+ 150000),
                isPublic = false
        )

        val error = wrapper.create(
                request = requestCopy
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testCreateSecondary_Admin_Success() {
        // Simulate a login
        context.login(user.id)
        context.currentRoles.addAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER))

        // Give the user an unlimited time cap
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )

        // Create a base request copy with a priority of SECONDARY
        val requestCopy = baseCreateRequest.copy(
                userId = user.id,
                priority = Appointment.Priority.SECONDARY
        )

        val error = wrapper.create(
                request = requestCopy
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testCreateSecondary_NotAdmin_Failure() {
        // Simulate a login
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        // Create a base request copy with a priority of SECONDARY
        val requestCopy = baseCreateRequest.copy(
                userId = user.id,
                priority = Appointment.Priority.SECONDARY
        )

        val error = wrapper.create(
                request = requestCopy
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testInvalidUpdate_NoUserRole_Failure(){
        // Simulate a login
        context.login(user.id)

        val error = wrapper.update(
                request = CoordinateAppointmentUpdate.Request(
                        id = appointment.id,
                        startTime = Date(System.currentTimeMillis() + 20000L),
                        endTime = Date(System.currentTimeMillis() + 50000L),
                        telescopeId = appointment.telescopeId,
                        isPublic = appointment.isPublic,
                        priority = appointment.priority,
                        hours = 12,
                        minutes = 12,
                        seconds = 12,
                        declination = 42.0
                )

        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testInvalidUpdate_NotLoggedIn_Failure(){
        // Don't simulate a log in
        val error = wrapper.update(
                request = CoordinateAppointmentUpdate.Request(
                        id = appointment.id,
                        startTime = Date(System.currentTimeMillis() + 20000L),
                        endTime = Date(System.currentTimeMillis() + 50000L),
                        telescopeId = appointment.telescopeId,
                        isPublic = appointment.isPublic,
                        priority = appointment.priority,
                        hours = 12,
                        minutes = 12,
                        seconds = 12,
                        declination = 42.0
                )

        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testInvalidUpdate_Private_NotResearcher_Failure() {
        // Simulate a log in to a guest account
        context.login(user.id)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.GUEST))

        val error = wrapper.update(
                request = CoordinateAppointmentUpdate.Request(
                        id = appointment.id,
                        startTime = Date(System.currentTimeMillis() + 20000L),
                        endTime = Date(System.currentTimeMillis() + 50000L),
                        telescopeId = appointment.telescopeId,
                        isPublic = false,
                        priority = appointment.priority,
                        hours = 12,
                        minutes = 12,
                        seconds = 12,
                        declination = 42.0
                )
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.RESEARCHER))
    }

    @Test
    fun testInvalidUpdate_InvalidId_Failure() {
        // Make the user an admin
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.ADMIN,
                isApproved = true
        )

        // Simulate a log in to an admin account
        context.login(user.id)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.ADMIN))

        val error = wrapper.update(
                request = CoordinateAppointmentUpdate.Request(
                        id = 420L,
                        startTime = Date(System.currentTimeMillis() + 20000L),
                        endTime = Date(System.currentTimeMillis() + 50000L),
                        telescopeId = appointment.telescopeId,
                        isPublic = false,
                        priority = appointment.priority,
                        hours = 12,
                        minutes = 12,
                        seconds = 12,
                        declination = 42.0
                )
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.invalidResourceId!!.isNotEmpty())
    }

    @Test
    fun testInvalidUpdate_SecondaryAppointment_NotAdmin_Failure() {
        // Give the user an unlimited time cap
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )

        // Simulate a log in to a researcher account
        context.login(user.id)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.RESEARCHER))

        // Make the appointment secondary
        appointment.priority = Appointment.Priority.SECONDARY
        appointmentRepo.save(appointment)

        val error = wrapper.update(
                request = CoordinateAppointmentUpdate.Request(
                        id = appointment.id,
                        startTime = Date(System.currentTimeMillis() + 20000L),
                        endTime = Date(System.currentTimeMillis() + 50000L),
                        telescopeId = appointment.telescopeId,
                        isPublic = appointment.isPublic,
                        priority = appointment.priority,
                        hours = 12,
                        minutes = 12,
                        seconds = 12,
                        declination = 42.0
                )
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testValidUpdate_SecondaryAppointment_Admin_Success() {
        // Give the user an unlimited time cap
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )

        // Simulate a log in to a admin account
        context.login(user.id)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.ADMIN))

        // Make the appointment secondary
        appointment.priority = Appointment.Priority.SECONDARY
        appointmentRepo.save(appointment)

        val error = wrapper.update(
                request = CoordinateAppointmentUpdate.Request(
                        id = appointment.id,
                        startTime = Date(System.currentTimeMillis() + 20000L),
                        endTime = Date(System.currentTimeMillis() + 50000L),
                        telescopeId = appointment.telescopeId,
                        isPublic = appointment.isPublic,
                        priority = appointment.priority,
                        hours = 12,
                        minutes = 12,
                        seconds = 12,
                        declination = 42.0
                )
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testValidUpdate_Private_Researcher_Success() {
        // Give the user an unlimited time cap
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )

        // Make the user a Researcher
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        // Simulate a log in to a researcher account
        context.login(user.id)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.RESEARCHER))

        val error = wrapper.update(
                request = CoordinateAppointmentUpdate.Request(
                        id = appointment.id,
                        startTime = Date(appointment.startTime.time + 10L),
                        endTime = Date(appointment.endTime.time -10L),
                        telescopeId = appointment.telescopeId,
                        isPublic = appointment.isPublic,
                        priority = appointment.priority,
                        hours = 12,
                        minutes = 12,
                        seconds = 12,
                        declination = 42.0
                )

        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testValidUpdate_Admin_Private_Success() {
        // Give the user an unlimited time cap
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )

        // Make the user an Admin
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.ADMIN,
                isApproved = true
        )

        // Simulate a log in to an admin account
        context.login(user.id)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.ADMIN))

        val error = wrapper.update(
                request = CoordinateAppointmentUpdate.Request(
                        id = appointment.id,
                        startTime = Date(appointment.startTime.time + 10L),
                        endTime = Date(appointment.endTime.time -10L),
                        telescopeId = appointment.telescopeId,
                        isPublic = appointment.isPublic,
                        priority = appointment.priority,
                        hours = 12,
                        minutes = 12,
                        seconds = 12,
                        declination = 42.0
                )

        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testValidUpdate_UserIsOwner_Success(){
        // Give the user an unlimited time cap
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )

        // Make the user an Admin
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.ADMIN,
                isApproved = true
        )

        // Simulate a login
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.update(
                request = CoordinateAppointmentUpdate.Request(
                        id = appointment.id,
                        startTime = Date(appointment.startTime.time + 10L),
                        endTime = Date(appointment.endTime.time -10L),
                        telescopeId = appointment.telescopeId,
                        isPublic = appointment.isPublic,
                        priority = appointment.priority,
                        hours = 12,
                        minutes = 12,
                        seconds = 12,
                        declination = 42.0
                )

        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testValidUpdate_Admin_Success(){
        // Give the user an unlimited time cap
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = null
        )

        // Make the user a Researcher
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.ADMIN,
                isApproved = true
        )

        // Simulate a login
        context.login(admin.id)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.update(
                request = CoordinateAppointmentUpdate.Request(
                        id = appointment.id,
                        startTime = Date(System.currentTimeMillis() + 100000L),
                        endTime = Date(System.currentTimeMillis() + 110000L),
                        telescopeId = appointment.telescopeId,
                        isPublic = appointment.isPublic,
                        priority = appointment.priority,
                        hours = 12,
                        minutes = 12,
                        seconds = 12,
                        declination = 42.0
                )

        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testInvalidUpdate_NotOwner_Failure(){
        // Simulate a login
        context.login(user2.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.update(
                request = CoordinateAppointmentUpdate.Request(
                        id = appointment.id,
                        startTime = Date(System.currentTimeMillis() + 20000L),
                        endTime = Date(System.currentTimeMillis() + 50000L),
                        telescopeId = appointment.telescopeId,
                        isPublic = appointment.isPublic,
                        priority = appointment.priority,
                        hours = 12,
                        minutes = 12,
                        seconds = 12,
                        declination = 42.0
                )

        ) {
            assertNull(it.success)
            assertNotNull(it.error)
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testInvalidUpdate_NotAdminYet_Failure(){
        // Simulate a login
        context.login(notAdminYet.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.update(
                request = CoordinateAppointmentUpdate.Request(
                        id = appointment.id,
                        startTime = Date(System.currentTimeMillis() + 20000L),
                        endTime = Date(System.currentTimeMillis() + 50000L),
                        telescopeId = appointment.telescopeId,
                        isPublic = appointment.isPublic,
                        priority = appointment.priority,
                        hours = 12,
                        minutes = 12,
                        seconds = 12,
                        declination = 42.0
                )

        ) {
            assertNull(it.success)
            assertNotNull(it.error)
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }



    @Test
    fun testRequestPublic_NotUser_Failure() {
        // Do not log the user in

        // Create a base request copy with a valid id
        val requestCopy = baseRequestRequest.copy(
                userId = user.id
        )

        val error = wrapper.request(
                request = requestCopy
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testRequest_DifferentUser_Failure() {
        // Simulate a login as a different user
        context.login(user2.id)
        context.currentRoles.add(UserRole.Role.USER)

        // Create a base request copy with a valid id
        val requestCopy = baseRequestRequest.copy(
                userId = user.id
        )

        val error = wrapper.request(
                request = requestCopy
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testRequestPublic_User_Success() {
        // Simulate a login
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        // Create a base request copy with a valid id
        val requestCopy = baseRequestRequest.copy(
                userId = user.id
        )

        val error = wrapper.request(
                request = requestCopy
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testRequestPrivate_NotResearcher_Failure() {
        // Simulate a login, but do not make them a researcher
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        // Create a base request copy with a valid id that
        // is also private
        val requestCopy = baseRequestRequest.copy(
                userId = user.id,
                isPublic = false
        )

        val error = wrapper.request(
                request = requestCopy
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.RESEARCHER))
    }

    @Test
    fun testRequestSecondary_NotAdmin_Failure() {
        // Simulate a login
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        val requestCopy = baseRequestRequest.copy(
                userId = user.id,
                priority = Appointment.Priority.SECONDARY
        )

        val error = wrapper.request(
                request = requestCopy
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testRequestSecondary_Admin_Success() {
        // Simulate a login
        context.login(user.id)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.ADMIN))

        val requestCopy = baseRequestRequest.copy(
                userId = user.id,
                priority = Appointment.Priority.SECONDARY
        )

        val error = wrapper.request(
                request = requestCopy
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testRequestPrivate_Researcher_Success() {
        // Simulate a login and make the user a researcher
        context.login(user.id)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.RESEARCHER))

        // Create a base request copy with a valid id that
        // is also private
        val requestCopy = baseRequestRequest.copy(
                userId = user.id,
                isPublic = false
        )

        val error = wrapper.request(
                request = requestCopy
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }
}