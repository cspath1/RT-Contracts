package com.radiotelescope.controller.model.resetPasswordToken

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.resetPasswordToken.ErrorTag
import com.radiotelescope.contracts.resetPasswordToken.ResetPassword
import com.radiotelescope.controller.model.BaseForm
import com.radiotelescope.repository.user.User

/**
 * Update form that takes nullable versions of the [ResetPassword.Request] object.
 * It is in charge of making sure these values are not null before adapting
 * it into a [ResetPassword.Request] object
 *
 * @param password the User's password
 * @param passwordConfirm the User's passwordConfirm

 */
data class UpdateForm(
        val password: String?,
        val passwordConfirm: String?
) : BaseForm<ResetPassword.Request> {
    /**
     * Override of the [BaseForm.toRequest] method that adapts
     * the form into [ResetPassword.Request] object
     */
    override fun toRequest(): ResetPassword.Request {
        return ResetPassword.Request(
                password = password!!,
                passwordConfirm = passwordConfirm!!
        )
    }

    /**
     * Makes sure all of the required fields are not null or blank
     *
     * @return a [HashMultimap] of errors or null
     */
    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (password.isNullOrBlank())
            errors.put(ErrorTag.PASSWORD, "Password may not be blank")
        else if (!password!!.matches(User.passwordRegex))
            errors.put(ErrorTag.PASSWORD, User.PASSWORD_ERROR_MESSAGE)
        if (password != passwordConfirm)
            errors.put(ErrorTag.PASSWORD_CONFIRM, "Passwords do not match")

        return if (errors.isEmpty) null else errors
    }

}