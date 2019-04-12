package com.radiotelescope.controller.model.appointment.create

import com.radiotelescope.contracts.appointment.ErrorTag
import org.junit.Assert.*
import org.junit.Test
import java.util.*

internal class DriftScanAppointmentCreateFormTest {
    private val baseForm = DriftScanAppointmentCreateForm(
            userId = -1L,
            startTime = Date(System.currentTimeMillis() + 50000L),
            endTime = Date(System.currentTimeMillis() + 100000L),
            telescopeId = 1L,
            isPublic = true,
            elevation = 90.0,
            azimuth = 180.0
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
        assertEquals(theRequest.elevation, baseForm.elevation!!, 0.0001)
        assertEquals(theRequest.azimuth, baseForm.azimuth!!, 0.0001)
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
    fun testNullElevation_Failure() {
        // Create a copy of the form with a null isPublic flag
        val baseFormCopy = baseForm.copy(
                elevation = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the isPublic field was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.ELEVATION].isNotEmpty())
    }

    @Test
    fun testElevationBelowZero_Failure() {
        // Create a copy of the form with a null isPublic flag
        val baseFormCopy = baseForm.copy(
                elevation = -1.0
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the isPublic field was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.ELEVATION].isNotEmpty())
    }

    @Test
    fun testElevationAboveNinety_Failure() {
        // Create a copy of the form with a null isPublic flag
        val baseFormCopy = baseForm.copy(
                elevation = 91.0
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the isPublic field was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.ELEVATION].isNotEmpty())
    }

    @Test
    fun testNullAzimuth_Failure() {
        // Create a copy of the form with a null isPublic flag
        val baseFormCopy = baseForm.copy(
                azimuth = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the isPublic field was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.AZIMUTH].isNotEmpty())
    }

    @Test
    fun testAzimuthBelowZero_Failure() {
        // Create a copy of the form with a null isPublic flag
        val baseFormCopy = baseForm.copy(
                azimuth = -1.0
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the isPublic field was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.AZIMUTH].isNotEmpty())
    }

    @Test
    fun testAzimuthAbove360_Failure() {
        // Create a copy of the form with a null isPublic flag
        val baseFormCopy = baseForm.copy(
                azimuth = 400.0
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the isPublic field was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.AZIMUTH].isNotEmpty())
    }

}