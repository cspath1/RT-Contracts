package com.radiotelescope.controller.model.user

import com.radiotelescope.contracts.user.ErrorTag
import org.junit.Assert.*
import org.junit.Test

internal class ChangePasswordFormTest {
    private val baseForm = ChangePasswordForm(
            currentPassword = "Password1@",
            password = "Password1!",
            passwordConfirm = "Password1!",
            id = -1L
    )

    @Test
    fun testToRequest() {
        // First, make sure there are no errors
        assertNull(baseForm.validateRequest())

        val theRequest = baseForm.toRequest()

        assertEquals(theRequest.id, baseForm.id)
        assertEquals(theRequest.password, baseForm.password)
        assertEquals(theRequest.currentPassword, baseForm.currentPassword)
        assertEquals(theRequest.passwordConfirm, baseForm.passwordConfirm)
    }

    @Test
    fun testNullCurrentPassword_Errors() {
        // Create a copy of the form with a null current password
        val baseFormCopy = baseForm.copy(
                currentPassword = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the current password was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.CURRENT_PASSWORD].isNotEmpty())
    }

    @Test
    fun testBlankCurrentPassword_Failure() {
        // Create a copy of the form with a blank current password
        val baseFormCopy = baseForm.copy(
                currentPassword = " "
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the current password was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.CURRENT_PASSWORD].isNotEmpty())
    }

    @Test
    fun testNullNewPassword_Failure() {
        // Create a copy of the form with a null new password
        val baseFormCopy = baseForm.copy(
                password = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the new password was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.PASSWORD].isNotEmpty())
    }

    @Test
    fun testBlankNewPassword_Failure() {
        // Create a copy of the form with a blank new password
        val baseFormCopy = baseForm.copy(
                password = " "
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the new password was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.PASSWORD].isNotEmpty())
    }

    @Test
    fun testNullPasswordConfirm_Failure() {
        // Create a copy of the form with a null password confirm
        val baseFormCopy = baseForm.copy(
                passwordConfirm = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the password confirm was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.PASSWORD_CONFIRM].isNotEmpty())
    }

    @Test
    fun testBlankPasswordConfirm_Failure() {
        // Create a copy of the form with a blank password confirm
        val baseFormCopy = baseForm.copy(
                passwordConfirm = " "
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the password confirm was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.PASSWORD_CONFIRM].isNotEmpty())
    }

    @Test
    fun testPasswordsDoNotMatch_Failure() {
        // Create a copy of the form with non-matching fields
        val baseFormCopy = baseForm.copy(
                password = "Password123",
                passwordConfirm = "Password1234"
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the password confirm was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.PASSWORD_CONFIRM].isNotEmpty())
    }

    @Test
    fun testNewPasswordEqualsOldPassword_Failure() {
        // Create a copy of the form with the same new password as old
        val baseFormCopy = baseForm.copy(
                password = "Password1@",
                currentPassword = "Password1@"
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the password confirm was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.PASSWORD_CONFIRM].isNotEmpty())
    }

    @Test
    fun testNullId_Failure() {
        // Create a copy of the form with a null
        val baseFormCopy = baseForm.copy(
                id = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the id was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.ID].isNotEmpty())
    }
}