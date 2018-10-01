package com.radiotelescope.controller.model

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.contracts.user.Register
import com.radiotelescope.repository.role.UserRole

data class RegisterForm(
        val firstName: String?,
        val lastName: String?,
        val email: String?,
        val phoneNumber: String?,
        val password: String?,
        val passwordConfirm: String?,
        val company: String?,
        val categoryOfService: UserRole.Role?
) : BaseCreateForm<Register.Request>{
    override fun toRequest(): Register.Request {
        return Register.Request(
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