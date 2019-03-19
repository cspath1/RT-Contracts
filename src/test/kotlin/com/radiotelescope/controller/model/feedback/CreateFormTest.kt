package com.radiotelescope.controller.model.feedback

import com.radiotelescope.contracts.feedback.ErrorTag
import org.junit.Assert.*
import org.junit.Test

internal class CreateFormTest {
    private val baseForm = CreateForm(
            name = "Cody Spath",
            priority = 5,
            comments = "light to decent 5"
    )

    @Test
    fun testToRequest() {
        // First make sure there are no errors
        assertNull(baseForm.validateRequest())

        val theRequest = baseForm.toRequest()

        assertEquals(theRequest.name, baseForm.name)
        assertEquals(theRequest.priority, baseForm.priority!!)
        assertEquals(theRequest.comments, baseForm.comments!!)
    }

    @Test
    fun testValidConstraints_Success() {
        // Call the validate request method
        val errors = baseForm.validateRequest()

        // Make sure there were no errors
        assertNull(errors)
    }

    @Test
    fun testNullPriority_Failure() {
        // Create a copy of the form with a null priority
        val baseFormCopy = baseForm.copy(
                priority = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the priority was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.PRIORITY].isNotEmpty())
    }

    @Test
    fun testNullComments_Failure() {
        // Create a copy of the form with a null comments
        val baseFormCopy = baseForm.copy(
                comments = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the comments was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.COMMENTS].isNotEmpty())
    }
}