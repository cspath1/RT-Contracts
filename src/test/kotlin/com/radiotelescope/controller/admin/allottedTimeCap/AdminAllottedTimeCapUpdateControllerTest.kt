package com.radiotelescope.controller.admin.allottedTimeCap

import com.radiotelescope.controller.allottedTimeCap.BaseAllottedTimeCapRestControllerTest
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class AdminAllottedTimeCapUpdateControllerTest : BaseAllottedTimeCapRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var adminAllottedTimeCapUpdateController: AdminAllottedTimeCapUpdateController
    private lateinit var admin: User
    private lateinit var user: User

    @Before
    override fun init(){
        super.init()

        admin = testUtil.createUser("lferree@ycp.edu")
        user = testUtil.createUser("rpim@ycp.edu")

        adminAllottedTimeCapUpdateController = AdminAllottedTimeCapUpdateController(
                allottedTimeCapWrapper = getWrapper(),
                logger = getLogger()
        )

    }

    @Test
    fun testSuccess_Response(){
        // Test the success scenario to ensure
        // the result object is correctly set

        // Give user Guest Role and 5 hour time cap (as they would have for an update)
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = 5*60*60*1000
        )

        // Simulate a login
        getContext().login(admin.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER))

        val result = adminAllottedTimeCapUpdateController.execute(
                userId = user.id,
                allottedTime = 1L
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

        val result = adminAllottedTimeCapUpdateController.execute(
                userId = 123456789,
                allottedTime = -1L
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
        val result = adminAllottedTimeCapUpdateController.execute(
                userId = user.id,
                allottedTime = 1L
        )
        assertNotNull(result)
        assertNull(result.data)
        assertEquals(HttpStatus.FORBIDDEN, result.status)
        assertNotNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())
    }

}