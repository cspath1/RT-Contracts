package com.radiotelescope.contracts.role

import com.radiotelescope.BaseDataJpaTest
import com.radiotelescope.TestUtil
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
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
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class UserUserRoleWrapperTest : BaseDataJpaTest() {
    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    val context = FakeUserContext()
    lateinit var factory: BaseUserRoleFactory
    lateinit var wrapper: UserUserRoleWrapper

    private var adminId: Long? = null
    private var userId: Long? = null

    @Before
    fun setUp() {
        // Initialize the factory and wrapper
        factory = BaseUserRoleFactory(
                userRoleRepo = userRoleRepo,
                userRepo = userRepo
        )

        wrapper = UserUserRoleWrapper(
                context = context,
                factory = factory,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        )

        // Persist a user and give them some roles
        val user = testUtil.createUser("cspath1@ycp.edu")
        testUtil.createUserRolesForUser(
                userId = user.id,
                role = UserRole.Role.STUDENT,
                isApproved = false
        )

        userId = user.id

        // Persist an admin user too
        val adminUser = testUtil.createUser("spathcody@gmail.com")
        testUtil.createUserRolesForUser(
                userId = user.id,
                role = UserRole.Role.ADMIN,
                isApproved = true
        )
        adminId = adminUser.id
    }

    @Test
    fun testUnapprovedList_NotLoggedIn_Failure() {
        val error = wrapper.unApprovedList(
                pageable = PageRequest.of(0, 5)
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles.containsAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER)))
    }

    @Test
    fun testUnapprovedList_UserNotAdmin_Failure() {
        // Log the user in as the student user
        context.login(userId!!)
        context.currentRoles.add(UserRole.Role.STUDENT)

        val error = wrapper.unApprovedList(
                pageable = PageRequest.of(0, 5)
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testUnapprovedList_Admin_Success() {
        // Log the user in as the student user
        context.login(adminId!!)
        context.currentRoles.add(UserRole.Role.ADMIN)

        var data: Page<UserRoleInfo> = PageImpl(arrayListOf())

        val error = wrapper.unApprovedList(
                pageable = PageRequest.of(0, 5)
        ) {
            assertNotNull(it.success)
            data = it.success!!
            assertNull(it.error)
        }

        assertNull(error)
        assertEquals(1, data.content.size)
    }
}