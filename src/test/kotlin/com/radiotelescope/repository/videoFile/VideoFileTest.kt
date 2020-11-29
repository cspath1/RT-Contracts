package com.radiotelescope.repository.videoFile

import com.radiotelescope.AbstractSpringTest
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
internal class VideoFileTest : AbstractSpringTest() {
    @Autowired
    private lateinit var videoFileRepo: IVideoFileRepository

    private lateinit var startDate: Date
    private lateinit var endDate: Date
    private lateinit var videoFile: VideoFile

    @Before
    fun setUp() {
        // start is current date
        startDate = Date()

        // insert video file record between start and end date
        videoFile = testUtil.createVideoFileRecord("thumbnail.png", "filename.png", "00:00:10")

        endDate = Date()
    }

    @Test
    fun testFindVideosBetweenCreatedDates() {
        // select between the start and end date
        val videos: List<VideoFile> = videoFileRepo.findVideosCreatedBetweenDates(startDate, endDate)

        assertNotNull(videos)
    }
}