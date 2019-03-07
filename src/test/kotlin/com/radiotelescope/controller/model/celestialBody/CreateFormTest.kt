package com.radiotelescope.controller.model.celestialBody

import com.radiotelescope.contracts.celestialBody.ErrorTag
import org.junit.Assert.*
import org.junit.Test

internal class CreateFormTest {
    private val baseForm = CreateForm(
            name = "Crab Nebula",
            hours = 5,
            minutes = 34,
            seconds = 32,
            declination = 22.0
    )

    @Test
    fun toRequest() {
        // First, make sure there are no errors
        assertNull(baseForm.validateRequest())

        val theRequest = baseForm.toRequest()

        assertEquals(baseForm.name, theRequest.name)
        assertEquals(baseForm.hours, theRequest.hours)
        assertEquals(baseForm.minutes, theRequest.minutes)
        assertEquals(baseForm.seconds, theRequest.seconds)
        assertEquals(baseForm.declination, theRequest.declination)
    }

    @Test
    fun testValidConstraints_NoErrors() {
        // Call the validate request method
        val errors = baseForm.validateRequest()

        // Make sure no errors occurred
        assertNull(errors)
    }

    @Test
    fun testNullName_Failure() {
        val baseFormCopy = baseForm.copy(
                name = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the the name was the reason for errors
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.NAME].isNotEmpty())
    }

    @Test
    fun testBlankName_Failure() {
        val baseFormCopy = baseForm.copy(
                name = " "
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the the name was the reason for errors
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.NAME].isNotEmpty())
    }
}