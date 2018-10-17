package com.radiotelescope.controller.model.role

import com.radiotelescope.contracts.role.ErrorTag
import com.radiotelescope.repository.role.UserRole
import org.junit.Assert.*
import org.junit.Test

internal class ValidateFormTest {
    private val baseForm = ValidateForm(
            id = 1L,
            role = UserRole.Role.STUDENT
    )

    @Test
    fun testToEntity() {
        // First, make sure there are no errors
        assertNull(baseForm.validateRequest())

        val theRequest = baseForm.toRequest()

        assertEquals(theRequest.role, baseForm.role!!)
        assertEquals(theRequest.id, baseForm.id!!)
    }

    @Test
    fun testValidConstraints_Success() {
        // Call the validate request method
        val errors = baseForm.validateRequest()

        // Make sure no errors occurred
        assertNull(errors)
    }

    @Test
    fun testNullId_Failure() {
        // Create a copy of the form with a null id
        val baseFormCopy = baseForm.copy(
                id = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the id was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.ID].isNotEmpty())
    }

    @Test
    fun testNullUserRole_Failure() {
        // Create a copy of the form with a null role
        val baseFormCopy = baseForm.copy(
                role = null
        )

        // call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the role was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.ROLE].isNotEmpty())
    }
}