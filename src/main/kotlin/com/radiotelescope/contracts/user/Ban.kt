package com.radiotelescope.contracts.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User


/**
 * Override of the [Command] interface for an admin to ban a User
 *
 * @param id the [User] id
 * @param userRepo the [IUserRepository] interface
 */
class Ban(
        private var id: Long,
        private var userRepo: IUserRepository
): Command<Long, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command] execute method. It checks to see if
     * the supplied id refers to an existing [User] Entity, and if so,
     * it will set their status to banned and respond with the user id
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        val errors = HashMultimap.create<ErrorTag,String>()

        if (!userRepo.existsById(id)) {
            errors.put(ErrorTag.ID, "User #$id could not be found")
            return SimpleResult(null, errors)
        }

        val theUser = userRepo.findById(id).get()

        theUser.status = User.Status.Banned
        theUser.active = false
        userRepo.save(theUser)

        return SimpleResult(id, null)
    }
}