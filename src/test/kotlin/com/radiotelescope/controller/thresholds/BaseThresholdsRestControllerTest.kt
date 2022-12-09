package com.radiotelescope.controller.thresholds

import com.radiotelescope.contracts.thresholds.BaseThresholdsFactory
import com.radiotelescope.contracts.thresholds.ThresholdsFactory
import com.radiotelescope.contracts.thresholds.UserThresholdsWrapper
import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.repository.thresholds.IThresholdsRepository
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired

internal abstract class BaseThresholdsRestControllerTest : BaseRestControllerTest() {
    @Autowired
    private lateinit var thresholdsRepo: IThresholdsRepository

    private lateinit var wrapper: UserThresholdsWrapper
    private lateinit var factory: ThresholdsFactory

    @Before
    override fun init() {
        super.init()

        factory = BaseThresholdsFactory(
                thresholdsRepo = thresholdsRepo
        )

        wrapper = UserThresholdsWrapper(
                context = getContext(),
                factory = factory
        )
    }

    fun getWrapper(): UserThresholdsWrapper {
        return wrapper
    }
}