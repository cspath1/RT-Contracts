package com.radiotelescope.contracts.frontpagePicture

import com.radiotelescope.AbstractSpringTest
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
internal class ApproveDenyTest : AbstractSpringTest() {
    @Autowired
    private lateinit var frontpagePictureRepo: IFrontpagePictureRepository

    private lateinit var baseRequest: ApproveDeny.Request

    //private lateinit var theFrontpagePicture: FrontpagePicture

    @Before
    fun init() {
        val theFrontpagePicture = testUtil.createFrontpagePicture("test.png", "Test Description", false)

        baseRequest = ApproveDeny.Request(
                frontpagePictureId = theFrontpagePicture.id,
                isApprove = true
        )
    }

    @Test
    fun testValidConstraintsApprove() {
        val (result, errors) = ApproveDeny(
                request = baseRequest,
                frontpagePictureRepo = frontpagePictureRepo
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
                frontpagePictureRepo = frontpagePictureRepo
        ).execute()

        assertNotNull(result)
        assertNull(errors)

        //print("Element: " + frontpagePictureRepo.findById(result!!.id) + "\n")

        //assertNull(frontpagePictureRepo.findById(result.id).get())
    }
}