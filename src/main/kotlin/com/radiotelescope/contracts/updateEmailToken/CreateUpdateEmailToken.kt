package com.radiotelescope.contracts.updateEmailToken

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.generateToken
import com.radiotelescope.repository.updateEmailToken.IUpdateEmailTokenRepository
import com.radiotelescope.repository.updateEmailToken.UpdateEmailToken
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import java.util.*

/**
 * Override of the [Command] interface method used for creating
 * update email token
 *
 * @param request the [Request] object
 * @param updateEmailTokenRepo the [IUpdateEmailTokenRepository] interface
 * @param userRepo the [IUserRepository] interface
 */
class CreateUpdateEmailToken(
        private val request: CreateUpdateEmailToken.Request,
        private val updateEmailTokenRepo: IUpdateEmailTokenRepository,
        private val userRepo: IUserRepository
)  : Command<String, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method. Calls the [validateRequest] method
     * that will handle all constraint checking and validation.
     *
     * If validation passes, it will create and persist the [UpdateEmailToken] object and
     * return the token in the [SimpleResult] object.
     *
     * If validation fails, it will return a [SimpleResult] with the errors
     */
    override fun execute(): SimpleResult<String, Multimap<ErrorTag, String>> {
        val errors = validateRequest()
        if(!errors.isEmpty)
            return SimpleResult(null, errors)

        // Create Token and check that the same token string has not been created
        var token = generateToken()
        while(updateEmailTokenRepo.existsByToken(token))
            token = generateToken()

        // Create the Entity, sets its' values and save it
        val theUpdateEmailToken = request.toEntity()

        theUpdateEmailToken.token = token
        theUpdateEmailToken.user = userRepo.findById(request.userId).get()

        updateEmailTokenRepo.save(theUpdateEmailToken)

        return SimpleResult(theUpdateEmailToken.token, null)
    }

    /**
     * Method responsible for constraint checking and validations that
     * ensures the user Id refers to an existing User entity
     */
    private fun validateRequest(): Multimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()

        // Check to see if user actually exists
        with(request) {
            if (userRepo.existsById(userId)) {
                if (email == userRepo.findById(userId).get().email)
                    errors.put(ErrorTag.EMAIL, "New email may not be the same as your current email")
                if (email != emailConfirm)
                    errors.put(ErrorTag.EMAIL_CONFIRM, "Emails do not match")
                if (email.isBlank())
                    errors.put(ErrorTag.EMAIL, "Email Address may not be blank")
                if (!User.isEmailValid(email))
                    errors.put(ErrorTag.EMAIL, "Invalid Email Address")
                if (userRepo.existsByEmail(email))
                    errors.put(ErrorTag.EMAIL, "Email Address is already in use")
            } else {
                    errors.put(ErrorTag.USER_ID, "User #$userId not found")
                    return errors
            }
        }
        return errors
    }

    /**
     * Data class containing all fields necessary for user update. Implements the
     * [BaseCreateRequest] interface and overrides the [BaseCreateRequest.toEntity]
     * method
     */
    data class Request(
            var userId: Long,
            val email: String,
            val emailConfirm: String
    ) : BaseCreateRequest<UpdateEmailToken> {
        /**
         * Override of the [BaseCreateRequest.toEntity] method that will take
         * the request and create an UpdateEmailToken entity
         */
        override fun toEntity(): UpdateEmailToken {
            // Expiration date for token will be 1 day
            // NOTE: Date objects are set using milliseconds
            return UpdateEmailToken(
                    token = "NOT_TOKEN",
                    expirationDate = Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)),
                    email = email
            )

        }
    }
}