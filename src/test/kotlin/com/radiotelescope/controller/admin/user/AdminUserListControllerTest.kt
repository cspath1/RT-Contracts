package com.radiotelescope.controller.admin.user

import com.radiotelescope.TestUtil
import com.radiotelescope.controller.user.BaseUserRestControllerTest
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class AdminUserListControllerTest : BaseUserRestControllerTest() {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var admin: User
    private lateinit var adminUserListController: AdminUserListController

    @Before
    override fun init() {
        super.init()

        adminUserListController = AdminUserListController(
                userWrapper = getWrapper(),
                logger = getLogger()
        )

        admin = testUtil.createUser("rpim@ycp.edu")
        testUtil.createUserRolesForUser(
                user = admin,
                role = UserRole.Role.ADMIN,
                isApproved = true
        )

        val user = testUtil.createUser("rpim2@ycp.edu")
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )
    }

    @Test
    fun testSuccessResponse() {
        // Test the success scenario to ensure
        // the result object is correctly set

        // Simulate a login
        getContext().login(admin.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER))

        val result = adminUserListController.execute(
                pageNumber = 0,
                pageSize = 10
        )

        assertNotNull(result)
        assertTrue(result.data is Page<*>)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log record was created
        // Only one should exist since the admin user
        // should have been filtered out
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testFailedRequiredFieldResponse() {
        // Simulate a login
        getContext().login(admin.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER))

        val result = adminUserListController.execute(
                pageNumber = null,
                pageSize = null
        )
        assertNotNull(result)
        assertNull(result.data)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertNotNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.BAD_REQUEST.value(), it.status)
        }
    }

    @Test
    fun testFailedValidationResponse() {
        // Simulate a login
        getContext().login(admin.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER))

        val result = adminUserListController.execute(
                pageNumber = -1,
                pageSize = 0
        )
        assertNotNull(result)
        assertNull(result.data)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertNotNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.BAD_REQUEST.value(), it.status)
        }
    }

    @Test
    fun testFailedAuthenticationResponse() {
        // Do not log the user in
        val result = adminUserListController.execute(
                pageNumber = 0,
                pageSize = 10
        )
        assertNotNull(result)
        assertNull(result.data)
        assertEquals(HttpStatus.FORBIDDEN, result.status)
        assertNotNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.FORBIDDEN.value(), it.status)
        }
    }
}