package com.radiotelescope.controller.videoFile

import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.repository.videoFile.IVideoFileRepository
import com.radiotelescope.repository.videoFile.VideoFile
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner
import java.sql.Time
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedVideoFiles.sql"])
internal class VideoFileRetrieveControllerTest : BaseRestControllerTest() {
    @Autowired
    private lateinit var videoRepo: IVideoFileRepository

    private lateinit var videoFileRetrieveController: VideoFileRetrieveController
    private lateinit var videoFile: VideoFile

    @Before
    override fun init() {
        super.init()

        //videoFile.recordCreatedTimestamp = Date(2019, 11, 1, 0, 0, 0)
        videoFile = videoRepo.findAll().iterator().next()

        assertEquals(1, videoRepo.count())
    }

    @Test
    fun sampleTest() {
        assertEquals(1, videoRepo.count())

        assertEquals("\\Videos\\2.png", videoFile.thumbnailPath)
    }
}