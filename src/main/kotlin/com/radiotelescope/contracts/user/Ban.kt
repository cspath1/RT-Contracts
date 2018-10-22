package com.radiotelescope.contracts.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User


/**
 * Command class which takes a
 * userRepo of type [IUserRepository],
 * and user_id of type [Long]
 *
 */
class Ban(

        private var userRepo: IUserRepository,
        private var user_id: Long

): Command<Long, Multimap<ErrorTag, String>> {

    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>>
    {
        var errors = HashMultimap.create<ErrorTag,String>()

        if (!userRepo.existsById(user_id))
        {
            errors.put(ErrorTag.ID, "User $user_id does not exist by id")
            return SimpleResult(null, errors)
        }
        //success case: Admin bans a user
        val u: User = userRepo.findById(user_id).get()
        //So when a user gets banned BY an admin--
        //this happens
            u.status = User.Status.Banned
            userRepo.save(u)
            return SimpleResult(user_id, null)
    }
}