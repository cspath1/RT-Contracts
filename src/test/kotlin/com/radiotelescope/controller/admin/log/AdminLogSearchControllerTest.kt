package com.radiotelescope.controller.admin.log

import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
internal class AdminLogSearchControllerTest : BaseLogRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var adminLogSearchController: AdminLogSearchController
    private lateinit var admin: User

    private val userContext = getContext()
    @Before
    override fun init() {
        super.init()

        adminLogSearchController = AdminLogSearchController(
                logWrapper = getWrapper(),
                logger = getLogger()
        )

        admin = testUtil.createUser("rpim@ycp.edu")
        testUtil.createUserRolesForUser(
                user = admin,
                role = UserRole.Role.ADMIN,
                isApproved = true
        )

        testUtil.createLog(
                user = admin,
                action = "Test",
                affectedRecordId = null,
                affectedTable = Log.AffectedTable.USER,
                timestamp = Date(System.currentTimeMillis()),
                isSuccess = true
        )
    }

    @Test
    fun testSuccessResponse_Action() {
        // Test the success scenario to ensure
        // the result object is correctly set

        // Simulate a login
        getContext().login(admin.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER))

        val result = adminLogSearchController.execute(
                pageNumber = 0,
                pageSize = 10,
                search = "action",
                value = "test"
        )

        assertTrue(result.data is Page<*>)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log record was created
        assertEquals(2, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testSuccessResponse_IsSuccess() {
        // Test the success scenario to ensure
        // the result object is correctly set

        // Simulate a login
        getContext().login(admin.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER))

        val result = adminLogSearchController.execute(
                pageNumber = 0,
                pageSize = 10,
                search = "isSuccess",
                value = true
        )

        assertTrue(result.data is Page<*>)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log record was created
        assertEquals(2, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testSuccessResponse_AffectedTable() {
        // Test the success scenario to ensure
        // the result object is correctly set

        // Simulate a login
        getContext().login(admin.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER))

        val result = adminLogSearchController.execute(
                pageNumber = 0,
                pageSize = 10,
                search = "affectedTable",
                value = Log.AffectedTable.USER
        )

        assertTrue(result.data is Page<*>)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log record was created
        assertEquals(2, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testSuccessResponse_Status() {
        // Test the success scenario to ensure
        // the result object is correctly set

        // Simulate a login
        getContext().login(admin.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER))

        val result = adminLogSearchController.execute(
                pageNumber = 0,
                pageSize = 10,
                search = "status",
                value = HttpStatus.OK.value()
        )

        assertTrue(result.data is Page<*>)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log record was created
        assertEquals(2, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testErrorResponse() {
        // Test the success scenario to ensure
        // the result object is correctly set

        // Simulate a login
        getContext().login(admin.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER))

        // Test the scenario where the business logic did not pass
        val result = adminLogSearchController.execute(
                pageNumber = 0,
                pageSize = 10,
                search = "something",
                value = "I no work"
        )

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertEquals(1, result.errors!!.size)

        assertEquals(logRepo.count(), 2)

        var logs = logRepo.findAll().toList()
        assertEquals(HttpStatus.BAD_REQUEST.value(), logs[1].status)
    }

    @Test
    fun testFailedAuthenticationResponse() {
        // Test the scenario where the authentication
        // in the wrapper fails

        // Don't Simulate a login

        val result = adminLogSearchController.execute(
                pageNumber = 0,
                pageSize = 10,
                search = "action",
                value = "I no work"
        )

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.FORBIDDEN, result.status)
        assertEquals(1, result.errors!!.size)

        assertEquals(logRepo.count(), 2)

        var logs = logRepo.findAll().toList()
        assertEquals(HttpStatus.FORBIDDEN.value(), logs[1].status)
    }

    @Test
    fun testInvalidPageParametersResponse() {
        // Test the scenario where the page parameters supplied
        // are invalid
        val result = adminLogSearchController.execute(
                pageNumber = -1,
                pageSize = -1,
                search = "firstName",
                value = "weibuwibeion"
        )

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertEquals(1, result.errors!!.size)

        assertEquals(logRepo.count(), 2)

        var logs = logRepo.findAll().toList()
        assertEquals(HttpStatus.BAD_REQUEST.value(), logs[1].status)

    }

}