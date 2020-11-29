package com.radiotelescope.contracts.videoFile

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.videoFile.IVideoFileRepository
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
internal class BaseVideoFileFactoryTest : AbstractSpringTest() {
    @Autowired
    private lateinit var videoFileRepo: IVideoFileRepository

    private lateinit var factory: BaseVideoFileFactory

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
        factory = BaseVideoFileFactory(
                videoFileRepo = videoFileRepo
        )
    }

    @Test
    fun createVideoFile() {
        val cmd = factory.create(
                request = createRequest,
                uuid = "testid",
                profile = "LOCAL"
        )

        assertTrue(cmd is Create)
    }

    @Test
    fun retrieveListOfVideoFiles() {
        val cmd = factory.retrieveList(
                pageable = PageRequest.of(0, 10)
        )

        assertTrue(cmd is RetrieveList)
    }

    @Test
    fun listVideoFilesBetweenCreatedDates() {
        val cmd = factory.listBetweenCreationDates(
                request = listBetweenDatesRequest
        )

        assertTrue(cmd is ListBetweenCreationDates)
    }
}