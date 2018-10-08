package com.radiotelescope.controller.model.role

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.role.ErrorTag
import com.radiotelescope.contracts.role.Validate
import com.radiotelescope.controller.model.BaseForm
import com.radiotelescope.repository.role.UserRole

data class ValidateForm(
        val id: Long?,
        val role: UserRole.Role?
) : BaseForm<Validate.Request> {
    override fun toRequest(): Validate.Request {
        return Validate.Request(
                id = id!!,
                role = role!!
        )
    }

    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        if (id == null)
            errors.put(ErrorTag.ID, "Required field")
        if (role == null)
            errors.put(ErrorTag.ROLE, "Required field")

        return if (errors.isEmpty) null else errors
    }
}