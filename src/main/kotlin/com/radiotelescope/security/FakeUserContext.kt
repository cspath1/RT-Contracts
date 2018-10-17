package com.radiotelescope.security

import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SecuredAction
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.role.UserRole

/**
 * Fake implementation of the [UserContext] object for use in in-memory tests
 */
class FakeUserContext : UserContext {
    var currentRoles = mutableListOf<UserRole.Role>()
    private var currentUserId = -1L

    /**
     * Test implementation of the [UserContext.require] method. It will check the current roles against the required roles
     * and if any are missing, this means validation has failed. In the event validation fails, we will return an [AccessReport]
     * data class containing a list of the missing roles. If validation succeeds, we will call the success command inside of the withAccess
     * anonymous function parameter, which returns a [SimpleResult] data class containing the success data type OR the failure data type
     *
     * NOTE: Kotlin can tell if the withAccess anonymous function was called or not, and the [SimpleResult] method it uses can be used in
     * by the method that called this
     */
    override fun <S, E> require(requiredRoles: List<UserRole.Role>, successCommand: Command<S, E>): SecuredAction<S, E> {
        return object : SecuredAction<S, E> {
            override fun execute(withAccess: (result: SimpleResult<S, E>) -> Unit): AccessReport? {
                val missingRoles = mutableListOf<UserRole.Role>()
                var failure = false

                requiredRoles.forEach {
                    if (!currentRoles.contains(it)) {
                        failure = true
                        missingRoles.add(it)
                    }
                }

                if (failure) {
                    return AccessReport(missingRoles = missingRoles.toList())
                }

                withAccess(successCommand.execute())
                return null
            }
        }
    }

    /**
     * Test implementation of the [UserContext.requireAny] method. It will check the current roles against the required roles
     * and if any of the required roles are found in the current roles list, then the user's action is allowed. Otherwise
     * we will return an [AccessReport] data class with a list of the missing roles
     */
    override fun <S, E> requireAny(requiredRoles: List<UserRole.Role>, successCommand: Command<S, E>): SecuredAction<S, E> {
        return object : SecuredAction<S, E> {
            override fun execute(withAccess: (result: SimpleResult<S, E>) -> Unit): AccessReport? {
                val missingRoles = mutableListOf<UserRole.Role>()

                requiredRoles.forEach {
                    if (currentRoles.contains(it)) {
                        withAccess(successCommand.execute())
                        return null
                    } else {
                        missingRoles.add(it)
                    }
                }

                return AccessReport(missingRoles = missingRoles.toList())
            }
        }
    }

    /**
     * Test implementation of the [UserContext.currentUserId] method that will return the [currentUserId] if it is greater than 0
     * (meaning they are logged in), and -1 otherwise (signifying they are not logged in
     */
    override fun currentUserId(): Long? {
        return if (currentUserId >= 0) {
            currentUserId
        } else {
            null
        }
    }

    /**
     * Used in test cases to simulate a login (where the role of Guest is added)
     * and the [currentUserId] is updated to the parameter
     */
    fun login(userId: Long) {
        currentUserId = userId
        currentRoles.add(UserRole.Role.GUEST)
    }

    /**
     * Used in test cases to simulate a logout (where the list of roles are cleared)
     * and the [currentUserId] is updated back to -1
     */
    fun logout() {
        currentUserId = -1
        currentRoles.clear()
    }
}