package com.radiotelescope.contracts.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User

class Delete(
        private val id: Long,
        private val userRepo: IUserRepository
) : Command<Long, Multimap<ErrorTag, String>> {
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val theUser = userRepo.findById(id).get()
            disableUser(theUser)
            return SimpleResult(theUser.id, null)
        }
    }

    private fun disableUser(user: User) {
        user.status = User.Status.Deleted
        user.active = false
    }

    private fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (!userRepo.existsById(id)) {
            errors.put(ErrorTag.ID, "User Id #$id not found")
        }

        return if (errors.isEmpty) null else errors
    }
}