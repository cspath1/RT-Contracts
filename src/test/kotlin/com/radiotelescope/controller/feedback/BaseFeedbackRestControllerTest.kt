package com.radiotelescope.controller.feedback

import com.radiotelescope.contracts.feedback.BaseFeedbackFactory
import com.radiotelescope.contracts.feedback.FeedbackFactory
import com.radiotelescope.contracts.feedback.UserFeedbackWrapper
import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.repository.feedback.IFeedbackRepository
import com.radiotelescope.security.FakeUserContext
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired

internal abstract class BaseFeedbackRestControllerTest : BaseRestControllerTest() {
    @Autowired
    private lateinit var feedbackRepo: IFeedbackRepository

    private lateinit var wrapper: UserFeedbackWrapper
    private lateinit var factory: FeedbackFactory

    @Before
    override fun init() {
        super.init()

        factory = BaseFeedbackFactory(
                feedbackRepo = feedbackRepo
        )

        wrapper = UserFeedbackWrapper(
                context = getContext(),
                factory = factory
        )
    }

    fun getWrapper(): UserFeedbackWrapper {
        return wrapper
    }
}