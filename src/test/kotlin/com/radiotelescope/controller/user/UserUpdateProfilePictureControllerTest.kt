package com.radiotelescope.controller.user

import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.services.s3.MockAwsS3DeleteService
import com.radiotelescope.services.s3.MockAwsS3UploadService
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class UserUpdateProfilePictureControllerTest : BaseUserRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var roleRepo: IUserRoleRepository

    private lateinit var userUpdateProfilePictureController: UserUpdateProfilePictureController

    private lateinit var admin: User
    private lateinit var user: User

    private lateinit var mockMultipartFile: MockMultipartFile

    @Before
    override fun init() {
        super.init()

        user = testUtil.createUser("jhorne@ycp.edu")
        admin = testUtil.createUser("admin@ycpradiotelescope.com")

        userUpdateProfilePictureController = UserUpdateProfilePictureController(
                userWrapper = getWrapper(),
                uploadService = MockAwsS3UploadService(true),
                deleteService = MockAwsS3DeleteService(true),
                context = getContext(),
                userRepo = userRepo,
                roleRepo = roleRepo,
                logger = getLogger()
        )

        mockMultipartFile = MockMultipartFile("user-file", "firstnamepic.jpg", "text/plain", "test data".toByteArray())
    }

    @Test
    fun testSuccessResponseUserOwnRecord() {
        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.add(UserRole.Role.USER)
        testUtil.createUserRoleForUser(user, UserRole.Role.USER, true)

        val result = userUpdateProfilePictureController.execute(
                userId = user.id,
                file = mockMultipartFile
        )

        assertNotNull(result)
        assertEquals(user.id, result.data)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)
        assertFalse(userRepo.findById(user.id).get().profilePictureApproved!!)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testFailedResponseUserOtherRecord() {
        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.add(UserRole.Role.USER)
        testUtil.createUserRoleForUser(user, UserRole.Role.USER, true)

        val result = userUpdateProfilePictureController.execute(
                userId = admin.id,
                file = mockMultipartFile
        )

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.FORBIDDEN, result.status)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.FORBIDDEN.value(), it.status)
        }
    }

    @Test
    fun testSuccessResponseAdminOwnRecord() {
        // Simulate an admin login
        getContext().login(admin.id)
        getContext().currentRoles.add(UserRole.Role.USER)
        testUtil.createUserRoleForUser(admin, UserRole.Role.USER, true)
        getContext().currentRoles.add(UserRole.Role.ADMIN)
        testUtil.createUserRoleForUser(admin, UserRole.Role.ADMIN, true)

        val result = userUpdateProfilePictureController.execute(
                userId = admin.id,
                file = mockMultipartFile
        )

        assertNotNull(result)
        assertEquals(admin.id, result.data)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)
        assertTrue(userRepo.findById(admin.id).get().profilePictureApproved!!)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testSuccessResponseAdminOtherRecord() {
        // Simulate an admin login
        getContext().login(admin.id)
        getContext().currentRoles.add(UserRole.Role.USER)
        testUtil.createUserRoleForUser(admin, UserRole.Role.USER, true)
        getContext().currentRoles.add(UserRole.Role.ADMIN)
        testUtil.createUserRoleForUser(admin, UserRole.Role.ADMIN, true)

        val result = userUpdateProfilePictureController.execute(
                userId = user.id,
                file = mockMultipartFile
        )

        assertNotNull(result)
        assertEquals(user.id, result.data)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)
        assertTrue(userRepo.findById(user.id).get().profilePictureApproved!!)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testFailedFileUpload() {
        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.add(UserRole.Role.USER)
        testUtil.createUserRoleForUser(user, UserRole.Role.USER, true)

        // Mock Upload Service set to fail
        userUpdateProfilePictureController = UserUpdateProfilePictureController(
                userWrapper = getWrapper(),
                uploadService = MockAwsS3UploadService(false),
                deleteService = MockAwsS3DeleteService(true),
                context = getContext(),
                userRepo = userRepo,
                roleRepo = roleRepo,
                logger = getLogger()
        )

        val result = userUpdateProfilePictureController.execute(
                userId = user.id,
                file = mockMultipartFile
        )

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.BAD_REQUEST.value(), it.status)
        }
    }

    @Test
    fun testFailedFileDeletion() {
        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.add(UserRole.Role.USER)
        testUtil.createUserRoleForUser(user, UserRole.Role.USER, true)

        user.profilePicture = "testprofilepic.jpg"
        userRepo.save(user)

        // Mock Delete Service set to fail
        userUpdateProfilePictureController = UserUpdateProfilePictureController(
                userWrapper = getWrapper(),
                uploadService = MockAwsS3UploadService(true),
                deleteService = MockAwsS3DeleteService(false),
                context = getContext(),
                userRepo = userRepo,
                roleRepo = roleRepo,
                logger = getLogger()
        )

        val result = userUpdateProfilePictureController.execute(
                userId = user.id,
                file = mockMultipartFile
        )

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.BAD_REQUEST.value(), it.status)
        }
    }

    @Test
    fun testFailedAuthenticationResponse() {
        // Do not log the user in
        val result = userUpdateProfilePictureController.execute(
                userId = user.id,
                file = mockMultipartFile
        )

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.FORBIDDEN, result.status)
        assertEquals(1, result.errors!!.size)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.FORBIDDEN.value(), it.status)
        }
    }
}