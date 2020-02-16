package com.radiotelescope.contracts.spectracyberConfig

import com.google.common.collect.Multimap
import com.radiotelescope.security.UserContext
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.security.AccessReport

/**
 * Wrapper that takes a [SpectracyberConfigFactory] and is responsible for all
 * user role validations for the SpectracyberConfig Entity
 *
 * @property context the [UserContext] interface
 * @property factory the [SpectracyberConfigFactory] interface
 */
class UserSpectracyberConfigWrapper (
        private val context: UserContext,
        private val factory: SpectracyberConfigFactory,
        private val userRepo: IUserRepository
) {
    /**
     * Wrapper method for the [SpectracyberConfigFactory.update] method.
     *
     * @param request the [Update.Request] object
     * @return a [Command] object
     */
    fun update(userId: Long, request: Update.Request, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        // If the user is logged in
        if (context.currentUserId() != null) {
            val theUser = userRepo.findById(context.currentUserId()!!)

            // If the user exists, they must have the same id as the to-be-deleted record
            // or they must be an admin
            if (theUser.isPresent) {
                return if (theUser.get().id == userId) {
                    context.require(
                            requiredRoles = listOf(UserRole.Role.USER),
                            successCommand = factory.update(request)
                    ).execute(withAccess)
                } else {
                    context.require(
                            requiredRoles = listOf(UserRole.Role.ADMIN),
                            successCommand = factory.update(request)
                    ).execute(withAccess)
                }
            }
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }
}