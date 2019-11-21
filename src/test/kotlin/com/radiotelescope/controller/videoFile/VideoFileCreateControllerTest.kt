package com.radiotelescope.controller.videoFile

import com.radiotelescope.controller.model.videoFile.CreateForm
import com.radiotelescope.repository.log.ILogRepository
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
internal class VideoFileCreateControllerTest : BaseVideoFileRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var videoFileCreateController: VideoFileCreateController

    private val baseForm = CreateForm(
            thumbnailPath = "security_footage.png",
            videoPath = "security_footage.mp4",
            videoLength = "01:00:00",
            token = "testid"
    )

    @Before
    override fun init() {
        super.init()

        videoFileCreateController = VideoFileCreateController(
                videoFileWrapper = getWrapper(),
                logger = getLogger()
        )

        videoFileCreateController.id = "testid"
    }

    @Test
    fun testSuccessResponse() {
        val result = videoFileCreateController.execute(baseForm)

        assertNotNull(result)
        assertTrue(result.data is Long)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log record has been created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testSuccessResponse_OverlongLength() {
        // Copy of the form with 60 hours as length
        val formCopy = baseForm.copy(
                videoLength = "60:00:00"
        )

        // Specifically tests Java Duration in relation to MySQL TIME
        val result = videoFileCreateController.execute(formCopy)

        assertNotNull(result)
        assertTrue(result.data is Long)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log record has been created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testInvalidFormResponse() {
        // Copy of the form with a null video length
        val formCopy = baseForm.copy(
                videoLength = null
        )

        val result = videoFileCreateController.execute(formCopy)

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertEquals(1, result.errors!!.size)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.BAD_REQUEST.value(), it.status)
        }
    }

    @Test
    fun testValidForm_FailedValidationResponse_BadId() {
        val formCopy = baseForm.copy(
                token = "nottestid"
        )

        val result = videoFileCreateController.execute(formCopy)

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertEquals(1, result.errors!!.size)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.BAD_REQUEST.value(), it.status)
        }
    }

    @Test
    fun testValidForm_FailedValidationResponse_ZeroLength() {
        // Copy of the form with a time of 00:00:00
        val formCopy = baseForm.copy(
                videoLength = "00:00:00"
        )

        val result = videoFileCreateController.execute(formCopy)

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertEquals(1, result.errors!!.size)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.BAD_REQUEST.value(), it.status)
        }
    }
}
