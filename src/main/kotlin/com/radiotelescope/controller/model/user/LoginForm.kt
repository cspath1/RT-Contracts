package com.radiotelescope.controller.model.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.user.Authenticate
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.controller.model.BaseForm

/**
 * Login form that takes nullable versions of the [Authenticate.Request] object.
 * It is in charge of making sure these values are not null before adapting it
 * to a [Authenticate.Request] object
 *
 * @param email the email
 * @param password the plain-text password
 */
data class LoginForm(
        val email: String?,
        val password: String?
) : BaseForm<Authenticate.Request> {
    /**
     * Override of the [BaseForm.toRequest] method that adapts
     * the form into a [Authenticate.Request] object
     *
     * @return the [Authenticate.Request] object
     */
    override fun toRequest(): Authenticate.Request {
        return Authenticate.Request(
                email = email!!,
                password = password!!
        )
    }

    /**
     * Makes sure the form's email and password are not null or blank
     *
     * @return a [HashMultimap] of errors or null
     */
    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        if (email.isNullOrBlank())
            errors.put(ErrorTag.EMAIL, "Invalid Email or Password")
        if (password.isNullOrBlank())
            errors.put(ErrorTag.PASSWORD, "Invalid Email or Password")

        return if (errors.isEmpty) null else errors
    }
}