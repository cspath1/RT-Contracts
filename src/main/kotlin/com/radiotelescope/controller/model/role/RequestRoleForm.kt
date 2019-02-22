package com.radiotelescope.controller.model.role

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.role.ErrorTag
import com.radiotelescope.contracts.role.RequestRole
import com.radiotelescope.controller.model.BaseForm
import com.radiotelescope.repository.role.UserRole

/**
 * RequestRole form that takes nullable versions of the [RequestRole.Request] object.
 * It is in charge of making sure these values are not null before adapting it
 * to a [RequestRole.Request] object
 *
 * @param userId the User id
 * @param role the User's role
 */
data class RequestRoleForm(
        val userId: Long?,
        val role: UserRole.Role?
) : BaseForm<RequestRole.Request> {
    /**
     * Override of the [BaseForm.toRequest] method that
     * adapts the form into a [RequestRole.Request] object
     *
     * @return the [RequestRole.Request] object
     */
    override fun toRequest(): RequestRole.Request {
        return RequestRole.Request(
                userId = userId!!,
                role = role!!
        )
    }

    /**
     * Makes sure the required fields are not null
     *
     * @return a [HashMultimap] of errors or null
     */
    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        if (userId == null)
            errors.put(ErrorTag.USER_ID, "Required field")
        if (role == null)
            errors.put(ErrorTag.ROLE, "Required field")

        return if (errors.isEmpty) null else errors
    }
}