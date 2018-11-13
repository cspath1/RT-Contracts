package com.radiotelescope.controller.model.appointment

import com.radiotelescope.contracts.appointment.ErrorTag
import org.junit.Assert.*
import org.junit.Test
import java.util.*

internal class UpdateFormTest {
    private val baseForm = UpdateForm(
            startTime = Date(System.currentTimeMillis() + 10000L),
            endTime = Date(System.currentTimeMillis() + 30000L),
            telescopeId = 1L,
            isPublic = false,
            rightAscension = 311.0,
            declination = 21.0
    )

    @Test
    fun testToRequest() {
        val theRequest = baseForm.toRequest()

        assertEquals(baseForm.startTime!!, theRequest.startTime)
        assertEquals(baseForm.endTime!!, theRequest.endTime)
        assertEquals(baseForm.telescopeId!!, theRequest.telescopeId)
        assertEquals(baseForm.isPublic!!, theRequest.isPublic)
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
    fun testInvalid_NullRightAscension_Failure() {
        // Create a copy of the form with a null right ascension
        val baseFormCopy = baseForm.copy(
                rightAscension = null
        )
        // Call the validateRequest method
        val errors = baseFormCopy.validateRequest()

        // Make sure the right ascension was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.RIGHT_ASCENSION].isNotEmpty())
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