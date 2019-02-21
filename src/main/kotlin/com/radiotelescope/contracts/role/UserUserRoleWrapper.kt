package com.radiotelescope.contracts.role

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext
import com.radiotelescope.toStringMap
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Wrapper responsible for adding user role validations for
 * endpoints for the UserRole Entity
 *
 * @property context the [UserContext] interface
 * @property factory the [UserRoleFactory] interface
 * @property userRepo the [IUserRepository] interface
 * @property userRoleRepo the [IUserRoleRepository] interface
 */
class UserUserRoleWrapper(
        private val context: UserContext,
        private val factory: UserRoleFactory,
        private val userRepo: IUserRepository,
        private val userRoleRepo: IUserRoleRepository
) {
    /**
     * Wrapper method for the [UserRoleFactory.unapprovedList] method that adds Spring
     * Security authentication to the [UnapprovedList] command object
     *
     * @param pageable the [Pageable] request
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun unapprovedList(pageable: Pageable, withAccess: (result: SimpleResult<Page<UserRoleInfo>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if (context.currentUserId() != null)
            return context.require(
                    requiredRoles = listOf(UserRole.Role.ADMIN),
                    successCommand = factory.unapprovedList(pageable)
            ).execute(withAccess)

        return AccessReport(missingRoles = listOf(UserRole.Role.USER, UserRole.Role.ADMIN), invalidResourceId = null)
    }

    /**
     * Wrapper method for the [UserRoleFactory.validate] method that adds Spring
     * Security authentication to the [Validate] command object
     *
     * @param request the [Validate.Request] object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun validate(request: Validate.Request, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        context.currentUserId()?.let {
            return context.require(
                    requiredRoles = listOf(UserRole.Role.ADMIN),
                    successCommand = factory.validate(request)
            ).execute(withAccess)
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER, UserRole.Role.ADMIN), invalidResourceId = null)
    }

    /**
     * Wrapper method for the [UserRoleFactory.retrieve] method that adds Spring
     * Security authentication to the [Retrieve] command object
     *
     * @param id the UserRole id
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun retrieve(id: Long, withAccess: (result: SimpleResult<UserRoleInfo, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        context.currentUserId()?.let {
            return context.require(
                    requiredRoles = listOf(UserRole.Role.ADMIN),
                    successCommand = factory.retrieve(id)
            ).execute(withAccess)
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER, UserRole.Role.ADMIN), invalidResourceId = null)
    }

    /**
     * Wrapper method for the [UserRoleFactory.requestRole] method that adds Spring
     * Security authentication to the [RequestRole] command object
     *
     * @param request the [RequestRole.Request] object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun requestRole(request: RequestRole.Request, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if(context.currentUserId() != null){
            val theUser = userRepo.findById(context.currentUserId()!!)

            // If the user exists, they must either be the owner or an admin
            if (theUser.isPresent) {
                return if (theUser.isPresent && theUser.get().id == request.userId) {
                    context.require(
                            requiredRoles = listOf(UserRole.Role.USER),
                            successCommand = factory.requestRole(request)
                    ).execute(withAccess)
                } else {
                    val theRequestedUser = userRepo.findById(request.userId)

                    return if (!theRequestedUser.isPresent)
                        AccessReport(missingRoles = null, invalidResourceId = invalidUserIdErrors(request.userId))
                    else
                        context.require(
                                requiredRoles = listOf(UserRole.Role.ADMIN),
                                successCommand = factory.requestRole(request)
                        ).execute(withAccess)
                }
            }
        }
        return AccessReport(missingRoles = listOf(UserRole.Role.ADMIN), invalidResourceId = null)
    }

    /**
     * Private method to return a [Map] of errors when a user could not be found.
     *
     * @param id the user id
     * @return a [Map] of errors
     */
    private fun invalidUserIdErrors(id: Long): Map<String, Collection<String>> {
        val errors = HashMultimap.create<com.radiotelescope.contracts.user.ErrorTag, String>()
        errors.put(com.radiotelescope.contracts.user.ErrorTag.ID, "User #$id could not be found")
        return errors.toStringMap()
    }
}