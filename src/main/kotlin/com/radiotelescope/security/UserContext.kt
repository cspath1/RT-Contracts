package com.radiotelescope.security

import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SecuredAction
import com.radiotelescope.repository.role.UserRole

/**
 * Provides abstract method definitions for role and permission checking
 */
interface UserContext {

    /**
     * Abstract method declaration used when ALL roles in the [Role] [List]
     * are required to pass validation
     *
     * @param requiredRoles a list of all required [Role]
     * @param successCommand [Command] object to be called upon successful authentication
     * @param failureCommand a [UserPreconditionFailure] that will respond when authentication fails
     * @return a [SecuredAction] object
     */
    fun <S, E> require(
        requiredRoles: List<UserRole.Role>,
        successCommand: Command<S, E>
    ): SecuredAction<S, E>

    /**
     * Abstract method declaration used when any role in the [Role] [List]
     * is sufficient for validation
     *
     * @param requiredRoles a list of any of the required [Role] values.
     * @param successCommand [Command] object to be called upon successful authentication
     * @param failureCommand a [UserPreconditionFailure] that will respond when authentication fails
     * @return a [SecuredAction] object
     */
    fun <S, E> requireAny(
            requiredRoles: List<UserRole.Role>,
            successCommand: Command<S, E>
    ): SecuredAction<S, E>

    /**
     * Abstract method to grab the user id of the currently logged in user
     */
    fun currentUserId(): Long?
}

/**
 * Data class that will keep track of all of the missing roles a user
 * had for a request
 *
 * @param missingRoles a [List] of missing [UserRole.Role] values
 */
data class AccessReport(val missingRoles: List<UserRole.Role>)