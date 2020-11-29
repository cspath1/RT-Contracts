package com.radiotelescope.controller.frontpagePicture

import com.radiotelescope.repository.frontpagePicture.FrontpagePicture
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.User
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class FrontpagePictureRetrieveApprovedControllerTest : BaseFrontpagePictureRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var frontpagePictureRetrieveApprovedController: FrontpagePictureRetrieveApprovedController

    private lateinit var user: User

    private lateinit var approvedPicture: FrontpagePicture
    private lateinit var deniedPicture: FrontpagePicture

    @Before
    override fun init() {
        super.init()

        frontpagePictureRetrieveApprovedController = FrontpagePictureRetrieveApprovedController(
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

        val result = frontpagePictureRetrieveApprovedController.execute()

        Assert.assertNotNull(result)
        Assert.assertTrue(result.data is List<*>)
        Assert.assertEquals(1, (result.data as List<*>).count())
        Assert.assertEquals(HttpStatus.OK, result.status)
        Assert.assertNull(result.errors)

        Assert.assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            Assert.assertEquals(HttpStatus.OK.value(), it.status)
        }
    }
}