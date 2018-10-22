package com.radiotelescope.controller.model.role

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.role.ErrorTag
import com.radiotelescope.contracts.role.Validate
import com.radiotelescope.controller.model.BaseForm
import com.radiotelescope.repository.role.UserRole

/**
 * Validate form that takes nullable versions of the [Validate.Request] object.
 * It is in charge of making sure these values are not null before adapting it
 * to a [Validate.Request] object
 *
 * @param id the UserRole id
 * @param role the UserRole role
 */
data class ValidateForm(
        val id: Long?,
        val role: UserRole.Role?
) : BaseForm<Validate.Request> {
    /**
     * Override of the [BaseForm.toRequest] method that adapts
     * the form into a [Validate.Request] object
     *
     * @return the [Validate.Request] object
     */
    override fun toRequest(): Validate.Request {
        return Validate.Request(
                id = id!!,
                role = role!!
        )
    }

    /**
     * Makes sure the form's id and role are not null
     *
     * @return a [HashMultimap] of errors or null
     */
    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        if (id == null)
            errors.put(ErrorTag.ID, "Required field")
        if (role == null)
            errors.put(ErrorTag.ROLE, "Required field")

        return if (errors.isEmpty) null else errors
    }
}