package com.radiotelescope.contracts.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User

/**
 * Command class for a User to cancel his own Account
 * @param userId the id of a user as a [Long]
 * @param userRepo the [IUserRepository] repository
 * @return a Command object
 */

class Cancel(
        val userRepo: IUserRepository,
        val userId: Long
        ): Command<Long, Multimap<ErrorTag, String>>
{

    /**
     * Override of the execute method for the Command object
     * @return a [SimpleResult] with the success parameter as the id of the user
     *
     * If the user does not exist, populate the errors map and return it in the simpleResult,
     * otherwise, return null for the Map and return the userId as the success param.
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>>
    {
     val errors = HashMultimap.create<ErrorTag, String>()
        if (!userRepo.existsById(userId))
        {
            errors.put(ErrorTag.ID, "userId $userId is not found")
            return SimpleResult(null, errors)
        }

        val u = userRepo.findById(userId).get()
        u.status = User.Status.Deleted
        userRepo.save(u)
        return SimpleResult(userId, null)
    }
}