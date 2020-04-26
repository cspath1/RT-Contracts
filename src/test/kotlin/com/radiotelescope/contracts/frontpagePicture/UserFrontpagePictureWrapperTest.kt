package com.radiotelescope.contracts.frontpagePicture

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.frontpagePicture.FrontpagePicture
import com.radiotelescope.repository.frontpagePicture.IFrontpagePictureRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.security.FakeUserContext
import com.radiotelescope.services.s3.MockAwsS3DeleteService
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class UserFrontpagePictureWrapperTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var frontpagePictureRepo: IFrontpagePictureRepository

    private lateinit var user: User
    private lateinit var admin: User

    private lateinit var theFrontpagePicture: FrontpagePicture
    private lateinit var submitRequest: Submit.Request
    private lateinit var approveDenyRequest: ApproveDeny.Request

    val context = FakeUserContext()
    lateinit var factory: FrontpagePictureFactory
    lateinit var wrapper: UserFrontpagePictureWrapper

    @Before
    fun init() {
        // Initialize the factory and wrapper
        factory = BaseFrontpagePictureFactory(
                frontpagePictureRepo = frontpagePictureRepo,
                s3DeleteService = MockAwsS3DeleteService(true)
        )

        wrapper = UserFrontpagePictureWrapper(
                context = context,
                factory = factory
        )

        // Create user and admin with default roles
        user = testUtil.createUser("jhorne@ycp.edu")
        testUtil.createUserRoleForUser(user, UserRole.Role.USER, true)

        admin = testUtil.createUser("admin@ycpradiotelescope.com")
        testUtil.createUserRoleForUser(admin, UserRole.Role.ADMIN, true)

        theFrontpagePicture = testUtil.createFrontpagePicture(
                pictureTitle = "Test Picture",
                pictureUrl = "testpic.jpg",
                description = "The Test Frontpage Picture",
                approved = false
        )

        submitRequest = Submit.Request(
                pictureTitle = "Test Picture",
                pictureUrl = "testpic.jpg",
                description = "The Test Frontpage Picture",
                approved = false
        )

        approveDenyRequest = ApproveDeny.Request(
                frontpagePictureId = theFrontpagePicture.id,
                isApprove = true
        )
    }

    @Test
    fun userSubmit_Success() {
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.submit(
                request = submitRequest
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun submit_NotLoggedIn_Failure() {
        // Do not log the user in
        val error = wrapper.submit(
                request = submitRequest
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
    }

    @Test
    fun adminRetrieveList_Success() {
        context.login(admin.id)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.retrieveList {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun alumnusRetrieveList_Success() {
        context.login(admin.id)
        context.currentRoles.add(UserRole.Role.ALUMNUS)

        val error = wrapper.retrieveList {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun userRetrieveList_Failure() {
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.retrieveList {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
    }

    @Test
    fun retrieveList_NotLoggedIn_Failure() {
        // Do not log the user in
        val error = wrapper.retrieveList {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
    }

    @Test
    fun adminApproveDeny_Success() {
        context.login(admin.id)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.approveDeny(
                request = approveDenyRequest
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun userApproveDeny_Failure() {
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.approveDeny(
                request = approveDenyRequest
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
    }

    @Test
    fun approveDeny_NotLoggedIn_Failure() {
        // Do not log the user in
        val error = wrapper.approveDeny(
                request = approveDenyRequest
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
    }

    @Test
    fun retrieveApproved_Success() {
        val result = wrapper.retrieveApproved()

        assertNotNull(result)
    }
}