package com.radiotelescope.contracts.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User

/**
 * Override of the [Command] interface method used for Inviting new users by email
 *
 * @param email the email address to send to
 * @param userRepo the [IUserRepository] interface
 */
class Invite(
        private val email: String,
        private val userRepo: IUserRepository
): Command<Boolean, Multimap<ErrorTag, String>>{
    /**
     * Override of the [Command] execute method. Calls the [validateRequest] method
     * that will handle all constraint checking and validations.
     *
     * If validation passes, it will return true.
     *
     * If validation fails, it will return a [SimpleResult] with the errors and a
     * null success field
     */
    override fun execute(): SimpleResult<Boolean, Multimap<ErrorTag, String>>{
        val errors = validateRequest()

        if(!errors.isEmpty)
            return SimpleResult(null, errors)

        return SimpleResult(true, null)
    }

    /**
     * Method responsible for constraint checking and validations for the user
     * invite request. It will ensure the email is not blank. After,
     * it will check to see if the email exists in the User Table.
     */
    private fun validateRequest(): Multimap<ErrorTag, String>{
        val errors = HashMultimap.create<ErrorTag, String>()

        if(email.isBlank())
            errors.put(ErrorTag.EMAIL, "Email Address may not be blank")
        if(!User.isEmailValid(email))
            errors.put(ErrorTag.EMAIL, "Invalid Email Address")
        if(userRepo.existsByEmail(email))
            errors.put(ErrorTag.EMAIL, "Email Address is already in use")

        return errors
    }
}