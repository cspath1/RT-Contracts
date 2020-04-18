package com.radiotelescope.contracts.user

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.accountActivateToken.IAccountActivateTokenRepository
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.loginAttempt.ILoginAttemptRepository
import com.radiotelescope.repository.model.user.Filter
import com.radiotelescope.repository.model.user.SearchCriteria
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.security.FakeUserContext
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
internal class UserUserWrapperTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var accountActivateTokenRepo: IAccountActivateTokenRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    @Autowired
    private lateinit var loginAttemptRepo: ILoginAttemptRepository

    private val baseCreateRequest = Register.Request(
            firstName = "Cody",
            lastName = "Spath",
            email = "spathcody@gmail.com",
            emailConfirm = "spathcody@gmail.com",
            phoneNumber = "717-823-2216",
            password = "ValidPassword1",
            passwordConfirm = "ValidPassword1",
            company = "York College of PA",
            categoryOfService = UserRole.Role.GUEST
    )

    private val baseChangePasswordRequest = ChangePassword.Request(
            id = -1,
            password = "Password1!",
            passwordConfirm = "Password1!",
            currentPassword = "Password1@"
    )

    private val context = FakeUserContext()
    private lateinit var factory: BaseUserFactory
    private lateinit var wrapper: UserUserWrapper

    private var userId = -1L
    private var otherUserId = -1L

    private val baseAuthenticateRequest = Authenticate.Request(
            email = "cspath1@ycp.edu",
            password = "Password"
    )

    @Before
    fun init() {
        // Initialize the factory and wrapper
        factory = BaseUserFactory(
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                accountActivateTokenRepo = accountActivateTokenRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                loginAttemptRepo = loginAttemptRepo
        )

        wrapper = UserUserWrapper(
                context = context,
                factory = factory,
                userRepo = userRepo
        )

        // Persist the User with the hashed password
        val user = testUtil.createUserWithEncodedPassword(
                email = "cspath1@ycp.edu",
                password = "Password"
        )

        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = Appointment.GUEST_APPOINTMENT_TIME_CAP
        )

        val otherUser = testUtil.createUserWithEncodedPassword(
                email = "codyspath@gmail.com",
                password = "Password"
        )

        testUtil.createUserRolesForUser(
                user = otherUser,
                role = UserRole.Role.MEMBER,
                isApproved = true
        )

        testUtil.createAllottedTimeCapForUser(
                user = otherUser,
                allottedTime = Appointment.MEMBER_APPOINTMENT_TIME_CAP
        )

        userId = user.id
        otherUserId = otherUser.id
    }

    @Test
    fun testValidRegistration_Success() {
        val (id, error) = wrapper.register(
                request = baseCreateRequest
        ).execute()

        assertNotNull(id)
        assertNull(error)
    }

    @Test
    fun testValidAuthentication_Success() {
        val (info, error) = wrapper.authenticate(
                request = baseAuthenticateRequest
        ).execute()

        assertNotNull(info)
        assertNull(error)
    }

    @Test
    fun testValidRetrieve_SameUser_Success() {
        // Simulate a login
        context.login(userId)
        context.currentRoles.add(UserRole.Role.USER)

        var userInfo: UserInfo? = null

        val error = wrapper.retrieve(
                request = userId
        ) {
            userInfo = it.success
            assertNull(it.error)
        }

        assertNull(error)
        assertNotNull(userInfo)
    }

    @Test
    fun testValidRetrieve_Admin_Success() {
        // Simulate login as admin
        context.login(otherUserId)
        context.currentRoles.add(UserRole.Role.ADMIN)

        var userInfo: UserInfo? = null

        val error = wrapper.retrieve(
                request = userId
        ) {
            userInfo = it.success
            assertNull(it.error)
        }

        assertNull(error)
        assertNotNull(userInfo)
    }

    @Test
    fun testValidRetrieve_Admin_InvalidId_Failure() {
        // Simulate login as an admin
        context.login(otherUserId)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.retrieve(
                request = 311L
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.invalidResourceId!!.getValue("ID").isNotEmpty())
    }

    @Test
    fun testInvalidRetrieve_NoUserRole_Failure() {
        // Simulate a login but do not add USER role
        context.login(userId)

        val error = wrapper.retrieve(
                request = userId
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testInvalidRetrieve_NotLoggedIn_Failure() {
        // Do not log the user in
        val error = wrapper.retrieve(
                request = userId
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testInvalidRetrieve_DifferentUser_Failure() {
        // Log the user in as a different user than the retrieve
        context.login(otherUserId)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.retrieve(
                request = userId
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testValidList_Admin_Success() {
        // Log the user in and make them an admin
        context.login(otherUserId)
        context.currentRoles.addAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER))

        var info: Page<UserInfo> = PageImpl<UserInfo>(arrayListOf())

        val error = wrapper.list(
                request = PageRequest.of(0, 5)
        ) {
            info = it.success!!
            assertNull(it.error)
        }

        assertNull(error)
        assertEquals(2, info.content.size)
    }

    @Test
    fun testInvalidList_UserNotLoggedIn_Failure() {
        val error = wrapper.list(
                request = PageRequest.of(0, 5)
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.containsAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER)))
    }

    @Test
    fun testInvalidList_UserNotAdmin_Failure() {
        // Log the user in as a base user
        context.login(userId)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.list(
                request = PageRequest.of(0, 5)
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun updateValidUpdate_UserIsOwner_Success(){
        // Simulate a login
        context.login(userId)
        context.currentRoles.add(UserRole.Role.USER)

        var id = -1L
        val error = wrapper.update(
                request = Update.Request(
                        id = userId,
                        firstName = "Rathana",
                        lastName = "Pim",
                        phoneNumber = "717-555-1111",
                        company = "York College of Pennsylvania",
                        profilePicture = "rathanapic.jpg",
                        profilePictureApproved = false,
                        notificationType = "SMS"
                )
        ){
            id = it.success!!
            assertNull(it.error)
        }

        assertNull(error)
        assertEquals(userId, id)
    }

    @Test
    fun testValidUpdate_UserIsAdmin_Success(){
        // Simulate a login
        context.login(otherUserId)
        context.currentRoles.add(UserRole.Role.ADMIN)

        var id = -1L
        val error = wrapper.update(
                request = Update.Request(
                        id = userId,
                        firstName = "Rathana",
                        lastName = "Pim",
                        phoneNumber = "717-555-1111",
                        company = "York College of Pennsylvania",
                        profilePicture = "rathanapic.jpg",
                        profilePictureApproved = false,
                        notificationType = "SMS"
                )
        ){
            id = it.success!!
            assertNull(it.error)
        }

        assertNull(error)
        assertEquals(userId, id)
    }

    @Test
    fun testInvalidUpdate_Admin_InvalidId_Failure() {
        // Simulate a login
        context.login(otherUserId)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.update(
                request = Update.Request(
                        id = 311L,
                        firstName = "Rathana",
                        lastName = "Pim",
                        phoneNumber = "717-555-1111",
                        company = "York College of Pennsylvania",
                        profilePicture = "rathanapic.jpg",
                        profilePictureApproved = false,
                        notificationType = "SMS"
                )
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.invalidResourceId!!.getValue("ID").isNotEmpty())
    }

    @Test
    fun testInvalidUpdate_NotOwner_Failure(){
        // Simulate a login
        context.login(otherUserId)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.update(
                request = Update.Request(
                        id = userId,
                        firstName = "Rathana",
                        lastName = "Pim",
                        phoneNumber = "717-555-1111",
                        company = "York College of Pennsylvania",
                        profilePicture = "rathanapic.jpg",
                        profilePictureApproved = false,
                        notificationType = "SMS"
                )
        ){
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testInvalidUpdate_NotLoggedIn_Failure() {
        // Do not simulate a login

        val error = wrapper.update(
                request = Update.Request(
                        id = userId,
                        firstName = "Codiferous",
                        lastName = "Spath",
                        phoneNumber = "717-823-2216",
                        company = "Business, None of Your, Inc.",
                        profilePicture = "codypic.jpg",
                        profilePictureApproved = false,
                        notificationType = "SMS"
                )
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testDeleteValid_Owner_Failure() {
        // Simulate a login
        context.login(userId)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.delete(
                id = userId
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testDelete_NotLoggedIn_Failure() {
        // Do not log the user
        val error = wrapper.delete(
                id = userId
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testDelete_NotAdmin_Failure() {
        // Log the user in as a different user (not admin)
        context.login(otherUserId)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.delete(
                id = userId
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testDelete_Admin_Success() {
        // Log the user in as admin
        context.login(otherUserId)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.delete(
                id = userId
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testBan_Admin_Success() {
        // Log the user in as an admin
        context.login(otherUserId)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.ADMIN))

        val error = wrapper.ban(
                id = userId
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testBan_NotAdmin_Failure() {
        // Log the user in as a base user
        context.login(otherUserId)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.ban(
                id = userId
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testUnban_NotLoggedIn_Failure() {
        // Do not log the user in

        val error = wrapper.unban(
                id = userId
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testUnban_NotAdmin_Failure() {
        // Log the user in as something other than an admin
        context.login(userId)
        context.currentRoles.add(UserRole.Role.STUDENT)

        val error = wrapper.unban(
                id = userId
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testUnban_Admin_Failure() {
        // Simulate the user being banned
        val theUser = userRepo.findById(userId).get()
        theUser.status = User.Status.BANNED
        theUser.active = false
        userRepo.save(theUser)

        // Log the user in as an admin
        context.login(otherUserId)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.unban(
                id = userId
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testChangePassword_NotLoggedIn_Failure() {
        // Do not log the user in

        // Create a request object
        val requestCopy = baseChangePasswordRequest.copy(id = userId)

        // Execute the wrapper method
        val error = wrapper.changePassword(
                request = requestCopy
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testChangePassword_DifferentUser_Failure() {
        // Log the user in as a different user
        context.login(otherUserId)
        context.currentRoles.add(UserRole.Role.USER)

        // Create a request object
        val requestCopy = baseChangePasswordRequest.copy(
                id = userId
        )

        // Execute the wrapper method
        val error = wrapper.changePassword(
                request = requestCopy
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testChangePassword_InvalidUserId_Failure() {
        // Log the user in
        context.login(userId)
        context.currentRoles.add(UserRole.Role.USER)

        // Create a request object
        val requestCopy = baseChangePasswordRequest.copy(
                id = 311L
        )

        // Execute the wrapper method
        val error = wrapper.changePassword(
                request = requestCopy
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.invalidResourceId!!.isNotEmpty())
    }

    @Test
    fun testChangePassword_Valid_Success() {
        // Log the user in
        context.login(userId)
        context.currentRoles.add(UserRole.Role.USER)

        // Create a request object
        val requestCopy = baseChangePasswordRequest.copy(
                id = userId
        )

        // Change the user info so the password is hashed
        val hashedPassword = User.rtPasswordEncoder.encode("Password1@")
        val theUser = userRepo.findById(userId).get()
        theUser.password = hashedPassword
        userRepo.save(theUser)

        // Execute the factory method
        val error = wrapper.changePassword(
                request = requestCopy
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testSearch_User_Success() {
        // Log the user in
        context.login(userId)
        context.currentRoles.add(UserRole.Role.USER)

        // Create the SearchCriteria list
        val searchCriteria = arrayListOf<SearchCriteria>()
        searchCriteria.add(SearchCriteria(Filter.FIRST_NAME, "First"))

        val error = wrapper.search(
                searchCriteria = searchCriteria,
                pageable = PageRequest.of(0, 20)
        ) {
            assertNull(it.error)
            assertNotNull(it.success)
        }

        assertNull(error)
    }

    @Test
    fun testSearch_NotLoggedIn_Failure() {
        // Create the SearchCriteria list
        val searchCriteria = arrayListOf<SearchCriteria>()
        searchCriteria.add(SearchCriteria(Filter.FIRST_NAME, "First"))

        val error = wrapper.search(
                searchCriteria = searchCriteria,
                pageable = PageRequest.of(0, 10)
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
    }

    @Test
    fun testInvite_User_Success() {
        // Log the user in and make them an admin
        context.login(otherUserId)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.invite(
                email = "whatever@email.mail"
        ) {
            assertTrue(it.success!!)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testInvite_NotLoggedIn_Failure() {
        val error = wrapper.invite(
                email = "whatever@email.mail"
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }
}