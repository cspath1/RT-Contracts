package com.radiotelescope.contracts.role

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class UserUserRoleWrapperTest {
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
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    val context = FakeUserContext()
    lateinit var factory: BaseUserRoleFactory
    lateinit var wrapper: UserUserRoleWrapper

    private var adminId: Long? = null
    private var userId: Long? = null

    private var baseValidateRequest = Validate.Request(
            id = -1L,
            role = UserRole.Role.GUEST
    )

    private var unapprovedRoleId = -1L

    @Before
    fun setUp() {
        // Initialize the factory and wrapper
        factory = BaseUserRoleFactory(
                userRoleRepo = userRoleRepo,
                userRepo = userRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )

        wrapper = UserUserRoleWrapper(
                context = context,
                factory = factory,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        )

        // Persist a user and give them some roles
        val user = testUtil.createUser("cspath1@ycp.edu")
        val roles = testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.STUDENT,
                isApproved = false
        )

        roles.forEach {
            if (!it.approved)
                unapprovedRoleId = it.id
        }

        userId = user.id

        // Persist an admin user too
        val adminUser = testUtil.createUser("spathcody@gmail.com")
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.ADMIN,
                isApproved = true
        )
        adminId = adminUser.id
    }

    @Test
    fun testUnapprovedList_NotLoggedIn_Failure() {
        val error = wrapper.unapprovedList(
                pageable = PageRequest.of(0, 5)
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.containsAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER)))
    }

    @Test
    fun testUnapprovedList_UserNotAdmin_Failure() {
        // Log the user in as the student user
        context.login(userId!!)
        context.currentRoles.add(UserRole.Role.STUDENT)

        val error = wrapper.unapprovedList(
                pageable = PageRequest.of(0, 5)
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testUnapprovedList_Admin_Success() {
        // Log the user in as the student user
        context.login(adminId!!)
        context.currentRoles.add(UserRole.Role.ADMIN)

        var data: Page<UserRoleInfo> = PageImpl(arrayListOf())

        val error = wrapper.unapprovedList(
                pageable = PageRequest.of(0, 5)
        ) {
            assertNotNull(it.success)
            data = it.success!!
            assertNull(it.error)
        }

        assertNull(error)
        assertEquals(1, data.content.size)
    }

    @Test
    fun testValidate_NotLoggedIn_Failure() {
        val requestCopy = baseValidateRequest.copy(
                id = unapprovedRoleId
        )

        val error = wrapper.validate(
                request = requestCopy
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.containsAll(listOf(UserRole.Role.USER, UserRole.Role.ADMIN)))
    }

    @Test
    fun testValidate_UserNotAdmin_Failure() {
        // Log the user in as the student user
        context.login(userId!!)
        context.currentRoles.add(UserRole.Role.STUDENT)

        val requestCopy = baseValidateRequest.copy(
                id = unapprovedRoleId
        )

        val error = wrapper.validate(
                request = requestCopy
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testValidate_Admin_Success() {
        // Log the user in as the student user
        context.login(adminId!!)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val requestCopy = baseValidateRequest.copy(
                id = unapprovedRoleId
        )

        val error = wrapper.validate(
                request = requestCopy
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testRetrieve_NotLoggedIn_Failure() {
        val error = wrapper.retrieve(unapprovedRoleId) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertEquals(2, error!!.missingRoles!!.size)
        assertTrue(error.missingRoles!!.containsAll(listOf(UserRole.Role.USER, UserRole.Role.ADMIN)))
    }

    @Test
    fun testRetrieve_UserNotAdmin_Failure() {
        // Log the user in as the student user
        context.login(userId!!)
        context.currentRoles.add(UserRole.Role.STUDENT)

        val error = wrapper.retrieve(unapprovedRoleId) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testRetrieve_Admin_Success() {
        // Log the user in as the student user
        context.login(adminId!!)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.retrieve(unapprovedRoleId) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testRequestRole_Admin_Success() {
        // Simulate a login
        context.login(adminId!!)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.requestRole(
                request = RequestRole.Request(
                        userId = userId!!,
                        role = UserRole.Role.GUEST
                )
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testRequestRole_NotLoggedIn_Failure() {
        // Not simulate a login
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.requestRole(
                request = RequestRole.Request(
                        userId = userId!!,
                        role = UserRole.Role.GUEST
                )
        ){
            assertNull(it.success)
            assertNotNull(it.error)
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testRequestRole_NotAdmin_Success() {
        // Simulate a login
        context.login(userId!!)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.requestRole(
                request = RequestRole.Request(
                        userId = userId!!,
                        role = UserRole.Role.GUEST
                )
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }
}