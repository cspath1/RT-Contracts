package com.radiotelescope.controller.admin.frontpagePicture

import com.radiotelescope.controller.admin.frontpagePictures.AdminFrontpagePictureRetrieveListController
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
internal class AdminFrontpagePictureRetrieveListControllerTest : BaseFrontpagePictureRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var frontpagePictureRetrieveListController: AdminFrontpagePictureRetrieveListController

    private lateinit var user: User

    private lateinit var approvedPicture: FrontpagePicture
    private lateinit var deniedPicture: FrontpagePicture

    @Before
    override fun init() {
        super.init()

        frontpagePictureRetrieveListController = AdminFrontpagePictureRetrieveListController(
                frontpagePictureWrapper = getWrapper(),
                logger = getLogger()
        )

        user = testUtil.createUser("jhorne@ycp.edu")

        // Add two pictures to the database
        approvedPicture = testUtil.createFrontpagePicture("Approved Pic", "approved.jpg", "This picture is approved.", true)
        deniedPicture = testUtil.createFrontpagePicture("Denied Pic", "denied.jpg", "This picture is denied.", false)
    }

    @Test
    fun testSuccessResponse() {
        getContext().login(user.id)
        // Make user an admin
        getContext().currentRoles.add(UserRole.Role.ADMIN)
        testUtil.createUserRoleForUser(user, UserRole.Role.ADMIN, true)

        val result = frontpagePictureRetrieveListController.execute()

        assertNotNull(result)
        assertTrue(result.data is List<*>)
        assertEquals(2, (result.data as List<*>).count())
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        assertEquals(2, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testFailedAuthenticationResponse() {
        val result = frontpagePictureRetrieveListController.execute()

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