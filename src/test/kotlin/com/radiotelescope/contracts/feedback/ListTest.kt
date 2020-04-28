package com.radiotelescope.contracts.feedback

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.feedback.Feedback
import com.radiotelescope.repository.feedback.IFeedbackRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class ListTest : AbstractSpringTest() {
    @Autowired
    private lateinit var feedbackRepo: IFeedbackRepository

    private var pageable = PageRequest.of(0, 5)

    @Before
    fun init() {
        // create several feedback objects
        feedbackRepo.save(Feedback("Feedback1", 10, "This website sucks."))
        feedbackRepo.save(Feedback("Feedback2", 10, "better than twitter"))
        feedbackRepo.save(Feedback("Feedback3", 1, "Everything is broken, please fix"))
    }

    @Test
    fun testPopulatedRepo_Success() {
        val (page, errors) = List(
                pageable = pageable,
                feedbackRepo = feedbackRepo
        ).execute()

        assertNull(errors)
        assertNotNull(page)
        assertEquals(3, page!!.content.size)
    }

    @Test
    fun testEmptyRepo_Success() {
        feedbackRepo.deleteAll()

        val (page, errors) = List(
                pageable = pageable,
                feedbackRepo = feedbackRepo
        ).execute()

        assertNotNull(errors)
        assertNull(page)
        assertEquals(0, page!!.content.size)
    }
}