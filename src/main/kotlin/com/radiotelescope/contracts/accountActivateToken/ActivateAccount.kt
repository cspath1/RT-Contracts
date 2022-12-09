package com.radiotelescope.contracts.accountActivateToken

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.accountActivateToken.IAccountActivateTokenRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import java.util.*

/**
 * Override of the [Command] interface method used for activating
 * a User account
 *
 * @param token the Token
 * @param accountActivateTokenRepo the [IAccountActivateTokenRepository] interface
 * @param userRepo the [IUserRepository] interface
 */
class ActivateAccount(
        private val token: String,
        private val accountActivateTokenRepo: IAccountActivateTokenRepository,
        private val userRepo: IUserRepository
) : Command<Long, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command] execute method. Calls the [validateRequest] method
     * that will handle all constraint checking/validations.
     *
     * If validation passes, it will activate the user's account.
     *
     * If validation fails, it will return a [SimpleResult] with the reason for failure.
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val theUserId = activateUser()
            return SimpleResult(theUserId, null)
        }
    }

    /**
     * Private method that will activate the user associated
     * with the supplied token. It will also delete the record
     * that the token belongs to
     */
    private fun activateUser(): Long {
        val theToken = accountActivateTokenRepo.findByToken(token)

        val theUser = theToken.user
        theUser.status = User.Status.ACTIVE
        theUser.active = true

        userRepo.save(theUser)
        accountActivateTokenRepo.deleteById(theToken.id)

        return theUser.id
    }

    /**
     * Method responsible for constraint checking/validations for the
     * activate account request. It ensures that the record exists and
     * is not expired.
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (!accountActivateTokenRepo.existsByToken(token)) {
            errors.put(ErrorTag.TOKEN, "Token $token not found")
            return errors
        }

        val theToken = accountActivateTokenRepo.findByToken(token)


        if (theToken.expirationDate.before(Date())){
            errors.put(ErrorTag.EXPIRATION_DATE, "Token has expired")
        }

        if (errors.isEmpty){
            return null
        }else{
            return errors;
        }
    }
}