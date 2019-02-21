package com.radiotelescope.controller.user

import com.radiotelescope.TestUtil
import com.radiotelescope.controller.model.Profile
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
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
internal class UserInviteControllerTest: BaseUserRestControllerTest() {
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

    private lateinit var userInviteController: UserInviteController

    private val userContext = getContext()

    private var id: Long = 1L

    @Before
    override fun init() {
        super.init()

        val user = testUtil.createUser(
                email = "lferree@ycp.edu",
                accountHash = "Test Account 1"
        )
        id = user.id
        userRepo.save(user)

        // Simulate a login
        userContext.login(user.id)
        userContext.currentRoles.add(UserRole.Role.USER)

        userInviteController = UserInviteController(
                userWrapper = getWrapper(),
                profile = Profile.LOCAL,
                userRepo = userRepo,
                awsSesSendService = MockAwsSesSendService(true),
                logger = getLogger()
        )
    }

    @Test
    fun testSuccessResponse() {
        // Test the success scenario to ensure the result
        // object is correctly set
        val result = userInviteController.execute(
                id = id,
                email = "test@email.mail"
        )

        assertNotNull(result)
        assertEquals(true, result.data)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())
    }

    @Test
    fun testFailedValidationResponse() {
        // Test the scenario where the form's validate
        // request method fails
        val result = userInviteController.execute(
                id = id,
                email = ""
        )

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertEquals(1, result.errors!!.size)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())
    }

    @Test
    fun testValidForm_FailedAuthenticationResponse() {
        // Test the scenario where the authentication
        // in the wrapper fails

        // Simulate a log out
        userContext.logout()

        val result = userInviteController.execute(
                id = id,
                email = "test@email.mail"
        )

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.FORBIDDEN, result.status)
        assertEquals(1, result.errors!!.size)
    }
}