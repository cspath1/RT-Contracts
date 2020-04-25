package com.radiotelescope.contracts.videoFile

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.User
import com.radiotelescope.repository.videoFile.IVideoFileRepository
import com.radiotelescope.security.FakeUserContext
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
internal class UserVideoFileWrapperTest : AbstractSpringTest() {
    @Autowired
    private lateinit var videoFileRepo: IVideoFileRepository

    private lateinit var admin: User

    val context = FakeUserContext()
    lateinit var factory: VideoFileFactory
    lateinit var wrapper: UserVideoFileWrapper

    private val createRequest = Create.Request(
            thumbnailPath = "thumbnail.png",
            videoPath = "video.mp4",
            videoLength = "01:00:00",
            token = "testid"
    )

    private val listBetweenDatesRequest = ListBetweenCreationDates.Request(
            lowerDate = Date(System.currentTimeMillis()),
            upperDate = Date(System.currentTimeMillis() + 60000L)
    )

    @Before
    fun init() {
        // Initialize the factory and wrapper
        factory = BaseVideoFileFactory(
                videoFileRepo = videoFileRepo
        )

        wrapper = UserVideoFileWrapper(
                context = context,
                factory = factory
        )

        // Create admin with default roles
        admin = testUtil.createUser("admin@ycpradiotelescope.com")
        testUtil.createUserRoleForUser(admin, UserRole.Role.ADMIN, true)
    }

    @Test
    fun create_Success() {
        val result = wrapper.create(
                request = createRequest,
                uuid = "testid",
                profile = "LOCAL"
        )

        assertNotNull(result)
    }

    @Test
    fun adminRetrieveList_Success() {
        context.login(admin.id)
        context.currentRoles.add(UserRole.Role.USER)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.retrieveList(
                pageable = PageRequest.of(0, 10)
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun retrieveList_NotLoggedIn_Failure() {
        // Do not log the user in
        val error = wrapper.retrieveList(
                pageable = PageRequest.of(0, 10)
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
    }

    @Test
    fun adminListBetweenCreationDates_Success() {
        context.login(admin.id)
        context.currentRoles.add(UserRole.Role.USER)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.listBetweenCreationDates(
                request = listBetweenDatesRequest
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun listBetweenCreationDates_NotLoggedIn_Failure() {
        // Do not log the user in
        val error = wrapper.listBetweenCreationDates(
                request = listBetweenDatesRequest
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
    }
}