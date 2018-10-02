package com.radiotelescope.contracts.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.user.IUserRepository

class Retrieve(
        private val id: Long,
        private val userRepo: IUserRepository
) : Command<UserInfo, Multimap<ErrorTag, String>> {
    override fun execute(): SimpleResult<UserInfo, Multimap<ErrorTag, String>> {
        if(!userRepo.existsById(id)){
            val errors = HashMultimap.create<ErrorTag,String>()
            errors.put(ErrorTag.ID, "User id $id not found")
            return SimpleResult(null,errors)
        }

        val theUser = userRepo.findById(id).get()
        return SimpleResult(UserInfo(theUser), null)
    }
}