package com.radiotelescope.controller.model.updateEmailToken

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.updateEmailToken.CreateUpdateEmailToken
import com.radiotelescope.contracts.updateEmailToken.ErrorTag
import com.radiotelescope.controller.model.BaseForm

/**
 * Update form that takes nullable versions of the [CreateUpdateEmailToken.Request] object.
 * It is in charge of making sure these values are not null before adapting it
 * to a [CreateUpdateEmailToken.Request] object
 *
 * @param email the User's new email
 * @param email the User's new confirm email
 */
data class UpdateForm(
        val email: String?,
        val emailConfirm: String?
) : BaseForm<CreateUpdateEmailToken.Request> {
    /**
     * Override of the [BaseForm.toRequest] method that
     * adapts the form into a [CreateUpdateEmailToken.Request] object
     */
    override fun toRequest(): CreateUpdateEmailToken.Request {
        return CreateUpdateEmailToken.Request(
                email = email!!,
                emailConfirm = emailConfirm!!,
                userId = -1L
        )
    }

    /**
     * Makes sure all required fields are not null
     *
     * @return a [HashMultimap] of errors or null
     */
    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (email == null)
            errors.put(ErrorTag.EMAIL, "Required field")
        if (emailConfirm == null)
            errors.put(ErrorTag.EMAIL_CONFIRM, "Required field")

        return if (errors.isEmpty) null else errors
    }
}