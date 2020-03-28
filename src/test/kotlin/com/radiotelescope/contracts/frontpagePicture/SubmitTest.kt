package com.radiotelescope.contracts.frontpagePicture

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.frontpagePicture.IFrontpagePictureRepository
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class SubmitTest : AbstractSpringTest() {
    @Autowired
    private lateinit var frontpagePictureRepo: IFrontpagePictureRepository

    private val baseRequest = Submit.Request(
            picture = "test.jpg",
            description = "Test Description",
            approved = false
    )

    @Test
    fun testValidConstraints() {
        val (result, errors) = Submit(
                request = baseRequest,
                frontpagePictureRepo = frontpagePictureRepo
        ).execute()

        assertNotNull(result)
        assertNull(errors)

        val theFrontpagePicture = frontpagePictureRepo.findById(result!!.id).get()
        assertNotNull(theFrontpagePicture)
        assertEquals(baseRequest.picture, result.picture)
        assertEquals(baseRequest.description, result.description)
        assertEquals(baseRequest.approved, result.approved)
    }

    @Test
    fun testInvalidConstraints_PictureTooLong() {
        val requestCopy = baseRequest.copy(
                picture = "test.jpg".repeat(40)
        )

        val (result, errors) = Submit(
                request = requestCopy,
                frontpagePictureRepo = frontpagePictureRepo
        ).execute()

        assertNotNull(errors)
        assertNull(result)

        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.PICTURE].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_DescriptionTooLong() {
        val requestCopy = baseRequest.copy(
                description = "Test Description".repeat(40)
        )

        val (result, errors) = Submit(
                request = requestCopy,
                frontpagePictureRepo = frontpagePictureRepo
        ).execute()

        assertNotNull(errors)
        assertNull(result)

        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.DESCRIPTION].isNotEmpty())
    }
}