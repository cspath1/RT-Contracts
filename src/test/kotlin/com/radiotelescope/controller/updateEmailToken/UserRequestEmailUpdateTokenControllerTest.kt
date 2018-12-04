package com.radiotelescope.controller.updateEmailToken

import com.radiotelescope.TestUtil
import com.radiotelescope.controller.model.Profile
import com.radiotelescope.controller.model.updateEmailToken.UpdateForm
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.UserRole
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
internal class UserRequestEmailUpdateTokenControllerTest : BaseUpdateEmailTokenRestControllerTest() {
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

    private lateinit var userRequestEmailUpdateTokenController: UserRequestEmailUpdateTokenController
    private lateinit var user: User

    private val baseForm = UpdateForm(
            email = "spathcody@gmail.com",
            emailConfirm = "spathcody@gmail.com"
    )

    @Before
    override fun init() {
        super.init()

        userRequestEmailUpdateTokenController = UserRequestEmailUpdateTokenController(
                updateEmailTokenWrapper = getWrapper(),
                profile = Profile.LOCAL,
                awsSesSendService = MockAwsSesSendService(true),
                logger = getLogger()
        )

        user = testUtil.createUser("cspath1@ycp.edu")
    }

    @Test
    fun testSuccessResponse() {
        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.add(UserRole.Role.USER)

        val result = userRequestEmailUpdateTokenController.execute(
                userId = user.id,
                form = baseForm
        )

        assertNotNull(result)
        assertTrue(result.data is String)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())
    }

    @Test
    fun testInvalidFormResponse() {
        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.add(UserRole.Role.USER)

        val formCopy = baseForm.copy(
                emailConfirm = null
        )

        val result = userRequestEmailUpdateTokenController.execute(
                userId = user.id,
                form = formCopy
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
    fun testValidForm_FailedValidationResponse() {
        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.add(UserRole.Role.USER)

        // Create a user with the email we want to change to
        testUtil.createUser("spathcody@gmail.com")

        val result = userRequestEmailUpdateTokenController.execute(
                userId = user.id,
                form = baseForm
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
    fun testFailedAuthenticationResponse() {
        // Do not log the user in
        val result = userRequestEmailUpdateTokenController.execute(
                userId = user.id,
                form = baseForm
        )

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.FORBIDDEN, result.status)
        assertEquals(1, result.errors!!.size)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())
    }
}