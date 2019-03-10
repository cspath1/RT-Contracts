package com.radiotelescope.contracts.appointment

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.model.appointment.Filter
import com.radiotelescope.repository.model.appointment.SearchCriteria
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.telescope.ITelescopeRepository
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
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
@ActiveProfiles(value = ["test"])
internal class UserAppointmentWrapperTest {
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
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var telescopeRepo: ITelescopeRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    @Autowired
    private lateinit var viewerRepo: IViewerRepository

    private val baseCreateRequest = Create.Request(
            userId = -1L,
            startTime = Date(System.currentTimeMillis() + 10000L),
            endTime = Date(System.currentTimeMillis() + 30000L),
            telescopeId = 1L,
            isPublic = true,
            hours = 12,
            minutes = 12,
            seconds = 12,
            declination = 69.0
    )

    private val baseRequestRequest = Request.Request(
            userId = -1L,
            startTime = Date(System.currentTimeMillis() + 10000L),
            endTime = Date(System.currentTimeMillis() + 30000L),
            telescopeId = 1L,
            isPublic = true,
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
    private lateinit var wrapper: UserAppointmentWrapper

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
                isPublic = true
        )

        appointmentNotPublic = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis() + 40000L),
                endTime = Date(System.currentTimeMillis() + 50000L),
                isPublic = false
        )

        appointmentRequested = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.REQUESTED,
                startTime = Date(System.currentTimeMillis() + 60000L),
                endTime = Date(System.currentTimeMillis() + 70000L),
                isPublic = false
        )

        factory = BaseAppointmentFactory(
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo
        )

        wrapper = UserAppointmentWrapper(
                context = context,
                factory = factory,
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
        // Make the user a guest
        testUtil.createUserRolesForUser(
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
        // Make the user a researcher
        testUtil.createUserRolesForUser(
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
        // Make the user an admin
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
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
    fun testRetrieve_NotUser_Failure() {
        // Do not log the user in

        val error = wrapper.retrieve(
                id = appointment.id
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testRetrieve_NotOwner_Private_Failure() {
        // Persist a new user, and attempt to access
        // the appointment as this user
        val newUser = testUtil.createUser("michaelscott@dundermifflin.com")
        context.login(newUser.id)
        context.currentRoles.add(UserRole.Role.USER)

        // Set the appointment to private
        appointment.isPublic = false
        appointmentRepo.save(appointment)

        val error = wrapper.retrieve(
                id = appointment.id
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testRetrieve_Admin_Private_Success() {
        // Persist a new user, and attempt to access
        // the appointment as this user
        val newUser = testUtil.createUser("michaelscott@dundermifflin.com")
        context.login(newUser.id)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.ADMIN))

        val error = wrapper.retrieve(
                id = appointment.id
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testRetrieve_NotOwner_Public_Success() {
        // Persist a new user, and attempt to access
        // the appointment as this user
        val newUser = testUtil.createUser("michaelscott@dundermifflin.com")
        context.login(newUser.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.retrieve(
                id = appointment.id
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testRetrieve_Owner_Success() {
        // Simulate a login
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.retrieve(
                id = appointment.id
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testRetrieve_NonExistent_Failure() {
        val error = wrapper.retrieve(
                id = 311L
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertNull(error!!.missingRoles)
        assertNotNull(error.invalidResourceId)
    }

    @Test
    fun testRetrieve_SharedWith_Success() {
        // Share the appointment with a user
        val sharedUser = testUtil.createUser("rpim2@ycp.edu")
        testUtil.createViewer(sharedUser, appointmentNotPublic)

        // Simulate a login
        context.login(sharedUser.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.retrieve(
                id = appointmentNotPublic.id
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testRetrieve_NotSharedWith_Failure() {
        // Don't share the appointment with a user
        val notSharedUser = testUtil.createUser("rpim2@ycp.edu")

        // Simulate a login
        context.login(notSharedUser.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.retrieve(
                id = appointmentNotPublic.id
        ) {
            assertNull(it.success)
            assertNotNull(it.error)
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testValidGetFutureAppointmentsForUser_SameUser_Success() {
        // Simulate a login
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.userFutureList(
                pageable = PageRequest.of(0, 10),
                userId = user.id
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testValidGetFutureAppointmentsForUser_Admin_Success() {
        // Simulate a login
        context.login(admin.id)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.userFutureList(
                pageable = PageRequest.of(0, 10),
                userId = user.id
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testInvalidGetFutureAppointmentsForUser_NoUserRole_Failure(){
        // Simulate a login
        context.login(user.id)

        val error = wrapper.userFutureList(
                pageable = PageRequest.of(0, 10),
                userId = user.id
        ) {
            assertNull(it.success)
            assertNotNull(it.error)
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testInvalidGetFutureAppointmentsForUser_NotLoggedIn_Failure(){
        // Do not log the user in

        val error = wrapper.userFutureList(
                pageable = PageRequest.of(0, 10),
                userId = user.id
        ) {
            assertNull(it.success)
            assertNotNull(it.error)
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testInvalidGetFutureAppointmentsForUser_DifferentUser_Failure(){
        // Simulate a login
        context.login(user2.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.userFutureList(
                pageable = PageRequest.of(0, 10),
                userId = user.id
        ) {
            assertNull(it.success)
            assertNotNull(it.error)
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testInvalidGetFutureAppointmentsForUser_NotAdminYet_Failure(){
        // Simulate a login
        context.login(notAdminYet.id)
        context.currentRoles.add(UserRole.Role.USER)

        // Initialize the rapper with the context
        wrapper = UserAppointmentWrapper(
                appointmentRepo = appointmentRepo,
                viewerRepo = viewerRepo,
                context = context,
                factory = factory
        )

        val error = wrapper.userFutureList(
                pageable = PageRequest.of(0, 10),
                userId = user.id
        ) {
            assertNull(it.success)
            assertNotNull(it.error)
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testPastAppointmentForUserList_NotLoggedIn_Failure() {
        // Do not log the user in
        val error = wrapper.userCompleteList(
                userId = user.id,
                pageable = PageRequest.of(0, 30)
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testPastAppointmentsForUserList_DifferentUser_NotAdmin_Failure() {
        // Log the user in as a different user
        context.login(user2.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.userCompleteList(
                userId = user.id,
                pageable = PageRequest.of(0, 30)
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testPastAppointmentsForUserList_Admin_Success() {
        // Log the user in as an admin
        context.login(user2.id)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.ADMIN))

        val error = wrapper.userCompleteList(
                userId = user.id,
                pageable = PageRequest.of(0, 20)
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testPastAppointmentsForUser_SameUser_Success() {
        // Log the user in
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.userCompleteList(
                userId = user.id,
                pageable = PageRequest.of(0, 20)
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testCancel_NonexistentRecord_Failure() {
        val error = wrapper.cancel(
                appointmentId = 311L
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.invalidResourceId!!.isNotEmpty())
    }

    @Test
    fun testCancel_NotLoggedIn_Failure() {
        val error = wrapper.cancel(
                appointmentId = appointment.id
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testCancel_DifferentUser_NotAdmin_Failure() {
        // Log the user in as a different user
        context.login(user2.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.cancel(
                appointmentId = appointment.id
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testCancel_Admin_Success() {
        // Log the user in as an admin
        context.login(user2.id)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.ADMIN))

        val error = wrapper.cancel(
                appointmentId = appointment.id
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testCancel_SameUser_Success() {
        // Log the user in as the owner
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.cancel(
                appointmentId = appointment.id
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testRetrieveFutureAppointmentsByTelescopeId_NotLoggedIn_Failure() {
        val error = wrapper.retrieveFutureAppointmentsByTelescopeId(
                telescopeId = 1L,
                pageable = PageRequest.of(0, 20)
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testRetrieveFutureAppointmentsByTelescopeId_LoggedIn_Failure() {
        // Log the user in
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.retrieveFutureAppointmentsByTelescopeId(
                telescopeId = 1L,
                pageable = PageRequest.of(0, 20)
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testInvalidUpdate_NoUserRole_Failure(){
        // Simulate a login
        context.login(user.id)

        val error = wrapper.update(
                request = Update.Request(
                        id = appointment.id,
                        startTime = Date(System.currentTimeMillis() + 20000L),
                        endTime = Date(System.currentTimeMillis() + 50000L),
                        telescopeId = appointment.telescopeId,
                        isPublic = appointment.isPublic,
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
                request = Update.Request(
                        id = appointment.id,
                        startTime = Date(System.currentTimeMillis() + 20000L),
                        endTime = Date(System.currentTimeMillis() + 50000L),
                        telescopeId = appointment.telescopeId,
                        isPublic = appointment.isPublic,
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
                request = Update.Request(
                        id = appointment.id,
                        startTime = Date(System.currentTimeMillis() + 20000L),
                        endTime = Date(System.currentTimeMillis() + 50000L),
                        telescopeId = appointment.telescopeId,
                        isPublic = false,
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
                request = Update.Request(
                        id = 420L,
                        startTime = Date(System.currentTimeMillis() + 20000L),
                        endTime = Date(System.currentTimeMillis() + 50000L),
                        telescopeId = appointment.telescopeId,
                        isPublic = false,
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
    fun testValidUpdate_Private_Researcher_Success() {
        // Make the user a researcher
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        // Simulate a log in to a researcher account
        context.login(user.id)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.RESEARCHER))

        val error = wrapper.update(
                request = Update.Request(
                        id = appointment.id,
                        startTime = Date(appointment.startTime.time + 10L),
                        endTime = Date(appointment.endTime.time -10L),
                        telescopeId = appointment.telescopeId,
                        isPublic = appointment.isPublic,
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
                request = Update.Request(
                        id = appointment.id,
                        startTime = Date(appointment.startTime.time + 10L),
                        endTime = Date(appointment.endTime.time -10L),
                        telescopeId = appointment.telescopeId,
                        isPublic = appointment.isPublic,
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
        // Make the user a researcher
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        // Simulate a login
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.update(
                request = Update.Request(
                        id = appointment.id,
                        startTime = Date(appointment.startTime.time + 10L),
                        endTime = Date(appointment.endTime.time -10L),
                        telescopeId = appointment.telescopeId,
                        isPublic = appointment.isPublic,
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
        // Make the user a admin
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.ADMIN,
                isApproved = true
        )

        // Simulate a login
        context.login(admin.id)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.update(
                request = Update.Request(
                        id = appointment.id,
                        startTime = Date(System.currentTimeMillis() + 100000L),
                        endTime = Date(System.currentTimeMillis() + 110000L),
                        telescopeId = appointment.telescopeId,
                        isPublic = appointment.isPublic,
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
                request = Update.Request(
                        id = appointment.id,
                        startTime = Date(System.currentTimeMillis() + 20000L),
                        endTime = Date(System.currentTimeMillis() + 50000L),
                        telescopeId = appointment.telescopeId,
                        isPublic = appointment.isPublic,
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
                request = Update.Request(
                        id = appointment.id,
                        startTime = Date(System.currentTimeMillis() + 20000L),
                        endTime = Date(System.currentTimeMillis() + 50000L),
                        telescopeId = appointment.telescopeId,
                        isPublic = appointment.isPublic,
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
    fun testValidMakePublic_Researcher_Success(){
        // Simulate a login
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.RESEARCHER)

        val error = wrapper.makePublic(
                appointmentId = appointmentNotPublic.id
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testValidMakePublic_Admin_Success() {
        // Simulate a login
        context.login(user2.id)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.makePublic(
                appointmentId = appointmentNotPublic.id
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testInvalidMakePublic_InvalidAppointment_Failure() {
        // Execute the method on an invalid id
        val error = wrapper.makePublic(
                appointmentId = 420L
        ) {
            assertNull(it.success)
            assertNotNull(it.error)
        }

        assertNotNull(error)
        assertTrue(error!!.invalidResourceId!!.isNotEmpty())
    }

    @Test
    fun testInvalidMakePublic_NotResearcher_Failure(){
        // Simulate a login as an admin user (different user)
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.makePublic(
                appointmentId = appointmentNotPublic.id
        ) {
            assertNull(it.success)
            assertNotNull(it.error)
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.RESEARCHER))
    }

    @Test
    fun testInvalidMakePublic_NotLoggedIn_Failure() {
        // Do not log the user in

        val error = wrapper.makePublic(
                appointmentId = appointmentNotPublic.id
        ) {
            assertNull(it.success)
            assertNotNull(it.error)
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testValidAppointmentListBetweenDates_LoggedIn_Success(){
        // Simulate a login
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.listBetweenDates(
                request = ListBetweenDates.Request(
                    startTime = Date(System.currentTimeMillis()),
                    endTime = Date(System.currentTimeMillis() + 200000L),
                    telescopeId = 1L
                )
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testInvalidAppointmentListBetweenDates_NotLoggedIn_Success(){
        val error = wrapper.listBetweenDates(
                request = ListBetweenDates.Request(
                    startTime = Date(System.currentTimeMillis()),
                    endTime = Date(System.currentTimeMillis() + 200000L),
                    telescopeId = 1L
                )
        ) {
            assertNull(it.success)
            assertNotNull(it.error)
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))

    }

    @Test
    fun testValidPublicCompletedAppointments_User_Success() {
        // Log the user in
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.publicCompletedAppointments(
                pageable = PageRequest.of(0, 5)
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testInvalidPublicCompletedAppointments_NotLoggedIn_Failure() {
        // Do not log the user in
        val error = wrapper.publicCompletedAppointments(
                pageable = PageRequest.of(0, 5)
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
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

    @Test
    fun testListRequest_Admin_Success() {
        // Simulate a login and make the user a researcher
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.requestedList(
                pageable = PageRequest.of(0, 10)
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testListRequest_NotAdmin_Failure() {
        // Simulate a login and make the user a researcher
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.requestedList(
                pageable = PageRequest.of(0, 10)
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testListRequest_NotLoggedIn_Failure() {
        // Do not log the user in
        val error = wrapper.requestedList(
                pageable = PageRequest.of(0, 10)
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testApproveDenyRequest_Admin_Success() {
        // Simulate a login and make the user a researcher
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.approveDenyRequest(
                request = ApproveDenyRequest.Request(
                        appointmentId = appointmentRequested.id,
                        isApprove = true
                )
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testApproveDenyRequest_NotAdmin_Failure() {
        // Simulate a login and make the user a researcher
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.approveDenyRequest(
                request = ApproveDenyRequest.Request(
                        appointmentId = appointmentRequested.id,
                        isApprove = true
                )
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testApproveDenyRequest_NotLoggedIn_Failure() {
        // Do not log the user in
        val error = wrapper.approveDenyRequest(
                request = ApproveDenyRequest.Request(
                        appointmentId = appointmentRequested.id,
                        isApprove = true
                )
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testValidUserAvailableTime_LoggedIn_Success(){
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        // Simulate a login
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.userAvailableTime(
                userId = user.id
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testInvalidUserAvailableTime_NotLoggedIn_Failure(){
        val error = wrapper.userAvailableTime(
                userId = user.id
        ) {
            assertNull(it.success)
            assertNotNull(it.error)
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testInvalidUserAvailableTime_DifferentUser_Failure() {
        // Simulate a login as a different user
        context.login(user2.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.userAvailableTime(
                userId = user.id
        ) {
            assertNull(it.success)
            assertNotNull(it.error)
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testSearch_LoggedIn_Success() {
        val searchCriteria = arrayListOf<SearchCriteria>()
        searchCriteria.add(SearchCriteria(Filter.USER_FULL_NAME, "cody spath"))

        // Simulate a login
        context.login(user2.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.search(
                searchCriteria = searchCriteria,
                pageable = PageRequest.of(0, 19)
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testSearch_NotLoggedIn_Failure() {
        val searchCriteria = arrayListOf<SearchCriteria>()
        searchCriteria.add(SearchCriteria(Filter.USER_FULL_NAME, "cody spath"))

        val error = wrapper.search(
                searchCriteria = searchCriteria,
                pageable = PageRequest.of(0, 19)
        ) {
            assertNull(it.success)
            assertNotNull(it.error)
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }
}