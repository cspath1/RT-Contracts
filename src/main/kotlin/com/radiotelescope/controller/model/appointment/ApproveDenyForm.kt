package com.radiotelescope.controller.model.appointment

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.appointment.ApproveDenyRequest
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.controller.model.BaseForm

/**
 * Request form that takes nullable versions of the [ApproveDenyRequest.Request] object.
 * It is in charge of making sure these values are not null before adapting it
 * to a [ApproveDenyRequest.Request] object
 *
 * @param appointmentId the appointment's Id
 * @param isApprove the approve status of the appointment
 */
data class ApproveDenyForm (
        val appointmentId: Long?,
        val isApprove: Boolean?
) : BaseForm<ApproveDenyRequest.Request> {
    /**
     * Override of the [BaseForm.toRequest] method that adapts the form
     * into a [ApproveDenyRequest.Request] object
     *
     * @return the [ApproveDenyRequest.Request] object
     */
    override fun toRequest(): ApproveDenyRequest.Request {
        return ApproveDenyRequest.Request(
                appointmentId = appointmentId!!,
                isApprove = isApprove!!
        )
    }

    /**
     * Makes sure the required fields are not null
     *
     * @return a [HashMultimap] of errors or null
     */
    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        if(appointmentId == null)
            errors.put(ErrorTag.ID, "Required field")
        if(isApprove == null)
            errors.put(ErrorTag.IS_APPROVE, "Required field")

        return if (errors.isEmpty) null else errors    }
}