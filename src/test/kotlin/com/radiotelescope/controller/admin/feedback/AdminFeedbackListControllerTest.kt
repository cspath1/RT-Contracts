package com.radiotelescope.controller.admin.feedback

import com.radiotelescope.controller.feedback.BaseFeedbackRestControllerTest
import com.radiotelescope.repository.feedback.Feedback
import com.radiotelescope.repository.feedback.IFeedbackRepository
import com.radiotelescope.repository.log.ILogRepository
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

@DataJpaTest
@RunWith(SpringRunner::class)
internal class AdminFeedbackListControllerTest : BaseFeedbackRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var feedbackRepo: IFeedbackRepository

    private lateinit var admin: User
    private lateinit var adminFeedbackListController: AdminFeedbackListController

    @Before
    override fun init() {
        super.init()

        adminFeedbackListController = AdminFeedbackListController(
                feedbackWrapper = getWrapper(),
                logger = getLogger()
        )

        admin = testUtil.createUser("admin@ycpradiotelescope.com")
        testUtil.createUserRolesForUser(admin, UserRole.Role.ADMIN, true)

        // create several feedback objects
        feedbackRepo.save(Feedback("Feedback1", 10, "This website sucks."))
        feedbackRepo.save(Feedback("Feedback2", 10, "better than twitter"))
        feedbackRepo.save(Feedback("Feedback3", 1, "Everything is broken, please fix"))
    }

    @Test
    fun testSuccessResponse() {
        // Simulate a login
        getContext().login(admin.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER))

        val result = adminFeedbackListController.execute(
                pageNumber = 0,
                pageSize = 5
        )

        assertNotNull(result)
        assertTrue(result.data is Page<*>)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log record was created for each
        assertEquals(3, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testFailedRequiredFieldResponse() {
        // Simulate a login
        getContext().login(admin.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER))

        val result = adminFeedbackListController.execute(
                pageNumber = null,
                pageSize = null
        )

        assertNotNull(result)
        assertNull(result.data)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertNotNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.BAD_REQUEST.value(), it.status)
        }
    }

    @Test
    fun testFailedValidationResponse() {
        // Simulate a login
        getContext().login(admin.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER))

        val result = adminFeedbackListController.execute(
                pageNumber = -1,
                pageSize = 0
        )

        assertNotNull(result)
        assertNull(result.data)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertNotNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.BAD_REQUEST.value(), it.status)
        }
    }

    @Test
    fun testFailedAuthenticationResponse() {
        // Do not log the user in
        val result = adminFeedbackListController.execute(
                pageNumber = 0,
                pageSize = 5
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