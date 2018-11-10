package com.radiotelescope.controller.model.user

import com.radiotelescope.contracts.user.ErrorTag
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UpdateFormTest {
    private val baseForm = UpdateForm(
            id = 123456789,
            firstName = "Rathana",
            lastName = "Pim",
            phoneNumber = "717-555-1111",
            company = "York College of PA"
    )

    @Test
    fun testToRequest() {
        val theRequest = baseForm.toRequest()

        assertEquals(baseForm.id!!, theRequest.id)
        assertEquals(baseForm.firstName!!, theRequest.firstName)
        assertEquals(baseForm.lastName!!, theRequest.lastName)
        assertEquals(baseForm.phoneNumber, theRequest.phoneNumber)
        assertEquals(baseForm.company, theRequest.company)
    }

    @Test
    fun testValidConstraints_NoErrors(){
        // Call the validate request method
        val errors = baseForm.validateRequest()

        // Make sure no errors occured
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
        Assert.assertNotNull(errors)
        Assert.assertTrue(errors!![ErrorTag.FIRST_NAME].isNotEmpty())
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
        Assert.assertNotNull(errors)
        Assert.assertTrue(errors!![ErrorTag.FIRST_NAME].isNotEmpty())
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
        Assert.assertNotNull(errors)
        Assert.assertTrue(errors!![ErrorTag.LAST_NAME].isNotEmpty())
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
        Assert.assertNotNull(errors)
        Assert.assertTrue(errors!![ErrorTag.LAST_NAME].isNotEmpty())
    }

    @Test
    fun testNullUserId_Errors() {
        // Create a copy of the form with a blank email
        val baseFormCopy = baseForm.copy(
                id = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the email field was the reason for errors
        Assert.assertNotNull(errors)
        Assert.assertTrue(errors!![ErrorTag.ID].isNotEmpty())
    }

    @Test
    fun testUserIdIsZero_Errors() {
        // Create a copy of the form with a blank email
        val baseFormCopy = baseForm.copy(
                id = 0
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the email field was the reason for errors
        Assert.assertNotNull(errors)
        Assert.assertTrue(errors!![ErrorTag.ID].isNotEmpty())
    }

    @Test
    fun testUserIdIsNegative_Errors() {
        // Create a copy of the form with a blank email
        val baseFormCopy = baseForm.copy(
                id = -123456789
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the email field was the reason for errors
        Assert.assertNotNull(errors)
        Assert.assertTrue(errors!![ErrorTag.ID].isNotEmpty())
    }



}