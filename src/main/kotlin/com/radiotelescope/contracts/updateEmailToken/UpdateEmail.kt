package com.radiotelescope.contracts.updateEmailToken

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.updateEmailToken.IUpdateEmailTokenRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import java.util.*

/**
 * Override of the [Command] interface method used for User update email
 *
 * @param token the update email token
 * @param token the reset password token
 * @param updateEmailTokenRepo the [IUpdateEmailTokenRepository]
 * @param userRepo the [IUserRepository]
 */
class UpdateEmail (
        private val token: String,
        private val updateEmailTokenRepo: IUpdateEmailTokenRepository,
        private val userRepo: IUserRepository
) : Command<Long, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command] execute method. Calls the [validateRequest] method
     * that will handle all constraint checking and validations.
     *
     * If validation passes, it will update and persist the [User] object.
     * It will then return a [SimpleResult] object with the [User] id
     * and a null errors field.
     *
     * If validation fails, it will return a [SimpleResult] with the errors and a
     * null success field
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        val errors = validateRequest()

        if(!errors.isEmpty)
            return SimpleResult(null, errors)

        val theToken = updateEmailTokenRepo.findByToken(token)
        val user = theToken.user
        user.email = theToken.email
        val updatedUser = userRepo.save(user)
        updateEmailTokenRepo.delete(theToken)

        return SimpleResult(updatedUser.id, null)
    }

    /**
     * Method responsible for constraint checking and validations for the user
     * update email. It will ensure that the token is not blank, does exist and
     * is not expired.
     */
    private fun validateRequest(): Multimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()

        if(token.isBlank())
            errors.put(ErrorTag.TOKEN, "Update Email Token is invalid")
        if(!updateEmailTokenRepo.existsByToken(token))
            errors.put(ErrorTag.TOKEN, "Update Email Token not found")
        else{
            if(updateEmailTokenRepo.findByToken(token).expirationDate.before(Date()))
                errors.put(ErrorTag.TOKEN, "Token is expired")
        }


        return errors
    }
}