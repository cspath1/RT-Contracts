package com.radiotelescope.controller.model.appointment

import com.radiotelescope.contracts.appointment.ErrorTag
import org.junit.Assert.*
import org.junit.Test
import java.util.*

internal class CoordinateAppointmentCreateFormTest {
    private val baseForm = CoordinateCreateForm(
            userId = 1L,
            startTime = Date(System.currentTimeMillis() + 10000L),
            endTime = Date(System.currentTimeMillis() + 30000L),
            telescopeId = 1L,
            isPublic = true,
            hours = 12,
            minutes = 12,
            seconds = 12,
            declination = 69.0
    )

    @Test
    fun testToRequest() {
        // First, make sure there are no errors
        assertNull(baseForm.validateRequest())

        val theRequest = baseForm.toRequest()

        assertEquals(theRequest.userId, baseForm.userId!!)
        assertEquals(theRequest.telescopeId, baseForm.telescopeId!!)
        assertEquals(theRequest.endTime, baseForm.endTime!!)
        assertEquals(theRequest.startTime, baseForm.startTime!!)
        assertEquals(theRequest.isPublic, baseForm.isPublic!!)
        assertEquals(theRequest.declination, baseForm.declination!!, 0.00001)
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

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the user id field was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.USER_ID].isNotEmpty())
    }

    @Test
    fun testNullStartTime_Failure() {
        // Create a copy of the form with a null start time
        val baseFormCopy = baseForm.copy(
                startTime = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the start time was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.START_TIME].isNotEmpty())
    }

    @Test
    fun testNullEndTime_Failure() {
        // Create a copy of the form with a null end time
        val baseFormCopy = baseForm.copy(
                endTime = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the end time was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.END_TIME].isNotEmpty())
    }

    @Test
    fun testNullTelescopeId_Failure() {
        // Create a copy of the form with a null telescope id
        val baseFormCopy = baseForm.copy(
                telescopeId = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the telescope id was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.TELESCOPE_ID].isNotEmpty())
    }

    @Test
    fun testNullPublicFlag_Failure() {
        // Create a copy of the form with a null isPublic flag
        val baseFormCopy = baseForm.copy(
                isPublic = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the isPublic field was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.PUBLIC].isNotEmpty())
    }

    @Test
    fun testNullHours_Failure() {
        // Create a copy of the form with a null hours field
        val baseFormCopy = baseForm.copy(
                hours = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the hours was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.HOURS].isNotEmpty())
    }

    @Test
    fun testHoursBelowZero_Failure() {
        // Create a copy of the form with hours below zero
        val baseFormCopy = baseForm.copy(
                hours = -1
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the hours was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.HOURS].isNotEmpty())
    }

    @Test
    fun testHoursAboveTwentyFour_Failure() {
        // Create a copy of the form with hours below twenty four
        val baseFormCopy = baseForm.copy(
                hours = 25
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the hours was the reason for failure
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

        // Make sure the minutes was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.MINUTES].isNotEmpty())
    }

    @Test
    fun testMinutesBelowZero_Failure() {
        // Create a copy of the form with a minutes field below zero
        val baseFormCopy = baseForm.copy(
                minutes = -1
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the minutes was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.MINUTES].isNotEmpty())
    }

    @Test
    fun testMinutesAboveSixty_Failure() {
        // Create a copy of the form with a minutes field above sixty
        val baseFormCopy = baseForm.copy(
                minutes = 61
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the minutes was the reason for failure
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

        // Make sure the seconds was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.SECONDS].isNotEmpty())
    }

    @Test
    fun testSecondsBelowZero_Failure() {
        // Create a copy of the form with a seconds field below zero
        val baseFormCopy = baseForm.copy(
                seconds = -1
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the seconds was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.SECONDS].isNotEmpty())
    }

    @Test
    fun testSecondsAboveSixty_Failure() {
        // Create a copy of the form with a seconds field above sixty
        val baseFormCopy = baseForm.copy(
                seconds = 61
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the seconds was the reason for failure
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

        // Make sure the right ascension was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.DECLINATION].isNotEmpty())
    }
}