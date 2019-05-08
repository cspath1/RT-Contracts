package com.radiotelescope.controller.admin.telescopeLog

import com.radiotelescope.contracts.telescopeLog.TelescopeLogInfo
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.telescopeLog.ITelescopeLogRepository
import com.radiotelescope.repository.telescopeLog.TelescopeLog
import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescopeLog.sql"])
internal class AdminTelescopeLogRetrieveControllerTest : BaseTelescopeLogRestControllerTest() {
    @Autowired
    private lateinit var telescopeLogRepo: ITelescopeLogRepository

    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var adminTelescopeLogRetrieveController: AdminTelescopeLogRetrieveController

    private lateinit var user: User
    private lateinit var telescopeLog: TelescopeLog

    @Before
    override fun init() {
        super.init()

        adminTelescopeLogRetrieveController = AdminTelescopeLogRetrieveController(
                telescopeLogWrapper = getWrapper(),
                logger = getLogger()
        )

        user = testUtil.createUser("cspath1@ycp.edu")

        telescopeLog = telescopeLogRepo.findAll().first()
    }

    @Test
    fun testSuccessResponse() {
        // Test the success scenario to ensure the
        // result object is correctly set

        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.ADMIN))

        val result = adminTelescopeLogRetrieveController.execute(
                telescopeLogId = telescopeLog.getId()
        )

        assertNotNull(result)
        assertTrue(result.data is TelescopeLogInfo)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)
    }

    @Test
    fun testFailedValidationResponse() {
        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.ADMIN))

        val result = adminTelescopeLogRetrieveController.execute(
                telescopeLogId = 311L
        )

        assertNotNull(result)
        assertNull(result.data)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertNotNull(result.errors)
    }

    @Test
    fun testFaileAuthenticationResponse() {
        // Do not log the user in
        val result = adminTelescopeLogRetrieveController.execute(
                telescopeLogId = telescopeLog.getId()
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