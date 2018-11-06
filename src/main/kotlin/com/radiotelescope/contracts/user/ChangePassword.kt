package com.radiotelescope.contracts.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseUpdateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User

/**
 * Override of the [Command] interface for a user to change their password
 *
 * @param request the [ChangePassword.request] request
 * @param userRepo the [IUserRepository] interface
 */
class ChangePassword(
        private val request: Request,
        private val userRepo: IUserRepository
) : Command<Long, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method that, given the request passes
     * validation, will change a user's password
     */

    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        val errors = validateRequest()
        if(!errors.isEmpty)
            return SimpleResult(null, errors)

        val user = userRepo.findById(request.id).get()
        val updatedUser = userRepo.save(request.updateEntity(user))

        return SimpleResult(updatedUser.id, null)
    }


    /**
     * Method responsible for constraint checking/validation. It ensures
     * that the given passwords are not blank, match, and fulfill the requirements
     * to be legitimate passwords
     */
    private fun validateRequest(): Multimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if (!userRepo.existsById(id)) {
                errors.put(ErrorTag.ID, "User #$id not found")
                return errors
            }

            if (password.isBlank())
                errors.put(ErrorTag.PASSWORD, "Required Field")
            if (currentPassword.isBlank())
                errors.put(ErrorTag.CURRENT_PASSWORD, "Required Field")
            if (passwordConfirm.isBlank())
                errors.put(ErrorTag.PASSWORD_CONFIRM, "Required Field")
            if (password != passwordConfirm)
                errors.put(ErrorTag.PASSWORD_CONFIRM, "Passwords do not match")
            if (password == currentPassword)
                errors.put(ErrorTag.CURRENT_PASSWORD, "New password may not be the same as old password")
            if (!password.matches(User.passwordRegex))
                errors.put(ErrorTag.PASSWORD, User.passwordErrorMessage)

            if (!errors.isEmpty)
                return errors

            val theUser = userRepo.findById(id).get()
            val rtPasswordEncoder = User.rtPasswordEncoder

            if (!rtPasswordEncoder.matches(currentPassword, theUser.password))
                errors.put(ErrorTag.CURRENT_PASSWORD, "Incorrect Password")
        }

        return errors
    }

    /**
     * Data class containing all fields necessary for user password change. Implements the
     * [BaseUpdateRequest] interface.
     */
    data class Request(
            val currentPassword: String,
            val password: String,
            val passwordConfirm: String,
            val id: Long
    ) : BaseUpdateRequest<User> {
        override fun updateEntity(entity: User): User {
            // Uses SHA-1 by default. Adds the salt value (secret)
            // to the password and encrypts it 50 times, specifying
            // a hash size of 256
            val passwordEncoder = User.rtPasswordEncoder
            entity.password = passwordEncoder.encode(password)

            return entity
        }
    }
}



