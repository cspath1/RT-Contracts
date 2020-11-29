package com.radiotelescope.controller.model.appointment

import com.radiotelescope.contracts.appointment.ErrorTag
import org.junit.Assert.*
import org.junit.Test

internal class AddFreeControlAppointmentCommandFormTest {
    private val baseForm = AddFreeControlAppointmentCommandForm(
            hours = 1,
            minutes = 2,
            declination = 4.20
    )

    @Test
    fun testToRequest() {
        // First, make sure there are no errors
        assertNull(baseForm.validateRequest())

        val theRequest = baseForm.toRequest()

        assertEquals(baseForm.hours!!, theRequest.hours)
        assertEquals(baseForm.minutes!!, theRequest.minutes)
        assertEquals(baseForm.declination!!, theRequest.declination, 0.0001)

        // The appointment id will be -1 since that is set via the controller's path variable
        assertEquals(-1L, theRequest.appointmentId)
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
        // Create a copy of the form with a null hours
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
        // Create a copy of the form with a null minutes
        val baseFormCopy = baseForm.copy(
                minutes = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the minutes field was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.MINUTES].isNotEmpty())
    }
    /*
    @Test
    fun testNullSeconds_Failure() {
        // Create a copy of the form with a null seconds
        val baseFormCopy = baseForm.copy(
                seconds = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the seconds field was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.SECONDS].isNotEmpty())
    }
    */
    @Test
    fun testNullDeclination_Failure() {
        // Create a copy of the form with a null declination
        val baseFormCopy = baseForm.copy(
                declination = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the declination field was the reason
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.DECLINATION].isNotEmpty())
    }
}