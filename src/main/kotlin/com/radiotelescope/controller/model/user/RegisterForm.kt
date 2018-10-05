package com.radiotelescope.controller.model.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.contracts.user.Register
import com.radiotelescope.controller.model.BaseCreateForm
import com.radiotelescope.repository.role.UserRole

/**
 * Register for that takes nullable versions of the [Register.Request] object.
 * It is in charge of making sure these values are not null before adapting it
 * into a [Register.Request] object
 *
 * @param firstName the User's proposed first name
 * @param lastName the User's proposed last name
 * @param email the User's proposed email address
 * @param phoneNumber the User's proposed phone number
 * @param password the User's proposed password
 * @param passwordConfirm a repeated entry of the User's proposed password
 * @param company the User's proposed company they are associated with
 * @param categoryOfService the User's proposed [UserRole.Role]
 */
data class RegisterForm(
        val firstName: String?,
        val lastName: String?,
        val email: String?,
        val phoneNumber: String?,
        val password: String?,
        val passwordConfirm: String?,
        val company: String?,
        val categoryOfService: UserRole.Role?
) : BaseCreateForm<Register.Request> {
    /**
     * Override of the [BaseCreateForm.toRequest] method that adapts
     * the form into a [Register.Request] object
     */
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

    /**
     * Makes sure all of the required fields are not null or blank
     *
     * @return a [HashMultimap] of errors or null
     */
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