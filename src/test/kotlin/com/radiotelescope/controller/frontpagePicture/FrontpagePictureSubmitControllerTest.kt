package com.radiotelescope.controller.frontpagePicture

import com.radiotelescope.controller.model.frontpagePicture.SubmitForm
import com.radiotelescope.repository.frontpagePicture.FrontpagePicture
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.IUserRoleRepository
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
internal class FrontpagePictureSubmitControllerTest : BaseFrontpagePictureRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var roleRepo: IUserRoleRepository

    private lateinit var frontpagePictureSubmitController: FrontpagePictureSubmitController

    private lateinit var user: User

    private val pictureTitle = "Test Picture"
    private val pictureUrl = "test.jpg"
    private val description = "Test Description"

    @Before
    override fun init() {
        super.init()

        frontpagePictureSubmitController = FrontpagePictureSubmitController(
                frontpagePictureWrapper = getWrapper(),
                context = getContext(),
                roleRepo = roleRepo,
                logger = getLogger()
        )

        user = testUtil.createUser("jhorne@ycp.edu")
    }

    @Test
    fun testSuccessResponseAdmin() {
        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.add(UserRole.Role.USER)
        testUtil.createUserRoleForUser(user, UserRole.Role.USER, true)
        // Make the user an admin
        getContext().currentRoles.add(UserRole.Role.ADMIN)
        testUtil.createUserRoleForUser(user, UserRole.Role.ADMIN, true)

        val result = frontpagePictureSubmitController.execute(
                pictureTitle = pictureTitle,
                pictureUrl = pictureUrl,
                description = description
        )

        assertNotNull(result)
        assertTrue(result.data is FrontpagePicture)
        assertTrue((result.data as FrontpagePicture).approved)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testSuccessResponseUser() {
        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.add(UserRole.Role.USER)
        testUtil.createUserRoleForUser(user, UserRole.Role.USER, true)

        val result = frontpagePictureSubmitController.execute(
                pictureTitle = pictureTitle,
                pictureUrl = pictureUrl,
                description = description
        )

        assertNotNull(result)
        assertTrue(result.data is FrontpagePicture)
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
        // Do not log the user in
        val result = frontpagePictureSubmitController.execute(
                pictureTitle = pictureTitle,
                pictureUrl = pictureUrl,
                description = description
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