package com.radiotelescope.contracts.frontpagePicture

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.frontpagePicture.IFrontpagePictureRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class BaseFrontpagePictureTest : AbstractSpringTest() {
    @Autowired
    private lateinit var frontpagePictureRepo: IFrontpagePictureRepository

    private lateinit var factory: FrontpagePictureFactory

    @Before
    fun init() {
        factory = BaseFrontpagePictureFactory(
                frontpagePictureRepo = frontpagePictureRepo
        )
    }

    @Test
    fun submit() {
        // Call the factory method
        val cmd = factory.submit(
                request = Submit.Request(
                        picture = "testpic.jpg",
                        description = "Test Description",
                        approved = true
                )
        )

        // Ensure it is the correct command
        Assert.assertTrue(cmd is Submit)
    }
}