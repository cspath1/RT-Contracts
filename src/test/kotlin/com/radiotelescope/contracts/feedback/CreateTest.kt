package com.radiotelescope.contracts.feedback

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.feedback.IFeedbackRepository
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class CreateTest : AbstractSpringTest() {
    @Autowired
    private lateinit var feedbackRepo: IFeedbackRepository

    private val baseRequest = Create.Request(
            name = "Michael Scarn",
            priority = 10,
            comments = "Threat Level Midnight"
    )

    @Test
    fun testValidConstraints() {
        val (id, errors) = Create(
                request = baseRequest,
                feedbackRepo = feedbackRepo
        ).execute()

        assertNotNull(id)
        assertNull(errors)

        val theFeedback = feedbackRepo.findById(id!!).get()
        assertNotNull(theFeedback)
        assertEquals(baseRequest.name, theFeedback.name)
        assertEquals(baseRequest.priority, theFeedback.priority)
        assertEquals(baseRequest.comments, theFeedback.comments)
    }

    @Test
    fun testInvalidConstraints_PriorityBelowOne() {
        val requestCopy = baseRequest.copy(
                priority = 0
        )

        val (id, errors) = Create(
                request = requestCopy,
                feedbackRepo = feedbackRepo
        ).execute()

        assertNotNull(errors)
        assertNull(id)

        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.PRIORITY].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_PriorityAboveTen() {
        val requestCopy = baseRequest.copy(
                priority = 11
        )

        val (id, errors) = Create(
                request = requestCopy,
                feedbackRepo = feedbackRepo
        ).execute()

        assertNotNull(errors)
        assertNull(id)

        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.PRIORITY].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_BlankComments() {
        val requestCopy = baseRequest.copy(
                comments = " "
        )

        val (id, errors) = Create(
                request = requestCopy,
                feedbackRepo = feedbackRepo
        ).execute()

        assertNotNull(errors)
        assertNull(id)

        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.COMMENTS].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_NameTooLong() {
        val requestCopy = baseRequest.copy(
                name = "Michael Scarn".repeat(50)
        )

        val (id, errors) = Create(
                request = requestCopy,
                feedbackRepo = feedbackRepo
        ).execute()

        assertNotNull(errors)
        assertNull(id)

        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.NAME].isNotEmpty())
    }
}