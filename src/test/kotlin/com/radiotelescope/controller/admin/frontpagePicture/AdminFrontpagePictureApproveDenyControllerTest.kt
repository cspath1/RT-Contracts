package com.radiotelescope.controller.admin.frontpagePicture

import com.radiotelescope.controller.admin.AdminFrontpagePictureApproveDenyController
import com.radiotelescope.controller.frontpagePicture.BaseFrontpagePictureRestControllerTest
import com.radiotelescope.repository.frontpagePicture.FrontpagePicture
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
internal class AdminFrontpagePictureApproveDenyControllerTest : BaseFrontpagePictureRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var frontpagePictureApproveDenyController: AdminFrontpagePictureApproveDenyController

    private lateinit var user: User

    private lateinit var theFrontpagePicture: FrontpagePicture

    @Before
    override fun init() {
        super.init()

        frontpagePictureApproveDenyController = AdminFrontpagePictureApproveDenyController(
                frontpagePictureWrapper = getWrapper(),
                context = getContext(),
                logger = getLogger()
        )

        user = testUtil.createUser("jhorne@ycp.edu")

        theFrontpagePicture = testUtil.createFrontpagePicture(
                picture = "testpic.jpg",
                description = "Test Description",
                approved = false
        )
    }

    @Test
    fun testApproveSuccessResponse() {
        getContext().login(user.id)
        // Make user an admin
        getContext().currentRoles.add(UserRole.Role.ADMIN)
        testUtil.createUserRoleForUser(user, UserRole.Role.ADMIN, true)

        val result = frontpagePictureApproveDenyController.execute(
                frontpagePictureId = theFrontpagePicture.id,
                isApprove = true
        )

        assertNotNull(result)
        assertTrue(result.data is FrontpagePicture)
        // Returned model updated to true
        assertTrue((result.data as FrontpagePicture).approved)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testDenySuccessResponse() {
        getContext().login(user.id)
        // Make user an admin
        getContext().currentRoles.add(UserRole.Role.ADMIN)
        testUtil.createUserRoleForUser(user, UserRole.Role.ADMIN, true)

        val result = frontpagePictureApproveDenyController.execute(
                frontpagePictureId = theFrontpagePicture.id,
                isApprove = false
        )

        assertNotNull(result)
        assertTrue(result.data is FrontpagePicture)
        // Returned object updated to false, but deleted
        assertFalse((result.data as FrontpagePicture).approved)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testFailedAuthenticationResponse() {
        val result = frontpagePictureApproveDenyController.execute(
                frontpagePictureId = theFrontpagePicture.id,
                isApprove = true
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