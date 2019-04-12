package com.radiotelescope.controller.model.appointment

import com.radiotelescope.contracts.appointment.ErrorTag
import org.junit.Assert.*
import org.junit.Test

internal class ApproveDenyFormTest {
    private val baseForm = ApproveDenyForm(
            appointmentId = 1L,
            isApprove = true
    )

    @Test
    fun testToRequest() {
        // First, make sure there are no errors
        assertNull(baseForm.validateRequest())

        val theRequest = baseForm.toRequest()

        assertEquals(theRequest.appointmentId, baseForm.appointmentId!!)
        assertEquals(theRequest.isApprove, baseForm.isApprove!!)
    }

    @Test
    fun testValidConstraints_Success() {
        // Call the validate request method
        val errors = baseForm.validateRequest()

        // Make sure there were no errors
        assertNull(errors)
    }

    @Test
    fun testNullAppointmentId_Failure() {
        val baseFormCopy = baseForm.copy(
                appointmentId = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the id was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.ID].isNotEmpty())
    }

    @Test
    fun testNullApprovalFlag_Failure() {
        val baseFormCopy = baseForm.copy(
                isApprove = null
        )

        // Call the validate request method
        val errors = baseFormCopy.validateRequest()

        // Make sure the approval flag was the reason for failure
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.IS_APPROVE].isNotEmpty())
    }
}