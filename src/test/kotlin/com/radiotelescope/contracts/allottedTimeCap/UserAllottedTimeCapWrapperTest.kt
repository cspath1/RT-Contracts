package com.radiotelescope.contracts.allottedTimeCap

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
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
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
internal class UserAllottedTimeCapWrapperTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    private lateinit var baseUpdateRequest: Update.Request

    private val context = FakeUserContext()
    private lateinit var factory: BaseAllottedTimeCapFactory
    private lateinit var wrapper: UserAllottedTimeCapWrapper

    private var userId = -1L
    private var otherUserId = -2L

    @Before
    fun setup(){
        // Init factory and wrapper
        factory = BaseAllottedTimeCapFactory(
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )

        wrapper = UserAllottedTimeCapWrapper(
                context = context,
                factory = factory
        )

        val user = testUtil.createUser("lferree@ycp.edu")
        val otherUser = testUtil.createUser("cspath@ycp.edu")
        userId = user.id
        otherUserId = otherUser.id

        // Create Guest role for user and create 5 hour allotted time cap
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = 5*60*60*1000
        )

        baseUpdateRequest = Update.Request(
                userId = userId,
                allottedTime = 12345L
        )
    }

    @Test
    fun testUpdate_Admin_Success(){
        // Log the user in as an admin
        context.login(otherUserId)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.ADMIN))

        val error = wrapper.update(
                request = baseUpdateRequest
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testUpdate_NotAdmin_Failure() {
        // Log the user in as a base user
        context.login(otherUserId)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.update(
                request = baseUpdateRequest
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testUpdate_NotLoggedIn_Failure() {
        // Do not log the user in

        val error = wrapper.update(
                request = baseUpdateRequest
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

}