package com.radiotelescope.contracts.appointment.wrapper

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.contracts.appointment.ApproveDenyRequest
import com.radiotelescope.contracts.appointment.ListBetweenDates
import com.radiotelescope.contracts.appointment.factory.BaseAppointmentFactory
import com.radiotelescope.contracts.appointment.factory.auto.CoordinateAppointmentFactory
import com.radiotelescope.controller.model.Profile
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.heartbeatMonitor.IHeartbeatMonitorRepository
import com.radiotelescope.repository.model.appointment.Filter
import com.radiotelescope.repository.model.appointment.SearchCriteria
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
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
internal class BaseUserAppointmentWrapperTest : AbstractSpringTest() {
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

    private lateinit var user: User
    private lateinit var admin: User
    private lateinit var user2: User
    private lateinit var notAdminYet: User
    private lateinit var appointment: Appointment
    private lateinit var appointmentNotPublic: Appointment
    private lateinit var appointmentRequested: Appointment

    private val context = FakeUserContext()
    private lateinit var factory: BaseAppointmentFactory
    private lateinit var wrapper: BaseUserAppointmentWrapper

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

        wrapper = BaseUserAppointmentWrapper(
                context = context,
                factory = factory,
                appointmentRepo = appointmentRepo,
                viewerRepo = viewerRepo
        )
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
        // Give the user a 48 hour time cap
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = 48 * 60 * 60 * 1000
        )

        // Give the user the member role
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.MEMBER,
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