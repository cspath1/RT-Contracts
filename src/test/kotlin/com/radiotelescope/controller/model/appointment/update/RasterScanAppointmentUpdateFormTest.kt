package com.radiotelescope.controller.model.appointment.update

import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.controller.model.coordinate.CoordinateForm
import com.radiotelescope.repository.appointment.Appointment
import org.junit.Assert.*
import org.junit.Test
import java.util.*

internal class RasterScanAppointmentUpdateFormTest {
    private var coordinateFormOne = CoordinateForm(
            hours = 12,
            minutes = 12,
            seconds = 12,
            declination = 45.0
    )

    private var coordinateFormTwo = CoordinateForm(
            hours = 13,
            minutes = 13,
            seconds = 13,
            declination = 50.0
    )

    private val baseForm = RasterScanAppointmentUpdateForm(
            telescopeId = 1L,
            startTime = Date(System.currentTimeMillis() + 100000L),
            endTime = Date(System.currentTimeMillis() + 300000L),
            isPublic = true,
            priority = Appointment.Priority.PRIMARY,
            coordinates = mutableListOf(coordinateFormOne, coordinateFormTwo)
    )

    @Test
    fun testToRequest() {
        // First, make sure there are no errors
        assertNull(baseForm.validateRequest())

        val theRequest = baseForm.toRequest()

        assertEquals(theRequest.telescopeId, baseForm.telescopeId!!)
        assertEquals(theRequest.endTime, baseForm.endTime!!)
        assertEquals(theRequest.startTime, baseForm.startTime!!)
        assertEquals(theRequest.isPublic, baseForm.isPublic!!)
        assertEquals(theRequest.coordinates.size, baseForm.coordinates!!.size)
    }

    @Test
    fun testValidConstraints_Success() {
        // Call the validate request method
        val errors = baseForm.validateRequest()

        // Make sure there were no errors
        assertNull(errors)
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
    fun testNullCoordinatesList_Failure() {
        // Create a copy of the form with a null coordinate form list
        val baseFormCopy = baseForm.copy(
                coordinates = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the coordinates list was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.COORDINATES].isNotEmpty())
    }

    @Test
    fun testNotEnoughCoordinates_Failure() {
        // Create a copy of the form with no coordinates in the list
        val baseFormCopy = baseForm.copy(
                coordinates = mutableListOf()
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the coordinates list was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.COORDINATES].isNotEmpty())
    }

    @Test
    fun testFailedCoordinateValidation_Failure() {
        val coordinateCopy = coordinateFormOne.copy(
                hours = null
        )

        // Create a copy of the form with the above coordinate form
        // which will fail its validation
        val baseFormCopy = baseForm.copy(
                coordinates = mutableListOf(coordinateCopy, coordinateFormTwo)
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the coordinates list was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.HOURS].isNotEmpty())
    }
}