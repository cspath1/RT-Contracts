package com.radiotelescope.controller.model.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.user.ChangePassword
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.controller.model.BaseForm

data class ChangePasswordForm(
        val currentPassword: String?,
        val password: String?,
        val passwordConfirm: String?,
        val id: Long?
): BaseForm<ChangePassword.Request>{
    /**
     * Override of the [BaseForm.toRequest] method that adapts
     * the form into a [ChangePassword.Request] object
     */
    override fun toRequest(): ChangePassword.Request {
        return ChangePassword.Request(
                currentPassword = currentPassword!!,
                password = password!!,
                passwordConfirm = passwordConfirm!!,
                id = id!!
        )
    }

    /**
     * Method responsible for constraint checking/validation. It ensures
     * that the given passwords are not blank, match, and fulfill the requirements
     * to be legitimate passwords
     */
    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if(id == null)
            errors.put(ErrorTag.ID, "Required Field")
        if (password.isNullOrBlank())
            errors.put(ErrorTag.PASSWORD, "Required Field")
        if (currentPassword.isNullOrBlank())
            errors.put(ErrorTag.CURRENT_PASSWORD, "Required Field")
        if (passwordConfirm.isNullOrBlank())
            errors.put(ErrorTag.PASSWORD_CONFIRM, "Required Field")
        if (password != passwordConfirm)
            errors.put(ErrorTag.PASSWORD_CONFIRM, "Passwords do not match")
        if (password == currentPassword)
            errors.put(ErrorTag.PASSWORD, "New password may not be the same as old password")

        return if (errors.isEmpty) null else errors
    }

}