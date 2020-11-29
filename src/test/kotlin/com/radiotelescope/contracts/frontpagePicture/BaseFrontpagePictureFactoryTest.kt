package com.radiotelescope.contracts.frontpagePicture

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.frontpagePicture.IFrontpagePictureRepository
import com.radiotelescope.services.s3.MockAwsS3DeleteService
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class BaseFrontpagePictureFactoryTest : AbstractSpringTest() {
    @Autowired
    private lateinit var frontpagePictureRepo: IFrontpagePictureRepository

    private lateinit var factory: FrontpagePictureFactory

    @Before
    fun init() {
        factory = BaseFrontpagePictureFactory(
                frontpagePictureRepo = frontpagePictureRepo,
                s3DeleteService = MockAwsS3DeleteService(true)
        )
    }

    @Test
    fun submit() {
        // Call the factory method
        val cmd = factory.submit(
                request = Submit.Request(
                        pictureTitle = "Test Picture",
                        pictureUrl = "testpic.jpg",
                        description = "Test Description",
                        approved = true
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is Submit)
    }

    @Test
    fun retrieveList() {
        // Call the factory method
        val cmd = factory.retrieveList()

        // Ensure it is the correct command
        assertTrue(cmd is RetrieveList)
    }

    @Test
    fun retrieveApproved() {
        // Call the factory method
        val cmd = factory.retrieveApproved()

        // Ensure it is the correct command
        assertTrue(cmd is RetrieveApproved)
    }

    @Test
    fun approveDeny() {
        // Call the factory method
        val cmd = factory.approveDeny(
                request = ApproveDeny.Request(
                        frontpagePictureId = 1L,
                        isApprove = true
                )
        )

        // Call the factory method
        assertTrue(cmd is ApproveDeny)
    }
}