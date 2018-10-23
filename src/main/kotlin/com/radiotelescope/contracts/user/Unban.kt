package com.radiotelescope.contracts.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User

class Unban(
        private val id: Long,
        private val userRepo: IUserRepository
) : Command<Long, Multimap<ErrorTag, String>> {
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val theUser = userRepo.findById(id).get()
            unbanUser(theUser)
            return SimpleResult(theUser.id, null)
        }
    }

    private fun unbanUser(user: User) {
        user.status = User.Status.Active
        user.active = true
    }

    private fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        //user does not exist
        if (!userRepo.existsById(id)) {
            errors.put(ErrorTag.ID, "User Id #$id not found")
        } //user exists
        else{
            if (userRepo.findById(id).get().status == User.Status.Active){
                errors.put(ErrorTag.ID, "User found by Id #$id is not banned")
            }
            if (userRepo.findById(id).get().active){
                errors.put(ErrorTag.ID, "User found by id #$id is already active")
            }
        }
        return if (errors.isEmpty) null else errors
    }
}