package com.radiotelescope.contracts.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User

/**
 * Override of the [Command] interface for an admin to unban a User
 *
 * @param id the [User] id
 * @param userRepo the [IUserRepository] interface
 */
class Unban(
        private val id: Long,
        private val userRepo: IUserRepository
) : Command<Unban.Response, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method that, given the request passes
     * validation, will set a User's status to ACTIVE and mark the active flag
     * back to true
     */
    override fun execute(): SimpleResult<Unban.Response, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val theUser = userRepo.findById(id).get()
            unbanUser(theUser)

            val theResponse = Response(
                    id = id,
                    email = theUser.email
            )
            return SimpleResult(theResponse, null)
        }
    }

    /**
     * Private method that handles updating the user
     * entity to an unbanned state
     */
    private fun unbanUser(user: User) {
        user.status = User.Status.ACTIVE
        user.active = true
        userRepo.save(user)
    }

    /**
     * Method responsible for constraint checking/validation. It ensures
     * that the given user id exists and is not an already active and unbanned
     * user
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        // User does not exist
        if (!userRepo.existsById(id)) {
            errors.put(ErrorTag.ID, "User Id #$id not found")
        } // User exists
        else {
            val theUser = userRepo.findById(id).get()
            if(theUser.status != User.Status.BANNED)
                errors.put(ErrorTag.STATUS, "User found by Id #$id is not banned")
            if (theUser.active){
                errors.put(ErrorTag.ID, "User found by id #$id is already active")
            }
        }
        return if (errors.isEmpty) null else errors
    }

    /**
     * Data class containing all fields returned from unban
     *
     * @param id the [User] id
     * @param email the [User] email
     */
    data class Response(
            val id: Long,
            val email: String
    )
}