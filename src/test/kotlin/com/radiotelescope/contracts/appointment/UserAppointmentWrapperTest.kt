package com.radiotelescope.contracts.appointment

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
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
import liquibase.integration.spring.SpringLiquibase



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

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var telescopeRepo: ITelescopeRepository

    private val baseCreateRequest = Create.Request(
            userId = -1L,
            startTime = Date(System.currentTimeMillis() + 10000L),
            endTime = Date(System.currentTimeMillis() + 30000L),
            telescopeId = 1L,
            isPublic = true
    )

    private lateinit var user: User
    private lateinit var admin: User
    private lateinit var user2: User
    private lateinit var notAdminYet: User
    private lateinit var appointment: Appointment

    private val context = FakeUserContext()
    private lateinit var factory: BaseAppointmentFactory
    private lateinit var wrapper: UserAppointmentWrapper

    @Before
    fun setUp() {
        // Persist a user
        user = testUtil.createUser("cspath1@ycp.edu")
        admin = testUtil.createUser("rpim@ycp.edu")
        user2 = testUtil.createUser("rathanapim@yahoo.com")
        notAdminYet = testUtil.createUser("rathanapim1@yahoo.com")

        // Persist Role.ADMIN for admin
        testUtil.createUserRolesForUser(
                isApproved = true,
                role = UserRole.Role.ADMIN,
                userId = admin.id
        )

        // Persis the Role.ADMIN for notAdminYet, but is not approved
        testUtil.createUserRolesForUser(
                isApproved = false,
                role = UserRole.Role.ADMIN,
                userId = notAdminYet.id
        )

        // Persist an appointment for the user
        appointment = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.Scheduled,
                startTime = Date(System.currentTimeMillis() + 10000L),
                endTime = Date(System.currentTimeMillis() + 30000L),
                isPublic = true
        )

        factory = BaseAppointmentFactory(
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo
        )

        wrapper = UserAppointmentWrapper(
                context = context,
                factory = factory,
                appointmentRepo = appointmentRepo
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
                userId = user.id,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        // Simulate a login
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        // Create a base request copy with a valid id
        val requestCopy = baseCreateRequest.copy(
                userId = user.id
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
    fun testCreatePrivate_Researcher_Failure() {
        // Make the user a guest
        testUtil.createUserRolesForUser(
                userId = user.id,
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
                        isPublic = appointment.isPublic
                )

        ) {
            assertNull(it.success)
            assertNotNull(it.error)
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
                        isPublic = appointment.isPublic
                )

        ) {
            assertNull(it.success)
            assertNotNull(it.error)
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
                        isPublic = false
                )
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.RESEARCHER))
    }

    @Test
    fun testValidUpdate_UserIsOwner_Success(){
        // Make the user a researcher
        testUtil.createUserRolesForUser(
                userId = user.id,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        // Simulate a login
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.update(
                request = Update.Request(
                        id = appointment.id,
                        startTime = Date(System.currentTimeMillis() + 20000L),
                        endTime = Date(System.currentTimeMillis() + 50000L),
                        telescopeId = appointment.telescopeId,
                        isPublic = appointment.isPublic
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
                userId = user.id,
                role = UserRole.Role.ADMIN,
                isApproved = true
        )

        // Simulate a login
        context.login(admin.id)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.update(
                request = Update.Request(
                        id = appointment.id,
                        startTime = Date(System.currentTimeMillis() + 20000L),
                        endTime = Date(System.currentTimeMillis() + 50000L),
                        telescopeId = appointment.telescopeId,
                        isPublic = appointment.isPublic
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
                        isPublic = appointment.isPublic
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
                        isPublic = appointment.isPublic
                )

        ) {
            assertNull(it.success)
            assertNotNull(it.error)
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testValidAppointmentListBetweenDates_LoggedIn_Success(){
        // Simulate a login
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.appointmentListBetweenDates(
                startTime = Date(System.currentTimeMillis()),
                endTime = Date(System.currentTimeMillis() + 200000L),
                pageable = PageRequest.of(0, 10)

        ){
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testValidAppointmentListBetweenDates_NotLoggedIn_Success(){
        val error = wrapper.appointmentListBetweenDates(
                startTime = Date(System.currentTimeMillis()),
                endTime = Date(System.currentTimeMillis() + 200000L),
                pageable = PageRequest.of(0, 10)

        ){
            assertNull(it.success)
            assertNotNull(it.error)
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))

    }
}