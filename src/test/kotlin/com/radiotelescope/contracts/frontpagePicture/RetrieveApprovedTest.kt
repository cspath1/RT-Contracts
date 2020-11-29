package com.radiotelescope.contracts.frontpagePicture

import com.radiotelescope.repository.frontpagePicture.FrontpagePicture
import com.radiotelescope.repository.frontpagePicture.IFrontpagePictureRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class RetrieveApprovedTest : BaseFrontpagePictureFactoryTest() {
    @Autowired
    private lateinit var frontpagePictureRepo: IFrontpagePictureRepository

    private lateinit var approvedPicture: FrontpagePicture
    private lateinit var deniedPicture: FrontpagePicture

    @Before
    override fun init() {
        super.init()

        // Add two pictures to the database
        approvedPicture = testUtil.createFrontpagePicture("Approved Pic", "approved.jpg", "This picture is approved.", true)
        deniedPicture = testUtil.createFrontpagePicture("Denied Pic", "denied.jpg", "This picture is denied.", false)
    }

    @Test
    fun testRetrieveApproved() {
        val (page, error) = RetrieveApproved(
                frontpagePictureRepo = frontpagePictureRepo
        ).execute()

        assertNotNull(page)
        assertNull(error)
        assertEquals(1, page!!.count())
    }

    @Test
    fun testRetrieveApprovedNoneApproved() {
        approvedPicture.approved = false
        frontpagePictureRepo.save(approvedPicture)

        val (page, error) = RetrieveApproved(
                frontpagePictureRepo = frontpagePictureRepo
        ).execute()

        assertNotNull(page)
        assertNull(error)
        assertEquals(0, page!!.count())
    }
}