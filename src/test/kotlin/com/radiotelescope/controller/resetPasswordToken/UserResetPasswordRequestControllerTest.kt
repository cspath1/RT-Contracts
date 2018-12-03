package com.radiotelescope.controller.resetPasswordToken

import com.radiotelescope.TestUtil
import com.radiotelescope.controller.model.Profile
import com.radiotelescope.repository.log.ILogRepository
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
internal class UserResetPasswordRequestControllerTest : BaseResetPasswordTokenControllerTest() {
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

    private lateinit var userResetPasswordRequestController: UserResetPasswordRequestController

    private lateinit var email: String

    @Before
    override fun init() {
        super.init()

        userResetPasswordRequestController = UserResetPasswordRequestController(
                resetPasswordTokenWrapper = getWrapper(),
                profile = Profile.LOCAL,
                awsSesSendService = MockAwsSesSendService(true),
                logger = getLogger()
        )

        val user = testUtil.createUser("cspath1@ycp.edu")
        email = user.email
    }

    @Test
    fun testSuccessResponse() {
        // Test the success scenario to ensure the
        // result object is correctly set
        val result = userResetPasswordRequestController.execute(email)

        assertNotNull(result)
        assertTrue(result.data is String)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())
    }

    @Test
    fun testInvalidEmailResponse() {
        // Test the scenario where the email entered does
        // not exist
        val result = userResetPasswordRequestController.execute("spathcody@gmail.com")

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertEquals(1, result.errors!!.size)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())
    }
}