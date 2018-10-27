package com.radiotelescope.security

import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SecuredAction
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.security.service.RetrieveAuthService
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component

/**
 * Concrete implementation of [UserContext] interface that uses Spring Security to validate
 * if a user has the ability to execute an action or not
 *
 * This is declared as a Component with a Bean value of "UserContext" so that it
 * can be autowired by Spring when the application runs, allowing for the [Logger]
 * service to be instantiated automatically. This is very important because it allows
 * us to use the [FakeUserContext] with the [Logger] so it can still be tested
 *
 * @param userRepo the [IUserRepository] interface
 * @param userRoleRepo the [IUserRoleRepository]
 */
@Component("userContext")
class UserContextImpl(
        private var userRepo: IUserRepository,
        private var userRoleRepo: IUserRoleRepository,
        private var retrieveAuthService: RetrieveAuthService
) : UserContext {

    /**
     * Override of the [UserContext.require] method that uses Spring Security to check if a user has
     * all of the roles in the requiredRoles parameter. If they do, it will call the success command.
     * If not, it will return the list of missing roles
     */
    override fun <S, E> require(requiredRoles: List<UserRole.Role>, successCommand: Command<S, E>): SecuredAction<S, E> {
        var missingRoles: MutableList<UserRole.Role>? = mutableListOf()

        val (session, _) = retrieveAuthService.execute()

        // If the authentication object exists, we can check the user's roles
        if (session != null) {
            val authorities = arrayListOf<SimpleGrantedAuthority>()

            // Add all of the user's roles to the list
            session.roles.forEach {
                authorities.add(SimpleGrantedAuthority(it.authority))
            }


            // Check if the token's user id is null or refers to a non-existent user
            if (!userRepo.existsById(session.userId))
                missingRoles?.add(UserRole.Role.GUEST)
            // Otherwise, we can check if the proper roles
            else {
                // Grab all of the roles and check them agains the required list
                val userRoles = userRoleRepo.findAllByUserId(session.userId)
                if (!requiredRoles.isEmpty()) {
                    requiredRoles.forEach { role: UserRole.Role ->
                        val hasThisRole = userRoles.any {
                            it.role == role
                        }
                        if (!hasThisRole) {
                            missingRoles?.add(role)
                        }
                    }
                }
            }
        }
        // Otherwise, they are not logged in
        else
            missingRoles?.add(UserRole.Role.USER)

        // If the missing roles list is empty
        // they have passed authentication
        if (missingRoles?.isEmpty() == true)
            missingRoles = null

        return object : SecuredAction<S, E> {
            override fun execute(withAccess: (result: SimpleResult<S, E>) -> Unit): AccessReport? {
                // Either return an AccessReport of the missing roles, or call the success command
                return missingRoles?.let { AccessReport(missingRoles = it, invalidResourceId = null) } ?: let {
                    withAccess(successCommand.execute())
                    null
                }
            }
        }
    }

    /**
     * Override of the [UserContext.requireAny] method that uses Spring Security to check if a user has
     * any of the roles in the requiredRoles parameter. If they do, it will call the success command.
     * If not, it will return a list of the missing roles
     */
    override fun <S, E> requireAny(requiredRoles: List<UserRole.Role>, successCommand: Command<S, E>): SecuredAction<S, E> {
        var hasAnyRole = false

        val (session, _) = retrieveAuthService.execute()

        // If the authentication object exists, we can actually check the roles
        if (session != null) {
            val authorities = arrayListOf<SimpleGrantedAuthority>()

            // Add all of the user's roles to the list
            session.roles.forEach {
                authorities.add(SimpleGrantedAuthority(it.authority))
            }

            // If the user id exists and refers to an existing user, we can grab their roles
            if (userRepo.existsById(session.userId)) {
                val roles = userRoleRepo.findAllByUserId(session.userId)

                // If anything in the roles list matches anything in the require roles
                // list, set the variable to true. If the required role list is empty,
                // set the variable to true
                hasAnyRole = if (!requiredRoles.isEmpty()) {
                    roles.any { role ->
                        requiredRoles.any {
                            it == role.role
                        }
                    }
                } else {
                    true
                }
            }
        }

        return object : SecuredAction<S, E> {
            override fun execute(withAccess: (result: SimpleResult<S, E>) -> Unit): AccessReport? {
                // If hasAnyRole is true, call the success command
                // and return null
                return if (hasAnyRole) {
                    withAccess(successCommand.execute())
                    null
                } else
                    // Otherwise return the requireRoles list
                    // in the AccessReport
                    AccessReport(missingRoles = requiredRoles, invalidResourceId = null)
            }
        }
    }

    /**
     * Override of the [UserContext.currentUserId] method that will grab the authentication
     * token if it exists and return the userId, otherwise it will return null
     */
    override fun currentUserId(): Long? {
        val (session, _) = retrieveAuthService.execute()

        return session?.userId
    }


}