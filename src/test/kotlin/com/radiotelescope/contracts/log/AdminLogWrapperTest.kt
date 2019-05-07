package com.radiotelescope.contracts.log

import com.google.common.collect.HashMultimap
import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.model.log.Filter
import com.radiotelescope.repository.model.log.SearchCriteria
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.security.FakeUserContext
import com.radiotelescope.toStringMap
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
internal class AdminLogWrapperTest : AbstractSpringTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    private lateinit var user: User

    private lateinit var errorLog: Log

    private val context = FakeUserContext()
    private lateinit var factory: BaseLogFactory
    private lateinit var wrapper: AdminLogWrapper

    @Before
    fun setUp() {
        // Persist a user
        user = testUtil.createUser("cspath1@ycp.edu")

        // Persist a log
        testUtil.createLog(
                user = user,
                affectedRecordId = 1L,
                affectedTable = Log.AffectedTable.USER,
                action = "User Registered",
                timestamp = Date(),
                isSuccess = true
        )

        // Create an error map
        val errors = HashMultimap.create<ErrorTag, String>()

        errors.put(ErrorTag.FIRST_NAME, "First Name may not be blank")
        errors.put(ErrorTag.EMAIL, "Email is already in use")

        // Persist an error log
        errorLog = testUtil.createErrorLog(
                user = null,
                affectedRecordId = null,
                affectedTable = Log.AffectedTable.USER,
                action = "User Registration",
                timestamp = Date(),
                isSuccess = false,
                errors = errors.toStringMap()
        )

        factory = BaseLogFactory(
                logRepo = logRepo,
                userRepo = userRepo
        )

        wrapper = AdminLogWrapper(
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
                pageable = PageRequest.of(0,5)
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
                pageable = PageRequest.of(0, 10)
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testList_NotAdmin_Failure() {
        // Log the user in as something other than an admin
        context.login(user.id)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.STUDENT))

        val error = wrapper.list(
                pageable = PageRequest.of(0, 10)
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testRetrieveErrors_Admin_Success() {
        // Simulate a login as an admin
        context.login(user.id)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.ADMIN))

        val error = wrapper.retrieveErrors(
                logId = errorLog.id
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testRetrieveErrors_NotLoggedIn_Failure() {
        // Do not log the user in

        val error = wrapper.retrieveErrors(
                logId = errorLog.id
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testRetrieveErrors_NotAdmin_Failure() {
        // Log the user in as something other than an admin
        context.login(user.id)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.STUDENT))

        val error = wrapper.retrieveErrors(
                logId = errorLog.id
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testSearch_NotLoggedIn_Failure() {
        // Do not log the user in

        val error = wrapper.search(
                searchCriteria = listOf(SearchCriteria(Filter.ACTION, "Test")),
                pageable = PageRequest.of(0, 25)
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testSearch_NotAdmin_Failure() {
        // Log the user in as something other than an admin
        context.login(user.id)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.STUDENT))

        val error = wrapper.search(
                searchCriteria = listOf(SearchCriteria(Filter.ACTION, "Test")),
                pageable = PageRequest.of(0, 25)
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testSearch_Admin_Success() {
        // Simulate a login as an admin
        context.login(user.id)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.ADMIN))

        val error = wrapper.search(
                searchCriteria = listOf(SearchCriteria(Filter.ACTION, "Test")),
                pageable = PageRequest.of(0, 25)
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }
}