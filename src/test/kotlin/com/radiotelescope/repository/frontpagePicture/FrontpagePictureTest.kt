package com.radiotelescope.repository.frontpagePicture

import com.radiotelescope.AbstractSpringTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class FrontpagePictureTest : AbstractSpringTest() {
    @Autowired
    private lateinit var frontpagePictureRepo: IFrontpagePictureRepository

    private lateinit var approvedPicture: FrontpagePicture
    private lateinit var deniedPicture: FrontpagePicture

    @Before
    fun setUp() {
        // Add two pictures to the database
        approvedPicture = testUtil.createFrontpagePicture("Approved Pic", "approved.jpg", "This picture is approved.", true)
        deniedPicture = testUtil.createFrontpagePicture("Denied Pic", "denied.jpg", "This picture is denied.", false)
    }

    @Test
    fun testFindAllApprovedFrontpagePictures() {
        // Get all approved pictures
        val approvedPictures: List<FrontpagePicture> = frontpagePictureRepo.findAllApproved()

        assertEquals(1, approvedPictures.count())
        assertTrue(approvedPictures[0].approved)
    }
}