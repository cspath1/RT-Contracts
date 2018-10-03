package com.radiotelescope.controller.model.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.user.Authenticate
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.controller.model.BaseCreateForm

data class LoginForm(
        val email: String?,
        val password: String?
) : BaseCreateForm<Authenticate.Request> {
    override fun toRequest(): Authenticate.Request {
        return Authenticate.Request(
                email = email!!,
                password = password!!
        )
    }

    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        if (email.isNullOrBlank())
            errors.put(ErrorTag.EMAIL, "Invalid Email or Password")
        if (password.isNullOrBlank())
            errors.put(ErrorTag.PASSWORD, "Invalid Email or Password")

        return if (errors.isEmpty) null else errors
    }
}