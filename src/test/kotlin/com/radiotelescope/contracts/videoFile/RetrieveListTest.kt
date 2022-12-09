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

@DataJpaTest
@RunWith(SpringRunner::class)
internal class RetrieveListTest : AbstractSpringTest() {
    @Autowired
    private lateinit var videoFileRepo: IVideoFileRepository

    @Before
    fun init() {
        // persist two video file records
        testUtil.createVideoFileRecord("thumbnail1.png", "filename1.mp4", "01:00:00")
        testUtil.createVideoFileRecord("thumbnail2.png", "filename2.mp4", "01:00:00")
    }

    @Test
    fun testValidConstraints_Success() {
        val (page, error) = RetrieveList(
                pageable = PageRequest.of(0, 10),
                videoFileRepo = videoFileRepo
        ).execute()

        assertNotNull(page)
        assertNull(error)
        assertEquals(2, page!!.count())
    }
}