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
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
internal class ListBetweenCreationDatesTest : AbstractSpringTest() {
    @Autowired
    private lateinit var videoFileRepo: IVideoFileRepository

    private val baseRequest = ListBetweenCreationDates.Request(
            lowerDate = Date(System.currentTimeMillis()),
            upperDate = Date(System.currentTimeMillis() + 60000L)
    )

    @Before
    fun init() {
        // persist two video file records
        testUtil.createVideoFileRecord("thumbnail1.png", "filename1.mp4", "01:00:00")
        testUtil.createVideoFileRecord("thumbnail2.png", "filename2.mp4", "01:00:00")
    }

    @Test
    fun testValidConstraints_Success() {
        val (page, error) = ListBetweenCreationDates(
                request = baseRequest,
                videoRepo = videoFileRepo
        ).execute()

        assertNotNull(page)
        assertNull(error)
        assertEquals(2, page!!.count())
    }

    @Test
    fun testInvalidTimeConstraints_Failure() {
        // set lower date higher than upper date
        val badRequest = baseRequest.copy(lowerDate = Date(System.currentTimeMillis() + 60000L))

        val (page, error) = ListBetweenCreationDates(
                request = badRequest,
                videoRepo = videoFileRepo
        ).execute()

        assertNull(page)
        assertNotNull(error)
    }
}