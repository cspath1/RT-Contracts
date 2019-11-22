package com.radiotelescope.controller.videoFile


import com.radiotelescope.controller.model.videoFile.ListBetweenCreationDatesForm
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.User
import org.springframework.http.HttpStatus
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
//@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedVideoFiles.sql"])
internal class VideoFileRetrieveControllerTest : BaseVideoFileRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var videoFileRetrieveController: VideoFileListBetweenCreationDatesController

    private lateinit var user: User

    private val baseForm = ListBetweenCreationDatesForm(
            lowerDate = Date(System.currentTimeMillis() - 100000L),
            upperDate = Date(System.currentTimeMillis() + 100000L)
    )

    private val invalidForm = ListBetweenCreationDatesForm(
            lowerDate = Date(System.currentTimeMillis() + 100000L),
            upperDate = Date(System.currentTimeMillis() - 100000L)
    )

    @Before
    override fun init() {
        super.init()

        videoFileRetrieveController = VideoFileListBetweenCreationDatesController(
                videoFileWrapper = getWrapper(),
                logger = getLogger()
        )

        user = testUtil.createUser("samplestudent@ycp.edu")

        testUtil.createVideoFileRecord("vid1.png", "vid1.mp4", "01:00:00")
        testUtil.createVideoFileRecord("vid2.png", "vid2.mp4", "01:01:00")
    }

    @Test
    fun testSuccessResponse() {
        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.add(UserRole.Role.ADMIN)

        val result = videoFileRetrieveController.execute(baseForm)

        assertNotNull(result)
        assertTrue(result.data is List<*>)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        assertEquals(2, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testInvalidFormResponse() {
        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.add(UserRole.Role.ADMIN)

        val result = videoFileRetrieveController.execute(invalidForm)

        assertNotNull(result)
        assertNull(result.data)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertNotNull(result.errors)

        // Ensure a log was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.BAD_REQUEST.value(), it.status)
        }
    }

    @Test
    fun testFailedAuthenticationResponse() {
        // Do not log the user in
        val result = videoFileRetrieveController.execute(baseForm)

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