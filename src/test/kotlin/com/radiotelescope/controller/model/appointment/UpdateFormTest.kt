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
            isPublic = false
    )

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

        // Make sure there were errors
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

        // Make sure there were errors
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

        // Make sure there were errors
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

        // Make sure there were errors
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.PUBLIC].isNotEmpty())
    }
}