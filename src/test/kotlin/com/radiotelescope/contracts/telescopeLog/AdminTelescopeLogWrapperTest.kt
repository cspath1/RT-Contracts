package com.radiotelescope.contracts.telescopeLog

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.telescopeLog.ITelescopeLogRepository
import com.radiotelescope.repository.telescopeLog.TelescopeLog
import com.radiotelescope.repository.user.User
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

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescopeLog.sql"])
internal class AdminTelescopeLogWrapperTest : AbstractSpringTest() {
    @Autowired
    private lateinit var telescopeLogRepo: ITelescopeLogRepository

    private lateinit var user: User
    private lateinit var telescopeLog: TelescopeLog

    private val context = FakeUserContext()
    private lateinit var factory: TelescopeLogFactory
    private lateinit var wrapper: AdminTelescopeLogWrapper

    @Before
    fun setUp() {
        // Persist a user
        user = testUtil.createUser("cspath1@ycp.edu")

        // Grab the telescope log
        telescopeLog = telescopeLogRepo.findAll().first()

        // Instantiate the factory
        factory = BaseTelescopeLogFactory(
                telescopeLogRepo = telescopeLogRepo
        )

        // Instantiate the wrapper
        wrapper = AdminTelescopeLogWrapper(
                context = context,
                factory = factory
        )
    }

    @Test
    fun testList_Admin_Success() {
        // Simulate a login as an admin
        context.login(user.id)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.ADMIN))

        val error = wrapper.list(
                pageable = PageRequest.of(0, 5)
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testList_NotLoggedIn_Failure() {
        // Do not log the user in
        val error = wrapper.list(
                pageable = PageRequest.of(0, 5)
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.containsAll(listOf(UserRole.Role.USER, UserRole.Role.ADMIN)))
    }

    @Test
    fun testList_NotAdmin_Failure() {
        // Log the user in as something other than an admin
        context.login(user.id)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.STUDENT))

        val error = wrapper.list(
                pageable = PageRequest.of(0, 5)
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testRetrieve_Admin_Success() {
        // Simulate a login as an admin
        context.login(user.id)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.ADMIN))

        val error = wrapper.retrieve(
                id = telescopeLog.getId()
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testRetrieve_NotLoggedIn_Failure() {
        // Do not log the user in
        val error = wrapper.retrieve(
                id = telescopeLog.getId()
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.containsAll(listOf(UserRole.Role.USER, UserRole.Role.ADMIN)))
    }

    @Test
    fun testRetrieve_NotAdmin_Failure() {
        // Log the user in as something other than
        context.login(user.id)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.STUDENT))

        val error = wrapper.retrieve(
                id = telescopeLog.getId()
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }
}