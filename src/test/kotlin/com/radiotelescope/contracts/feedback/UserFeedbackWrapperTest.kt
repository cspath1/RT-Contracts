package com.radiotelescope.contracts.feedback

import com.radiotelescope.repository.feedback.IFeedbackRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class UserFeedbackWrapperTest {
    @Autowired
    private lateinit var feedbackRepo: IFeedbackRepository

    private val baseRequest = Create.Request(
            name = "Michael Scarn",
            priority = 10,
            comments = "Threat Level Midnight"
    )

    private lateinit var factory: FeedbackFactory
    private lateinit var wrapper: UserFeedbackWrapper

    @Before
    fun init() {
        // Instantiate the factory and wrapper
        factory = BaseFeedbackFactory(feedbackRepo)

        wrapper = UserFeedbackWrapper(factory)
    }

    @Test
    fun testValidFeedbackCreation() {
        val (id, error) = wrapper.create(baseRequest).execute()

        assertNotNull(id)
        assertNull(error)
    }
}