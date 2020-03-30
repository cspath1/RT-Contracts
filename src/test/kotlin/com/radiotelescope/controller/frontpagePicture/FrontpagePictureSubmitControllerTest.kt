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

    private val picture = "test.jpg"
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
        getContext().currentRoles.add(UserRole.Role.ADMIN)

        val result = frontpagePictureSubmitController.execute(
                picture = picture,
                description = description
        )

        assertNotNull(result)
        print(result.data)
        assertTrue(result.data is FrontpagePicture)
        assertTrue((result.data as FrontpagePicture).approved)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }
}