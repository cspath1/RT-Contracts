package com.radiotelescope.controller.model.appointment

import com.radiotelescope.contracts.appointment.ErrorTag
import org.junit.Assert.*
import org.junit.Test

internal class StartFreeControlAppointmentFormTest {
    private val baseForm = StartFreeControlAppointmentForm(
            userId = 1L,
            telescopeId = 1L,
            duration = 30,
            hours = 1,
            minutes = 2,
            seconds = 3,
            declination = 20.0,
            isPublic = true
    )

    @Test
    fun testToRequest() {
        // First, make sure there are no errors
        assertNull(baseForm.validateRequest())

        val theRequest = baseForm.toRequest()

        assertEquals(baseForm.userId!!, theRequest.userId)
        assertEquals(baseForm.telescopeId, theRequest.telescopeId)
        assertEquals(baseForm.duration!!, theRequest.duration)
        assertEquals(baseForm.hours!!, theRequest.hours)
        assertEquals(baseForm.minutes!!, theRequest.minutes)
        assertEquals(baseForm.seconds!!, theRequest.seconds)
        assertEquals(baseForm.declination!!, theRequest.declination, 0.0001)
        assertEquals(baseForm.isPublic!!, theRequest.isPublic)
    }

    @Test
    fun testValidConstraints_Success() {
        // Call the validate request method
        val errors = baseForm.validateRequest()

        // Make sure there were no errors
        assertNull(errors)
    }

    @Test
    fun testNullUserId_Failure() {
        // Create a copy of the form with a null user id
        val baseFormCopy = baseForm.copy(
                userId = null
        )

        // Call the valid request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the user id field was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.USER_ID].isNotEmpty())
    }

    @Test
    fun testNullTelescopeId_Failure() {
        // Create a copy of the form with a null telescope id
        val baseFormCopy = baseForm.copy(
                telescopeId = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the telescope id field was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.TELESCOPE_ID].isNotEmpty())
    }

    @Test
    fun testNullDuration_Failure() {
        // Create a copy of the form with a null duration
        val baseFormCopy = baseForm.copy(
                duration = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the duration field was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.END_TIME].isNotEmpty())
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

    @Test
    fun testNullPublicField_Failure() {
        // Create a copy of the form with a null public field
        val baseFormCopy = baseForm.copy(
                isPublic = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the public field was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.PUBLIC].isNotEmpty())
    }
}