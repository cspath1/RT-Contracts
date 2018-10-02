package com.radiotelescope.controller.model.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.contracts.user.Update
import com.radiotelescope.controller.model.BaseUpdateForm
import com.radiotelescope.repository.role.UserRole

data class UpdateForm(
        val id: Long?,
        val firstName: String?,
        val lastName: String?,
        val email: String?,
        val phoneNumber: String?,
        val password: String?,
        val passwordConfirm: String?,
        val company: String?,
        val categoryOfService: UserRole.Role?
) : BaseUpdateForm<Update.Request> {
    override fun toRequest(): Update.Request {
        return Update.Request(
                id = id!!,
                firstName = firstName!!,
                lastName = lastName!!,
                email = email!!,
                phoneNumber = phoneNumber,
                password = password!!,
                passwordConfirm = passwordConfirm!!,
                company = company,
                categoryOfService = categoryOfService!!
        )
    }

    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        if(id == null)
            errors.put(ErrorTag.ID, "ID may not be blank")
        if (firstName.isNullOrBlank())
            errors.put(ErrorTag.FIRST_NAME, "First Name may not be blank")
        if (lastName.isNullOrBlank())
            errors.put(ErrorTag.LAST_NAME, "Last Name may not be blank")
        if (email.isNullOrBlank())
            errors.put(ErrorTag.EMAIL, "Email may not be blank")
        if (password.isNullOrBlank())
            errors.put(ErrorTag.PASSWORD, "Password may not be blank")
        if (passwordConfirm.isNullOrBlank())
            errors.put(ErrorTag.PASSWORD_CONFIRM, "Password may not be blank")
        if (categoryOfService == null)
            errors.put(ErrorTag.CATEGORY_OF_SERVICE, "Required field")

        return if (errors.isEmpty) null else errors
    }
}