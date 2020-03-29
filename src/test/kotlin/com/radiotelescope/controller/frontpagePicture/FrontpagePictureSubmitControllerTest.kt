package com.radiotelescope.controller.frontpagePicture

import com.radiotelescope.controller.model.frontpagePicture.SubmitForm
import com.radiotelescope.repository.log.ILogRepository
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class FrontpagePictureSubmitControllerTest : BaseFrontpagePictureRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var frontpagePictureSubmitController: FrontpagePictureSubmitController

    private val baseForm = SubmitForm(
            picture = "test.jpg",
            description = "Test Description",
            approved = false
    )

    @Before
    override fun init() {
        super.init()

        frontpagePictureSubmitController = FrontpagePictureSubmitController(
                frontpagePictureWrapper = getWrapper(),
                logger = getLogger()
        )
    }
}