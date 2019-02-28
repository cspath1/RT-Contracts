package com.radiotelescope.controller.user

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
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
@ActiveProfiles("test")
internal class UserSearchControllerTest : BaseUserRestControllerTest() {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var userSearchController: UserSearchController

    private val userContext = getContext()

    @Before
    override fun init() {
        super.init()

        val user = testUtil.createUser("cspath1@ycp.edu")
        user.company = "York College of PA"
        userRepo.save(user)

        // Simulate a login
        userContext.login(user.id)
        userContext.currentRoles.add(UserRole.Role.USER)

        userSearchController = UserSearchController(
                userWrapper = getWrapper(),
                logger = getLogger()
        )
    }

    @Test
    fun testSuccessResponse_FirstNameAndLastName() {
        // Test the success response scenario to ensure the result
        // object is correctly set
        val result = userSearchController.execute(
                pageNumber = 0,
                pageSize = 15,
                search = "firstName+lastName",
                value = "First"
        )

        assertTrue(result.data is Page<*>)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testSuccessResponse_Email() {
        // Test the success response scenario to ensure the result
        // object is correctly set
        val result = userSearchController.execute(
                pageNumber = 0,
                pageSize = 15,
                search = "email",
                value = "ycp.edu"
        )

        assertNotNull(result)
        assertTrue(result.data is Page<*>)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testSuccessResponse_Company() {
        // Test the success response scenario to ensure the result
        // object is correctly set
        val result = userSearchController.execute(
                pageNumber = 0,
                pageSize = 15,
                search = "company",
                value = "York College"
        )

        assertNotNull(result)
        assertTrue(result.data is Page<*>)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testSuccessResponse_UnknownSearchParamIgnored() {
        // Test the success response scenario to ensure the result
        // object is correctly set
        val result = userSearchController.execute(
                pageNumber = 0,
                pageSize = 15,
                search = "firstName+username",
                value = "York College"
        )

        assertNotNull(result)
        assertTrue(result.data is Page<*>)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)
    }

    @Test
    fun testErrorResponse() {
        // Test the scenario where the business logic did not pass
        val result = userSearchController.execute(
                pageNumber = 0,
                pageSize = 15,
                search = "",
                value = "oeif"
        )

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertEquals(1, result.errors!!.size)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.BAD_REQUEST.value(), it.status)
        }
    }

    @Test
    fun testFailedAuthenticationResponse() {
        // Test the scenario where the authentication
        // in the wrapper fails

        // Simulate a logout
        userContext.logout()

        val result = userSearchController.execute(
                pageNumber = 0,
                pageSize = 15,
                search = "firstName",
                value = "eifnwoiefnwieo"
        )

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.FORBIDDEN, result.status)
        assertEquals(1, result.errors!!.size)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.FORBIDDEN.value(), it.status)
        }
    }
}