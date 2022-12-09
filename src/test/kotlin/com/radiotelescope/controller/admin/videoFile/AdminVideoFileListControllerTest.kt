package com.radiotelescope.controller.admin.videoFile

import com.radiotelescope.controller.videoFile.BaseVideoFileRestControllerTest
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.User
import com.radiotelescope.repository.videoFile.VideoFile
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
internal class AdminVideoFileListControllerTest : BaseVideoFileRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var adminVideoFileListController: AdminVideoFileListController
    private lateinit var admin: User
    private lateinit var user: User

    @Before
    override fun init() {
        super.init()

        adminVideoFileListController = AdminVideoFileListController(
                videoFileWrapper = getWrapper(),
                logger = getLogger()
        )

        admin = testUtil.createUser("admin@ycp.edu")
        testUtil.createUserRolesForUser(
                user = admin,
                role = UserRole.Role.ADMIN,
                isApproved = true
        )

        user = testUtil.createUser("user@ycp.edu")
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.USER,
                isApproved = true
        )

        testUtil.createVideoFileRecord("vid1.png", "vid1.mp4", "01:00:00")
        testUtil.createVideoFileRecord("vid2.png", "vid2.mp4", "01:01:00")
    }

    @Test
    fun testSuccessResponse() {
        // Simulate a login
        getContext().login(admin.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER))

        val result = adminVideoFileListController.execute(
                pageNumber = 0,
                pageSize = 10
        )

        assertNotNull(result)
        assertTrue(result.data is Page<*>)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Test that page is sorted by latest input
        assertEquals(VideoFile("vid2.png", "vid2.mp4", "01:01:00"), (result.data as Page<*>).content[0])

        // Ensure a log record was created
        assertEquals(2, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testFailedRequiredFieldResponse() {
        // Simulate a login
        getContext().login(admin.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.ADMIN, UserRole.Role.USER))

        val result = adminVideoFileListController.execute(
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
        val result = adminVideoFileListController.execute(
                pageNumber = 0,
                pageSize = 10
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

    @Test
    fun testFailedAuthenticationResponse_notAdmin() {
        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.USER))

        val result = adminVideoFileListController.execute(
                pageNumber = 0,
                pageSize = 10
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