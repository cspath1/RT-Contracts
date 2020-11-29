package com.radiotelescope.controller.model.celestialBody

import com.radiotelescope.contracts.celestialBody.ErrorTag
import org.junit.Assert.*
import org.junit.Test

internal class UpdateFormTest {
    private val baseForm = UpdateForm(
            name = "Crab Nebula",
            hours = 5,
            minutes = 34,
            declination = 20.0
    )

    @Test
    fun testToRequest() {
        assertNull(baseForm.validateRequest())

        val theRequest = baseForm.toRequest()

        assertEquals(baseForm.name, theRequest.name)
        assertEquals(baseForm.declination, theRequest.declination)
        assertEquals(baseForm.hours, theRequest.hours)
        assertEquals(baseForm.minutes, theRequest.minutes)
        assertEquals(-1L, theRequest.id)
    }

    @Test
    fun testValidConstraints_Success() {
        // Call the validateRequest method
        val errors = baseForm.validateRequest()

        // Make sure there were no errors
        assertNull(errors)
    }

    @Test
    fun testNullName_Failure() {
        // Create a copy of the form with a null name
        val baseFormCopy = baseForm.copy(name = null)

        // Call the validateRequest method
        val errors = baseFormCopy.validateRequest()

        // Make sure the name was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.NAME].isNotEmpty())
    }

    @Test
    fun testBlankName_Failure() {
        // Create a copy of the form with a blank name
        val baseFormCopy = baseForm.copy(name = " ")

        // Call the validateRequest method
        val errors = baseFormCopy.validateRequest()

        // Make sure the name was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.NAME].isNotEmpty())
    }
}