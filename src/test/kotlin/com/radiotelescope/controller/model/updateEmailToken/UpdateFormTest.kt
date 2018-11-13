package com.radiotelescope.controller.model.updateEmailToken

import com.radiotelescope.contracts.updateEmailToken.ErrorTag
import org.junit.Assert.*
import org.junit.Test

internal class UpdateFormTest {
    @Test
    fun testToRequest() {
        val baseForm = UpdateForm(
                email = "cspath1@ycp.edu",
                emailConfirm = "cspath1@ycp.edu"
        )

        val theRequest = baseForm.toRequest()

        assertEquals(baseForm.email!!, theRequest.email)
        assertEquals(baseForm.emailConfirm!!, theRequest.emailConfirm)
    }

    @Test
    fun testValidConstraints_Success() {
        // Call the validate request method
        val errors = UpdateForm(
                email = "cspath1@ycp.edu",
                emailConfirm = "cspath1@ycp.edu"
        ).validateRequest()

        // Ensure there are no errors
        assertNull(errors)
    }

    @Test
    fun testNullEmail_Failure() {
        // Call the validate request method with a null email
        val errors = UpdateForm(
                email = null,
                emailConfirm = "cspath1@ycp.edu"
        ).validateRequest()

        // Ensure it failed for the expected reason
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.EMAIL].isNotEmpty())
    }

    @Test
    fun testBlankEmail_Failure() {
        // Call the validate request method with a blank email
        val errors = UpdateForm(
                email = " ",
                emailConfirm = "cspath1@ycp.edu"
        ).validateRequest()

        // Ensure it failed for the expected reason
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.EMAIL].isNotEmpty())
    }

    @Test
    fun testNullEmailConfirm_Failure() {
        // Call the validate request method with a null email confirm
        val errors = UpdateForm(
                email = "cspath1@ycp.edu",
                emailConfirm = null
        ).validateRequest()

        // Ensure it failed for the expected reason
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.EMAIL_CONFIRM].isNotEmpty())
    }

    @Test
    fun testBlankEmailConfirm_Failure() {
        // Call the validate request method with a blank email confirm
        val errors = UpdateForm(
                email = "cspath1@ycp.edu",
                emailConfirm = " "
        ).validateRequest()

        // Ensure it failed for the expected reason
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.EMAIL_CONFIRM].isNotEmpty())
    }
}