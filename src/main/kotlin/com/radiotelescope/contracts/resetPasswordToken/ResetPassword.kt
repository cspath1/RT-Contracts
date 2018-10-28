package com.radiotelescope.contracts.resetPasswordToken

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseUpdateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.resetPasswordToken.IResetPasswordTokenRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder

/**
 * Override of the [Command] interface method used for User reset password
 *
 * @param request the [ResetPassword.Request] object
 * @param token the reset password token
 * @param resetPasswordTokenRepo the [IResetPasswordTokenRepository]
 * @param userRepo the [IUserRepository]
 */
class ResetPassword (
        private val request: ResetPassword.Request,
        private val token: String,
        private val resetPasswordTokenRepo: IResetPasswordTokenRepository,
        private val userRepo: IUserRepository
) : Command<Long, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command] execute mehtod. Calls the [validateRequest] method
     * that will handle all constraint checking and validations.
     *
     * If validation passes, it will update and persist the [User] object.
     * It will then return a [SimpleResult] object with the [User] id
     * and a null errors field.
     *
     * If validation fields, it will return a [SimpleResult] with the errors and a
     * null success field
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        val errors = validateRequest()

        if(!errors.isEmpty)
            return SimpleResult(null, errors)

        val user = resetPasswordTokenRepo.findByToken(token).user!!
        val updatedUser = userRepo.save(request.updateEntity(user))
        return SimpleResult(updatedUser.id, null)
    }

    /**
     * Method responsible for constraint checking and validations for the user
     * reset password request. It will ensure that the password is not blank and matches the
     * password confirm field
     */
    private fun validateRequest(): Multimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if (password.isBlank())
                errors.put(ErrorTag.PASSWORD, "Password may not be blank")
            if (password != passwordConfirm)
                errors.put(ErrorTag.PASSWORD_CONFIRM, "Passwords do not match")
            if (!password.matches(User.passwordRegex))
                errors.put(ErrorTag.PASSWORD, User.passwordErrorMessage)
        }

        return errors
    }

    /**
     * Data class containing all fields necessary for user reset password. Implements the
     * [BaseUpdateRequest] interface and overrides the [BaseUpdateRequest.updateEntity]
     * method
     */
    data class Request(
            val password: String,
            val passwordConfirm: String
    ) : BaseUpdateRequest<User>{
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