package com.radiotelescope.contracts.user

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext
import com.radiotelescope.security.crud.UserUpdatable

/**
 * Wrapper that takes a [UserFactory] and is responsible for all
 * user role validations for our endpoints for the User Entity
 * @property context the [UserContext] interface
 * @property factory the [UserFactory] factory interface
 * @property userRepo the [IUserRepository] interface
 * @property userRoleRepo the [IUserRoleRepository] interface
 */
class UserUserWrapper(
        private val context: UserContext,
        private val factory: UserFactory,
        private val userRepo: IUserRepository,
        private val userRoleRepo: IUserRoleRepository
) : UserUpdatable<Update.Request, SimpleResult<Long, Multimap<ErrorTag, String>>> {
    override fun update(request: Update.Request, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        // TODO - Add base UserRole.Role -> User for any user to use for requiredRoles
        // If the user is not logged in, refers to a valid account, and is updating THEIR account
        if (context.currentUserId() != null) {
            val theUser = userRepo.findById(context.currentUserId()!!)
            if (theUser.isPresent) {
                if (theUser.get().id != request.id) {
                    context.require(
                            requiredRoles = listOf(UserRole.Role.GUEST),
                            successCommand = factory.update(request)
                    ).execute(withAccess)
                }
            }
        }

        // Otherwise, return an error
        return AccessReport(missingRoles = listOf(UserRole.Role.GUEST))
    }

    fun register(request: Register.Request): Command<Long, Multimap<ErrorTag, String>> {
        return Register(
                request = request,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        )
    }
}