package com.radiotelescope.contracts.videoFile

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.videoFile.IVideoFileRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class CreateTest : AbstractSpringTest() {
    @Autowired
    private lateinit var videoFileRepo: IVideoFileRepository

    private val baseRequest = Create.Request(
            thumbnailPath = "thumbnail.png",
            videoPath = "video.mp4",
            videoLength = "01:00:00",
            token = "testid"
    )

    @Test
    fun testValidConstraints_Success() {
        val (id, error) = Create (
                request = baseRequest,
                videoFileRepo = videoFileRepo,
                uuid = "testid",
                profile = "LOCAL"
        ).execute()

        assertNotNull(id)
        assertNull(error)
    }

    @Test
    fun testInvalidConstraintsBadUUID_Failure() {
        val badRequest = baseRequest.copy(token = "nottestid")

        val (id, error) = Create (
                request = badRequest,
                videoFileRepo = videoFileRepo,
                uuid = "testid",
                profile = "LOCAL"
        ).execute()

        assertNull(id)
        assertNotNull(error)
    }

    @Test
    fun testInvalidConstraintsZeroLength_Failure() {
        val badRequest = baseRequest.copy(videoLength = "00:00:00")

        val (id, error) = Create (
                request = badRequest,
                videoFileRepo = videoFileRepo,
                uuid = "testid",
                profile = "LOCAL"
        ).execute()

        assertNull(id)
        assertNotNull(error)
    }

    @Test
    fun testInvalidConstraintsOverlongLength_Failure() {
        val badRequest = baseRequest.copy(videoLength = "01:00:0000")

        val (id, error) = Create (
                request = badRequest,
                videoFileRepo = videoFileRepo,
                uuid = "testid",
                profile = "LOCAL"
        ).execute()

        assertNull(id)
        assertNotNull(error)
    }

    @Test
    fun testInvalidConstraintsProduction_Failure() {
        val (id, error) = Create (
                request = baseRequest,
                videoFileRepo = videoFileRepo,
                uuid = "testid",
                profile = "PROD"
        ).execute()

        assertNull(id)
        assertNotNull(error)
    }
}