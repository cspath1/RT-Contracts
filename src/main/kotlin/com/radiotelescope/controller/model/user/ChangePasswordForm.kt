package com.radiotelescope.controller.model.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.user.ChangePassword
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.controller.model.BaseForm

data class ChangePasswordForm(
        val currentPassword: String,
        val password: String,
        val passwordConfirm: String,
        val id: Long
): BaseForm<ChangePassword.Request>{
    /**
     * Override of the [BaseForm.toRequest] method that adapts
     * the form into a [ChangePassword.Request] object
     */
    override fun toRequest(): ChangePassword.Request {
        return ChangePassword.Request(
                currentPassword = currentPassword,
                password = password,
                passwordConfirm = passwordConfirm,
                id = id
        )
    }

    /**
     * Method responsible for constraint checking/validation. It ensures
     * that the given passwords are not blank, match, and fulfill the requirements
     * to be legitimate passwords
     */
    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
            if (password.isBlank() || currentPassword.isBlank() || passwordConfirm.isBlank())
                errors.put(com.radiotelescope.contracts.user.ErrorTag.PASSWORD, "Password may not be blank")
            if (password != passwordConfirm)
                errors.put(com.radiotelescope.contracts.user.ErrorTag.PASSWORD_CONFIRM, "Passwords do not match")
            if (password == currentPassword)
                errors.put(com.radiotelescope.contracts.user.ErrorTag.PASSWORD, "New password may not be the same as old password")
            if (!password.matches(com.radiotelescope.repository.user.User.passwordRegex))
                errors.put(com.radiotelescope.contracts.user.ErrorTag.PASSWORD, com.radiotelescope.repository.user.User.passwordErrorMessage)

        return if (errors.isEmpty) null else errors
    }

}