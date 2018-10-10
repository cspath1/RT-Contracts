package com.radiotelescope.contracts.user

import com.radiotelescope.contracts.BaseUserWrapperTest
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
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
class UserUserWrapperTest : BaseUserWrapperTest() {

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    private val baseCreateRequest = Register.Request(
            firstName = "Cody",
            lastName = "Spath",
            email = "codyspath@gmail.com",
            phoneNumber = "717-823-2216",
            password = "ValidPassword1",
            passwordConfirm = "ValidPassword1",
            company = "York College of PA",
            categoryOfService = UserRole.Role.GUEST
    )

    val context = FakeUserContext()
    lateinit var factory: BaseUserFactory
    lateinit var wrapper: UserUserWrapper

    private var userId = -1L
    private var otherUserId = -1L

    private val baseAuthenticateRequest = Authenticate.Request(
            email = "spathcody@gmail.com",
            password = "Password"
    )

    @Before
    fun init() {
        // Initialize the factory and wrapper
        factory = BaseUserFactory(userRepo, userRoleRepo)
        wrapper = UserUserWrapper(context, factory, userRepo, userRoleRepo)

        // Create a user for the authentication test
        // We will need to hash the password before persisting
        val passwordEncoder = Pbkdf2PasswordEncoder(
                "YCAS2018",
                50,
                256
        )

        // Persist the User with the hashed password
        val user = userRepo.save(User(
                firstName = "Cody",
                lastName = "Spath",
                email = "spathcody@gmail.com",
                password = passwordEncoder.encode("Password")
        ))

        val otherUser = userRepo.save(User(
                firstName = "Punished",
                lastName = "Snake",
                email = "snake@kojima.biz",
                password = passwordEncoder.encode("Password")
        ))

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
    fun testInvalidRetrieve_NoUserRole_Failure() {
        // Simulate a login but do not add USER role
        context.login(userId)

        val error = wrapper.retrieve(
                request = userId
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles.contains(UserRole.Role.USER))
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
        assertTrue(error!!.missingRoles.contains(UserRole.Role.USER))
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
        assertTrue(error!!.missingRoles.contains(UserRole.Role.ADMIN))
    }

}