package com.radiotelescope.controller.model.appointment.update

import com.radiotelescope.contracts.appointment.ErrorTag
import org.junit.Assert.*
import org.junit.Test
import java.util.*

internal class CoordinateAppointmentUpdateFormTest {
    private val baseForm = CoordinateAppointmentUpdateForm(
            startTime = Date(System.currentTimeMillis() + 10000L),
            endTime = Date(System.currentTimeMillis() + 30000L),
            telescopeId = 1L,
            isPublic = false,
            hours = 12,
            minutes = 12,
            seconds = 12,
            declination = 21.0
    )

    @Test
    fun testToRequest() {
        assertNull(baseForm.validateRequest())

        val theRequest = baseForm.toRequest()

        assertEquals(baseForm.startTime!!, theRequest.startTime)
        assertEquals(baseForm.endTime!!, theRequest.endTime)
        assertEquals(baseForm.telescopeId!!, theRequest.telescopeId)
        assertEquals(baseForm.isPublic!!, theRequest.isPublic)
        assertEquals(baseForm.hours!!, theRequest.hours)
        assertEquals(baseForm.minutes!!, theRequest.minutes)
        assertEquals(baseForm.seconds!!, theRequest.seconds)
        assertEquals(baseForm.declination!!, theRequest.declination, 0.00001)
    }

    @Test
    fun testValidConstraints_Success(){
        // Call the validateRequest method
        val errors = baseForm.validateRequest()

        // Make sure there were no errors
        assertNull(errors)
    }


    @Test
    fun testInvalid_NullStartTime_Failure(){
        // Create a copy of the form with a null appointmentId
        val baseFormCopy = baseForm.copy(
                startTime = null
        )
        // Call the validateRequest method
        val errors = baseFormCopy.validateRequest()

        // Make sure start time was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.START_TIME].isNotEmpty())
    }

    @Test
    fun testInvalid_NullEndTime_Failure(){
        // Create a copy of the form with a null appointmentId
        val baseFormCopy = baseForm.copy(
                endTime = null
        )
        // Call the validateRequest method
        val errors = baseFormCopy.validateRequest()

        // Make sure end time was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.END_TIME].isNotEmpty())
    }

    @Test
    fun testInvalid_NullTelescopeId_Failure(){
        // Create a copy of the form with a null appointmentId
        val baseFormCopy = baseForm.copy(
                telescopeId = null
        )
        // Call the validateRequest method
        val errors = baseFormCopy.validateRequest()

        // Make sure the telescope id was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.TELESCOPE_ID].isNotEmpty())
    }

    @Test
    fun testInvalid_NullIsPublic_Failure(){
        // Create a copy of the form with a null appointmentId
        val baseFormCopy = baseForm.copy(
                isPublic = null
        )
        // Call the validateRequest method
        val errors = baseFormCopy.validateRequest()

        // Make sure the isPublic was the reason for failure
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
    fun testInvalid_NullDeclination_Failure() {
        // Create a copy of the form with a null declination
        val baseFormCopy = baseForm.copy(
                declination = null
        )
        // Call the validateRequest method
        val errors = baseFormCopy.validateRequest()

        // Make sure the declination was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.DECLINATION].isNotEmpty())
    }
}