package com.radiotelescope.controller.frontpagePicture

import com.radiotelescope.contracts.frontpagePicture.BaseFrontpagePictureFactory
import com.radiotelescope.contracts.frontpagePicture.FrontpagePictureFactory
import com.radiotelescope.contracts.frontpagePicture.UserFrontpagePictureWrapper
import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.repository.frontpagePicture.IFrontpagePictureRepository
import com.radiotelescope.services.s3.MockAwsS3DeleteService
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired

internal abstract class BaseFrontpagePictureRestControllerTest : BaseRestControllerTest() {
    @Autowired
    private lateinit var frontpagePictureRepo: IFrontpagePictureRepository

    private lateinit var wrapper: UserFrontpagePictureWrapper
    private lateinit var factory: FrontpagePictureFactory

    @Before
    override fun init() {
        super.init()

        factory = BaseFrontpagePictureFactory(
                frontpagePictureRepo = frontpagePictureRepo,
                s3DeleteService = MockAwsS3DeleteService(true)
        )

        wrapper = UserFrontpagePictureWrapper(
                context = getContext(),
                factory = factory
        )
    }

    fun getWrapper(): UserFrontpagePictureWrapper {
        return wrapper
    }
}