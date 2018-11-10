package com.radiotelescope.contracts.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
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
        private var userRepo: IUserRepository,
        private val userRoleRepo: IUserRoleRepository
): Command<Long, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method that, given the request passes
     * validation, will set a User's status to banned and set their account to inactive
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val theUser = userRepo.findById(id).get()

            if (theUser.status == User.Status.BANNED) {
                val errors = HashMultimap.create<ErrorTag, String>()
                errors.put(ErrorTag.STATUS, "User ${id} is already banned, cannot ban again")
                return SimpleResult(null, errors)
            }
            theUser.status = User.Status.BANNED
            theUser.active = false
            userRepo.save(theUser)

            return SimpleResult(id, null)
        }
    }

    /**
     * Method responsible for constraint checking/validation. It ensures
     * that the given user id refers to an existing entity and that said
     * entity is not an admin
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (!userRepo.existsById(id)) {
            errors.put(ErrorTag.ID, "User #$id could not be found")
            return errors
        }

        val theUserRoles = userRoleRepo.findAllByUserId(id)
        val isAdmin = theUserRoles.any {
            it.role == UserRole.Role.ADMIN
        }

        if (isAdmin)
            errors.put(ErrorTag.ROLES, "Cannot ban this user")

        return if (errors.isEmpty) null else errors
    }
}