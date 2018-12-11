package com.radiotelescope.controller.admin.user

import com.radiotelescope.TestUtil
import com.radiotelescope.controller.user.BaseUserRestControllerTest
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.services.ses.MockAwsSesSendService
import liquibase.integration.spring.SpringLiquibase
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class AdminUserBanControllerTest : BaseUserRestControllerTest() {
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
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    private lateinit var adminUserBanController: AdminUserBanController
    private lateinit var admin: User
    private lateinit var user: User

    @Before
    override fun init() {
        super.init()

        adminUserBanController = AdminUserBanController(
                userRepo = userRepo,
                userWrapper = getWrapper(),
                logger = getLogger(),
                awsSesSendService = MockAwsSesSendService(true)
        )

        admin = testUtil.createUser("rpim1@ycp.edu")
        testUtil.createUserRolesForUser(
                userId = admin.id,
                role = UserRole.Role.ADMIN,
                isApproved = true
        )

        user = testUtil.createUser("rpim@ycp.edu")

    }

    @Test
    fun testSuccessResponse_BanMessage() {
        // Test the success scenario to ensure
        // the result object is correctly set

        // Simulate a login
        getContext().login(admin.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER))

        val result = adminUserBanController.execute(
                message = "You have misused our site",
                userId = user.id
        )

        assertNotNull(result)
        assertTrue(result.data is Long)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())
    }

    @Test
    fun testSuccessResponse_NoBanMessage() {
        // Test the success scenario to ensure
        // the result object is correctly set

        // Simulate a login
        getContext().login(admin.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER))

        val result = adminUserBanController.execute(
                message = null,
                userId = user.id
        )

        assertNotNull(result)
        assertTrue(result.data is Long)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())
    }

    @Test
    fun testFailedValidationResponse() {
        // Simulate a login
        getContext().login(admin.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER))

        val result = adminUserBanController.execute(
                message = "You have missed use our site",
                userId = 123456789
        )
        assertNotNull(result)
        assertNull(result.data)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertNotNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())
    }

    @Test
    fun testFailedAuthenticationResponse() {
        // Do not log the user in
        val result = adminUserBanController.execute(
                message = "You have missed use our site",
                userId = user.id
        )
        assertNotNull(result)
        assertNull(result.data)
        assertEquals(HttpStatus.FORBIDDEN, result.status)
        assertNotNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())
    }

}