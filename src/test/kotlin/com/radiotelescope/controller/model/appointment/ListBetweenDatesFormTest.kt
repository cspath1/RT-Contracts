package com.radiotelescope.controller.model.appointment

import com.radiotelescope.contracts.appointment.ErrorTag
import org.junit.Assert.*
import org.junit.Test
import java.util.*

internal class ListBetweenDatesFormTest {
    private val baseForm = ListBetweenDatesForm(
            startTime = Date(System.currentTimeMillis() + 100000L),
            endTime = Date(System.currentTimeMillis() + 200000L)
    )

    @Test
    fun testToRequest() {
        // First, make sure there are no errors
        assertNull(baseForm.validateRequest())

        val theRequest = baseForm.toRequest()

        assertEquals(theRequest.endTime, baseForm.endTime!!)
        assertEquals(theRequest.startTime, baseForm.startTime!!)

        // The telescope id gets set via the path variable,
        // so it should be set to -1 after the adaptation
        assertEquals(theRequest.telescopeId, -1L)
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

        // Make sure the start time field was the reason for failure
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
}