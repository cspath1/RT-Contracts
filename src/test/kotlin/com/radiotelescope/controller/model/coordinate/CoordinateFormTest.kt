package com.radiotelescope.controller.model.coordinate

import com.radiotelescope.contracts.appointment.ErrorTag
import org.junit.Assert.*
import org.junit.Test

internal class CoordinateFormTest {
    private val baseForm = CoordinateForm(
            hours = 12,
            minutes = 12,
            seconds = 12,
            declination = 45.0
    )

    @Test
    fun testToRequest() {
        // First, make sure there are no errors
        assertNull(baseForm.validateRequest())

        val theRequest = baseForm.toRequest()

        assertEquals(theRequest.hours, baseForm.hours!!)
        assertEquals(theRequest.minutes, baseForm.minutes!!)
        assertEquals(theRequest.seconds, baseForm.seconds!!)
        assertEquals(theRequest.declination, baseForm.declination!!, 0.0001)
    }

    @Test
    fun testValidConstraints_Success() {
        // Call the validate request method
        val errors = baseForm.validateRequest()

        // Make sure there were no errors
        assertNull(errors)
    }

    @Test
    fun testNullHours_Failure() {
        // Create a copy of the form with a null hours field
        val baseFormCopy = baseForm.copy(
                hours = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the hours field was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.HOURS].isNotEmpty())
    }

    @Test
    fun testNullMinutes_Failure() {
        // Create a copy of the form with a null minutes field
        val baseFormCopy = baseForm.copy(
                minutes = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the minutes field was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.MINUTES].isNotEmpty())
    }

    @Test
    fun testNullSeconds_Failure() {
        // Create a copy of the form with a null seconds field
        val baseFormCopy = baseForm.copy(
                seconds = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the seconds field was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.SECONDS].isNotEmpty())
    }

    @Test
    fun testNullDeclination_Failure() {
        // Create a copy of the form with a null declination field
        val baseFormCopy = baseForm.copy(
                declination = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the seconds field was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.DECLINATION].isNotEmpty())
    }
}