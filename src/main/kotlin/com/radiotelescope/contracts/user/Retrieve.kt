package com.radiotelescope.contracts.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User

/**
 * Override of the [Command] interface method used to retrieve [User]
 * information
 *
 * @param id the User's id
 * @param userRepo the [IUserRepository] interface
 */
class Retrieve(
        private val id: Long,
        private val userRepo: IUserRepository,
        private val userRoleRepo: IUserRoleRepository
) : Command<UserInfo, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command] execute method. It checks to see if
     * the supplied id refers to an existing [User] Entity, and if so
     * it will retrieve it and adapt it to a [UserInfo] data class and
     * returns it.
     *
     * If the user does not exist, it will return an error
     */
    override fun execute(): SimpleResult<UserInfo, Multimap<ErrorTag, String>> {
        if(!userRepo.existsById(id)){
            val errors = HashMultimap.create<ErrorTag,String>()
            errors.put(ErrorTag.ID, "User #$id not found")
            return SimpleResult(null, errors)
        }

        val theUser = userRepo.findById(id).get()
        val theUserRole = userRoleRepo.findMembershipRoleByUserId(theUser.id)
        val theRole = theUserRole?.role

        return SimpleResult(UserInfo(theUser, theRole?.label), null)
    }
}