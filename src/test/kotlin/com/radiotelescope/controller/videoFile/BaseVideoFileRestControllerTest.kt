package com.radiotelescope.controller.videoFile

import com.radiotelescope.contracts.videoFile.BaseVideoFileFactory
import com.radiotelescope.contracts.videoFile.UserVideoFileWrapper
import com.radiotelescope.contracts.videoFile.VideoFileFactory
import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.repository.videoFile.IVideoFileRepository
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired

internal abstract class BaseVideoFileRestControllerTest: BaseRestControllerTest() {
    @Autowired
    private lateinit var videoFileRepo: IVideoFileRepository

    private lateinit var wrapper: UserVideoFileWrapper
    private lateinit var factory: VideoFileFactory

    @Before
    override fun init() {
        super.init()

        factory = BaseVideoFileFactory(
                videoFileRepo = videoFileRepo
        )

        wrapper = UserVideoFileWrapper(
                context = getContext(),
                factory = factory
        )
    }

    fun getWrapper(): UserVideoFileWrapper {
        return wrapper
    }
}