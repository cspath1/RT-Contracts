package com.radiotelescope.controller.model.user

import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.repository.role.UserRole
import org.junit.Assert.*
import org.junit.Test

class RegisterFormTest {
    private val baseForm = RegisterForm(
            firstName = "Cody",
            lastName = "Spath",
            email = "cspath1@ycp.edu",
            phoneNumber = "717-823-2216",
            password = "ValidPassword",
            passwordConfirm = "ValidPassword",
            company = "York College of PA",
            categoryOfService = UserRole.Role.GUEST
    )

    @Test
    fun testValidConstraints_NoErrors() {
        // Call the validate request method
        val errors = baseForm.validateRequest()

        // Make sure no errors occurred
        assertNull(errors)
    }

    @Test
    fun testNullFirstName_Errors() {
        // Create a copy of the form with a null first name
        val baseFormCopy = baseForm.copy(
                firstName = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the first name field was the reason for errors
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.FIRST_NAME].isNotEmpty())
    }

    @Test
    fun testBlankFirstName_Errors() {
        // Create a copy of the form with a null first name
        val baseFormCopy = baseForm.copy(
                firstName = " "
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the first name field was the reason for errors
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.FIRST_NAME].isNotEmpty())
    }

    @Test
    fun testNullLastName_Errors() {
        // Create a copy of the form with a null last name
        val baseFormCopy = baseForm.copy(
                lastName = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the last name field was the reason for errors
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.LAST_NAME].isNotEmpty())
    }

    @Test
    fun testBlankLastName_Errors() {
        // Create a copy of the form with a null last name
        val baseFormCopy = baseForm.copy(
                lastName = " "
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the last name field was the reason for errors
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.LAST_NAME].isNotEmpty())
    }

    @Test
    fun testNullEmail_Errors() {
        // Create a copy of the form with a null email
        val baseFormCopy = baseForm.copy(
                email = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the email field was the reason for errors
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.EMAIL].isNotEmpty())
    }

    @Test
    fun testBlankEmail_Errors() {
        // Create a copy of the form with a blank email
        val baseFormCopy = baseForm.copy(
                email = " "
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the email field was the reason for errors
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

        // Make sure the password field was the reason for errors
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.PASSWORD].isNotEmpty())
    }

    @Test
    fun testBlankPassword_Errors() {
        // Create a copy of the form with a null password
        val baseFormCopy = baseForm.copy(
                password = " "
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the password field was the reason for errors
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.PASSWORD].isNotEmpty())
    }

    @Test
    fun testNullPasswordConfirm_Errors() {
        // Create a copy of the form with a null password confirm
        val baseFormCopy = baseForm.copy(
                passwordConfirm = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the password confirm field was the reason for errors
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.PASSWORD_CONFIRM].isNotEmpty())
    }

    @Test
    fun testBlankPasswordConfirm_Errors() {
        // Create a copy of the form with a blank password confirm
        val baseFormCopy = baseForm.copy(
                passwordConfirm = " "
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the password confirm field was the reason for errors
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.PASSWORD_CONFIRM].isNotEmpty())
    }

    @Test
    fun testNullCategoryOfService_Errors() {
        // Create a copy of the form with a null category of service
        val baseFormCopy = baseForm.copy(
                categoryOfService = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the category of service field was the reason for errors
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.CATEGORY_OF_SERVICE].isNotEmpty())
    }
}