package com.radiotelescope.contracts.feedback

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.feedback.IFeedbackRepository
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class BaseFeedbackFactoryTest : AbstractSpringTest() {
    @Autowired
    private lateinit var feedbackRepo: IFeedbackRepository

    private lateinit var factory: FeedbackFactory

    @Before
    fun init() {
        // Instantiate the factory
        factory = BaseFeedbackFactory(
                feedbackRepo = feedbackRepo
        )
    }

    @Test
    fun create() {
        // Call the factory method
        val cmd = factory.create(
                request = Create.Request(
                        name = "Cody Spath",
                        priority = 10,
                        comments = "Please save me"
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is Create)
    }
}