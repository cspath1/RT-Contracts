package com.radiotelescope.contracts.resetPasswordToken

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.resetPasswordToken.IResetPasswordTokenRepository
import com.radiotelescope.repository.resetPasswordToken.ResetPasswordToken
import com.radiotelescope.repository.user.IUserRepository
import java.util.*

/**
 * Override of the [Command] interface method used for Appointment creation
 *
 * @param email the User's email
 * @param resetPasswordTokenRepo the [IResetPasswordTokenRepository] interface
 * @param userRepo the [IUserRepository] interface
 */
class CreateResetPasswordToken (
        private val email: String,
        private val resetPasswordTokenRepo: IResetPasswordTokenRepository,
        private val userRepo: IUserRepository
) : Command<String, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method. Calls the [validateRequest] method
     * that will handle all constraint checking and validation.
     *
     * If validation passes, it will create and persist the [ResetPasswordToken] object and
     * return the token in the [SimpleResult] object.
     *
     * If validation fails, it will return a [SimpleResult] with the errors
     */
    override fun execute(): SimpleResult<String, Multimap<ErrorTag, String>> {
        val errors = validateRequest()
        if (!errors.isEmpty)
            return SimpleResult(null, errors)

        // Delete any previous tokens
        val listToken = resetPasswordTokenRepo.findAllByUserId(
                userRepo.findByEmail(email)!!.id
        )
        listToken.forEach{
            resetPasswordTokenRepo.delete(it)
        }

        // Create Token and check that the same token string has not been created
        var token = UUID.randomUUID().toString().replace("-", "", false)
        while(resetPasswordTokenRepo.existsByToken(token))
            token = UUID.randomUUID().toString().replace("-", "", false)

        // Expiration date for token will be 1 day
        // NOTE: Date objects are set using milliseconds
        val theResetPasswordToken = ResetPasswordToken(
                token = token,
                expirationDate = Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000))
        )

        theResetPasswordToken.user = userRepo.findByEmail(email)
        resetPasswordTokenRepo.save(theResetPasswordToken)

        return SimpleResult(theResetPasswordToken.token, null)
    }

    /**
     * Method responsible for constraint checking and validations that
     * ensures the user Id refers to an existing User entity
     */
    private fun validateRequest(): Multimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()

        // Check to see if user actually exists
        if (!userRepo.existsByEmail(email))
            errors.put(ErrorTag.EMAIL, "No User is found with specified email")

        return errors
    }
}