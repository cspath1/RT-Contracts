package com.radiotelescope.controller.user.role

import com.radiotelescope.services.ses.MockAwsSesSendService
import com.radiotelescope.TestUtil
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
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
@ActiveProfiles("test")
internal class RoleRequestControllerTest : BaseUserRoleControllerTest() {
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

    private lateinit var userRoleRequestController: RoleRequestController

    private lateinit var user: User

    private val userContext = getContext()

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Before
    override fun init() {
        super.init()

        user = testUtil.createUserWithEncodedPassword(
                email = "rpim@ycp.edu",
                password = "Password",
                accountHash = "Test Account"
        )
        // Simulate a login
        userContext.login(user.id)
        userContext.currentRoles.add(UserRole.Role.USER)

        userRoleRequestController = RoleRequestController(
                roleWrapper = getWrapper(),
                userRepo = userRepo,
                awsSesSendService = MockAwsSesSendService(true),
                logger = getLogger()
        )
    }

    @Test
    fun testSuccessResponse() {
        // Test the success scenario to ensure the result
        // object is correctly set
        val result = userRoleRequestController.execute(
                role = UserRole.Role.MEMBER,
                userId = user.id
        )

        assertNotNull(result)
        assertNotNull(result.data)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)
    }

    @Test
    fun testValidForm_FailedValidationResponse() {
        // Test the scenario where the form is valid,
        // but validation in the command object fails
        val result = userRoleRequestController.execute(
                role = UserRole.Role.ADMIN,
                userId = user.id
        )

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertEquals(1, result.errors!!.size)
    }

    @Test
    fun testValidForm_FailedAuthenticationResponse() {
        // Test the scenario where the form is valid,
        // but the authentication in the wrapper fails

        // Simulate a log out
        userContext.logout()

        val result = userRoleRequestController.execute(
                role = UserRole.Role.MEMBER,
                userId = user.id
        )

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.FORBIDDEN, result.status)
        assertEquals(1, result.errors!!.size)
    }
}