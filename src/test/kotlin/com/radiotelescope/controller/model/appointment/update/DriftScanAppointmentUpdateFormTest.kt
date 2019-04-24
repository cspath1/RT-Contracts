package com.radiotelescope.controller.model.appointment.update

import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.repository.appointment.Appointment
import org.junit.Assert.*
import org.junit.Test
import java.util.*

internal class DriftScanAppointmentUpdateFormTest {
    private val baseForm = DriftScanAppointmentUpdateForm(
            startTime = Date(System.currentTimeMillis() + 10000L),
            endTime = Date(System.currentTimeMillis() + 30000L),
            telescopeId = 1L,
            isPublic = false,
            priority = Appointment.Priority.PRIMARY,
            elevation = 90.0,
            azimuth = 180.0
    )

    @Test
    fun testToRequest() {
        assertNull(baseForm.validateRequest())

        val theRequest = baseForm.toRequest()

        assertEquals(baseForm.startTime!!, theRequest.startTime)
        assertEquals(baseForm.endTime!!, theRequest.endTime)
        assertEquals(baseForm.telescopeId!!, theRequest.telescopeId)
        assertEquals(baseForm.isPublic!!, theRequest.isPublic)
        assertEquals(baseForm.elevation!!, theRequest.elevation, 0.0)
        assertEquals(baseForm.azimuth!!, theRequest.azimuth, 0.0)
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
    fun testNullElevation_Failure() {
        // Create a copy of the form with a null hours field
        val baseFormCopy = baseForm.copy(
                elevation = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the hours was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.ELEVATION].isNotEmpty())
    }

    @Test
    fun testElevationBelowZero_Failure() {
        // Create a copy of the form with a null hours field
        val baseFormCopy = baseForm.copy(
                elevation = -1.0
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the hours was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.ELEVATION].isNotEmpty())
    }

    @Test
    fun testElevationAboveNinety_Failure() {
        // Create a copy of the form with a null hours field
        val baseFormCopy = baseForm.copy(
                elevation = 91.0
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the hours was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.ELEVATION].isNotEmpty())
    }

    @Test
    fun testNullAzimuth_Failure() {
        // Create a copy of the form with a null hours field
        val baseFormCopy = baseForm.copy(
                azimuth = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the hours was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.AZIMUTH].isNotEmpty())
    }

    @Test
    fun testAzimuthBelowZero_Failure() {
        // Create a copy of the form with a null hours field
        val baseFormCopy = baseForm.copy(
                azimuth = -1.0
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the hours was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.AZIMUTH].isNotEmpty())
    }

    @Test
    fun testAzimuthAbove360_Failure() {
        // Create a copy of the form with a null hours field
        val baseFormCopy = baseForm.copy(
                azimuth = 361.0
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the hours was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.AZIMUTH].isNotEmpty())
    }
}