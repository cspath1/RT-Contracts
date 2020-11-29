package com.radiotelescope.contracts.frontpagePicture

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.frontpagePicture.FrontpagePicture
import com.radiotelescope.repository.frontpagePicture.IFrontpagePictureRepository
import com.radiotelescope.services.s3.MockAwsS3DeleteService
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
internal class ApproveDenyTest : AbstractSpringTest() {
    @Autowired
    private lateinit var frontpagePictureRepo: IFrontpagePictureRepository

    private lateinit var baseRequest: ApproveDeny.Request

    @Before
    fun init() {
        val theFrontpagePicture = testUtil.createFrontpagePicture("Test Picture", "test.png", "Test Description", false)

        baseRequest = ApproveDeny.Request(
                frontpagePictureId = theFrontpagePicture.id,
                isApprove = true
        )
    }

    @Test
    fun testValidConstraintsApprove() {
        val (result, errors) = ApproveDeny(
                request = baseRequest,
                frontpagePictureRepo = frontpagePictureRepo,
                s3DeleteService = MockAwsS3DeleteService(true)
        ).execute()

        assertNotNull(result)
        assertNull(errors)

        val theFrontpagePicture = frontpagePictureRepo.findById(result!!.id).get()
        assertNotNull(theFrontpagePicture)
        assertTrue(theFrontpagePicture.approved)
    }

    @Test
    fun testValidConstraintsDeny() {
        val requestCopy = baseRequest.copy(
                isApprove = false
        )

        val (result, errors) = ApproveDeny(
                request = requestCopy,
                frontpagePictureRepo = frontpagePictureRepo,
                s3DeleteService = MockAwsS3DeleteService(true)
        ).execute()

        assertNotNull(result)
        assertNull(errors)

        // record should be deleted
        assertFalse(frontpagePictureRepo.findById(result!!.id).isPresent)
    }
}