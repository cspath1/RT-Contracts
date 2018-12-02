package com.radiotelescope.controller.user

import com.radiotelescope.TestUtil
import com.radiotelescope.contracts.user.UserInfo
import com.radiotelescope.repository.role.UserRole
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
internal class UserRetrieveControllerTest : BaseUserRestControllerTest() {
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

    private lateinit var userRetrieveController: UserRetrieveController

    private lateinit var user: User

    private val userContext = getContext()

    @Before
    override fun init() {
        super.init()

        user = testUtil.createUser("rpim@ycp.edu")

        // Simulate a login
        userContext.login(user.id)
        userContext.currentRoles.add(UserRole.Role.USER)

        userRetrieveController = UserRetrieveController(
                userWrapper = getWrapper(),
                logger = getLogger()
        )
    }

    @Test
    fun testSuccessResponse() {
        // Test the success scenario to ensure the result
        // object is correctly set
        val result = userRetrieveController.execute(user.id)

        assertNotNull(result)
        assertTrue(result.data is UserInfo)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)
    }

    @Test
    fun testFailedValidationResponse() {
        // Test the scenario where the validation
        // in the command object fails
        val result = userRetrieveController.execute(123456)

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.NOT_FOUND, result.status)
        assertEquals(1, result.errors!!.size)
    }

    @Test
    fun testValidForm_FailedAuthenticationResponse() {
        // Test the scenario where the authentication
        // in the wrapper fails

        // Simulate a log out
        userContext.logout()

        val result = userRetrieveController.execute(user.id)

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.FORBIDDEN, result.status)
        assertEquals(1, result.errors!!.size)
    }

}