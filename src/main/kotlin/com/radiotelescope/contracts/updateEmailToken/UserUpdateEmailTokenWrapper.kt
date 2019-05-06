package com.radiotelescope.contracts.updateEmailToken

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
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
        private val userRepo: IUserRepository
) {
    /**
     * Wrapper method for the [UpdateEmailTokenFactory.requestUpdateEmail] method that adds Spring
     * Security authentication to the [CreateUpdateEmailToken] command object.
     *
     * @param request the [CreateUpdateEmailToken.Request] object
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun requestUpdateEmail(request: CreateUpdateEmailToken.Request, withAccess: (result: SimpleResult<String, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        // If the user is logged in
        if (context.currentUserId() != null) {
            val theUser = userRepo.findById(context.currentUserId()!!)

            // If the user exists, they must either be the owner
            if (theUser.isPresent && theUser.get().id == request.userId) {
                return context.require(
                        requiredRoles = listOf(UserRole.Role.USER),
                        successCommand = factory.requestUpdateEmail(
                                request = request
                        )
                ).execute(withAccess)
            }
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }

    /**
     * Reset Password function that will return a [UpdateEmail] command object.
     * This does not need any user role authentication since the user will not be signed in a the time
     *
     * @param token the update user email token
     * @return a [UpdateEmail] command object
     */
    fun updateEmail(token: String) : Command<Long, Multimap<ErrorTag, String>> {
        return factory.updateEmail(
                token = token
        )
    }
}