package com.radiotelescope.contracts.user

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.accountActivateToken.IAccountActivateTokenRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.security.FakeUserContext
import liquibase.integration.spring.SpringLiquibase
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
@ActiveProfiles(value = ["test"])
internal class UserUserWrapperTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
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
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var accountActivateTokenRepo: IAccountActivateTokenRepository

    private val baseCreateRequest = Register.Request(
            firstName = "Cody",
            lastName = "Spath",
            email = "spathcody@gmail.com",
            phoneNumber = "717-823-2216",
            password = "ValidPassword1",
            passwordConfirm = "ValidPassword1",
            company = "York College of PA",
            categoryOfService = UserRole.Role.GUEST
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
                accountActivateTokenRepo = accountActivateTokenRepo
        )

        wrapper = UserUserWrapper(
                context = context,
                factory = factory,
                userRepo = userRepo
        )

        // Create a user for the authentication test
        // We will need to hash the password before persisting
        val passwordEncoder = Pbkdf2PasswordEncoder(
                "YCAS2018",
                50,
                256
        )

        // Persist the User with the hashed password
        val user = testUtil.createUserWithEncodedPassword(
                email = "cspath1@ycp.edu",
                password = passwordEncoder.encode("Password")
        )

        val otherUser = testUtil.createUserWithEncodedPassword(
                email = "codyspath@gmail.com",
                password = passwordEncoder.encode("Password")
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

        val error = wrapper.pageable(
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
        val error = wrapper.pageable(
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

        val error = wrapper.pageable(
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

        var Id = -1L
        val error = wrapper.update(
                request = Update.Request(
                        id = userId,
                        firstName = "Rathana",
                        lastName = "Pim",
                        email = "rpim@ycp.edu",
                        phoneNumber = "717-555-1111",
                        company = "York College of Pennsylvania"
                )
        ){
            Id = it.success!!
            assertNull(it.error)
        }

        assertNull(error)
        assertEquals(userId, Id)
    }

    @Test
    fun testValidUpdate_UserIsAdmin_Success(){
        // Simulate a login
        context.login(otherUserId)
        context.currentRoles.add(UserRole.Role.ADMIN)

        var Id = -1L
        val error = wrapper.update(
                request = Update.Request(
                        id = userId,
                        firstName = "Rathana",
                        lastName = "Pim",
                        email = "rpim@ycp.edu",
                        phoneNumber = "717-555-1111",
                        company = "York College of Pennsylvania"
                )
        ){
            Id = it.success!!
            assertNull(it.error)
        }

        assertNull(error)
        assertEquals(userId, Id)
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
                        email = "rpim@ycp.edu",
                        phoneNumber = "717-555-1111",
                        company = "York College of Pennsylvania"
                )
        ){
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
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
        // Log the user in as an admin
        context.login(userId)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.unban(
                id = userId
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }
}