package com.radiotelescope.contracts.user

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext
import com.radiotelescope.security.crud.UserPageable
import com.radiotelescope.security.crud.UserRetrievable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Wrapper that takes a [UserFactory] and is responsible for all
 * user role validations for endpoints for the User Entity
 *
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
) : UserRetrievable<Long, SimpleResult<UserInfo, Multimap<ErrorTag, String>>>,
UserPageable<Pageable, SimpleResult<Page<UserInfo>, Multimap<ErrorTag, String>>>{
    /**
     * Register function that will return a [Register] command object. This does not need any
     * user role authentication since the user will not be signed in at the time
     *
     * @param request the [Register.Request] object
     * @return a [Register] command object
     */
    fun register(request: Register.Request): Command<Long, Multimap<ErrorTag, String>> {
        return Register(
                request = request,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        )
    }

    /**
     * Authenticate function that will return a [Authenticate] command object. This does not need
     * any user role authentication since the user will not be logged in at the time
     *
     * @param request the [Authenticate.Request] object
     * @return a [Authenticate] command object
     */
    fun authenticate(request: Authenticate.Request): Command<UserInfo, Multimap<ErrorTag, String>> {
        return Authenticate(
                request = request,
                userRepo = userRepo
        )
    }

    /**
     * Concrete implementation of the [UserRetrievable] interface used to add Spring Security
     * authentication to the [Retrieve] command object
     *
     * @param request the User id
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    override fun retrieve(request: Long, withAccess: (result: SimpleResult<UserInfo, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        // If the user is logged in
        if (context.currentUserId() != null) {
            val theUser = userRepo.findById(context.currentUserId()!!)

            // If the user exists, they must either be the owner or an admin
            if (theUser.isPresent) {
                return if (theUser.isPresent && theUser.get().id == request) {
                    context.require(
                            requiredRoles = listOf(UserRole.Role.USER),
                            successCommand = factory.retrieve(request)
                    ).execute(withAccess)
                } else {
                    context.require(
                            requiredRoles = listOf(UserRole.Role.ADMIN),
                            successCommand = factory.retrieve(request)
                    ).execute(withAccess)
                }
            }
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER))
    }

    /**
     * Concrete implementation of the [UserPageable] interface used to add Spring Security
     * authentication to the [List] command object
     *
     * @param request the [Pageable] object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    override fun pageable(request: Pageable, withAccess: (result: SimpleResult<Page<UserInfo>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if (context.currentUserId() != null)
            return context.require(
                    requiredRoles = listOf(UserRole.Role.ADMIN),
                    successCommand = factory.list(request)
            ).execute(withAccess)


        return AccessReport(missingRoles = listOf(UserRole.Role.USER, UserRole.Role.ADMIN))
    }

}