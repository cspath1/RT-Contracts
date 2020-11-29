package com.radiotelescope.controller.admin.user

import com.radiotelescope.controller.user.BaseUserRestControllerTest
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
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
internal class AdminUserApproveDenyProfilePictureControllerTest : BaseUserRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    private lateinit var userApproveDenyProfilePictureController: AdminUserApproveDenyProfilePictureController

    private lateinit var user: User
    private lateinit var admin: User

    @Before
    override fun init() {
        super.init()

        userApproveDenyProfilePictureController = AdminUserApproveDenyProfilePictureController(
                userWrapper = getWrapper(),
                logger = getLogger()
        )

        user = testUtil.createUser("jhorne@ycp.edu")
        admin = testUtil.createUser("admin@ycpradiotelescope.com")

        user.profilePicture = "testpic.jpg"
        user.profilePictureApproved = false
        userRepo.save(user)
    }

    @Test
    fun testApproveSuccessResponse() {
        getContext().login(admin.id)
        // Make user an admin
        getContext().currentRoles.add(UserRole.Role.ADMIN)
        testUtil.createUserRoleForUser(admin, UserRole.Role.ADMIN, true)

        val result = userApproveDenyProfilePictureController.execute(
                userId = user.id,
                isApprove = true
        )

        assertNotNull(result)
        assertTrue(result.data is User)
        assertTrue((result.data as User).profilePictureApproved!!)
        assertNotNull((result.data as User).profilePicture)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testDenySuccessResponse() {
        getContext().login(admin.id)
        // Make user an admin
        getContext().currentRoles.add(UserRole.Role.ADMIN)
        testUtil.createUserRoleForUser(admin, UserRole.Role.ADMIN, true)

        val result = userApproveDenyProfilePictureController.execute(
                userId = user.id,
                isApprove = false
        )

        assertNotNull(result)
        assertTrue(result.data is User)
        assertNull((result.data as User).profilePictureApproved)
        assertNull((result.data as User).profilePicture)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testImproperRoleResponse() {
        getContext().login(user.id)
        getContext().currentRoles.add(UserRole.Role.USER)
        testUtil.createUserRoleForUser(user, UserRole.Role.USER, true)

        val result = userApproveDenyProfilePictureController.execute(
                userId = user.id,
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

    @Test
    fun testFailedAuthenticationResponse() {
        // Do not log the user in
        val result = userApproveDenyProfilePictureController.execute(
                userId = user.id,
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