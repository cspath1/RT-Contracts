package com.radiotelescope.contracts.updateEmailToken

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.user.UserFactory
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.updateEmailToken.UpdateEmailToken
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext

/**
 * Wrapper that takes a [UpdateEmailTokenFactory] and is responsible for all
 * user role validations for endpoints for the [UpdateEmailToken] Entity
 *
 * @property context the [UserContext] interface
 * @property factory the [UserFactory] factory interface
 * @property userRepo the [IUserRepository] interface */
class UserUpdateEmailTokenWrapper (
        private val context: UserContext,
        private val factory: UpdateEmailTokenFactory,
        private val userRepo: IUserRepository){

    fun requestUpdateEmail(request: CreateUpdateEmailToken.Request, withAccess: (result: SimpleResult<String, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        // If the user is logged in
        if (context.currentUserId() != null) {
            val theUser = userRepo.findById(context.currentUserId()!!)

            // If the user exists, they must either be the owner or an admin
            if (theUser.isPresent) {
                return if (theUser.isPresent && theUser.get().id == request.userId) {
                    context.require(
                            requiredRoles = listOf(UserRole.Role.USER),
                            successCommand = factory.requestUpdateEmail(request)
                    ).execute(withAccess)
                } else {
                    context.require(
                            requiredRoles = listOf(UserRole.Role.ADMIN),
                            successCommand = factory.requestUpdateEmail(request)
                    ).execute(withAccess)
                }
            }
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }
}