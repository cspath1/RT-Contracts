package com.radiotelescope.controller.model.user

import com.radiotelescope.contracts.user.ErrorTag
import org.junit.Assert.*
import org.junit.Test

internal class LoginFormTest {
    private val baseForm = LoginForm(
            email = "cspath1@ycp.edu",
            password = "Password"
    )

    @Test
    fun testToRequest() {
        // First, make sure there are no errors
        assertNull(baseForm.validateRequest())

        val theRequest = baseForm.toRequest()

        assertEquals(theRequest.email, baseForm.email!!)
        assertEquals(theRequest.password, baseForm.password!!)
    }

    @Test
    fun testValidConstraints_NoErrors() {
        // Call the validate request method
        val errors = baseForm.validateRequest()

        // Make sure no errors occurred
        assertNull(errors)
    }

    @Test
    fun testNullEmail_Errors() {
        // Create a copy of the form with a null email
        val baseFormCopy = baseForm.copy(
                email = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the email was the reason for errors
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.EMAIL].isNotEmpty())
    }

    @Test
    fun testBlankEmail_Errors() {
        // Create a copy of the form with a blank email
        val baseFormCopy = baseForm.copy(
                email = ""
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the email was the reason for errors
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.EMAIL].isNotEmpty())
    }

    @Test
    fun testNullPassword_Errors() {
        // Create a copy of the form with a null password
        val baseFormCopy = baseForm.copy(
                password = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the password was the reason for errors
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.PASSWORD].isNotEmpty())
    }

    @Test
    fun testBlankPassword_Errors() {
        // Create a copy of the form with a blank password
        val baseFormCopy = baseForm.copy(
                password = ""
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the password was the reason for errors
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.PASSWORD].isNotEmpty())
    }
}