package com.radiotelescope.contracts.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.model.user.SearchCriteria
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext
import com.radiotelescope.toStringMap
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import kotlin.collections.List

/**
 * Wrapper that takes a [UserFactory] and is responsible for all
 * user role validations for endpoints for the User Entity
 *
 * @property context the [UserContext] interface
 * @property factory the [UserFactory] factory interface
 * @property userRepo the [IUserRepository] interface
 */
class UserUserWrapper(
        private val context: UserContext,
        private val factory: UserFactory,
        private val userRepo: IUserRepository
) {
    /**
     * Register function that will return a [Register] command object. This does not need any
     * user role authentication since the user will not be signed in at the time
     *
     * @param request the [Register.Request] object
     * @return a [Register] command object
     */
    fun register(request: Register.Request): Command<Register.Response, Multimap<ErrorTag, String>> {
        return factory.register(request)
    }

    /**
     * Authenticate function that will return a [Authenticate] command object. This does not need
     * any user role authentication since the user will not be logged in at the time
     *
     * @param request the [Authenticate.Request] object
     * @return a [Authenticate] command object
     */
    fun authenticate(request: Authenticate.Request): Command<UserInfo, Multimap<ErrorTag, String>> {
        return factory.authenticate(request)
    }

    /**
     * Wrapper method for the [UserFactory.retrieve] method used to add Spring Security
     * authentication to the [Retrieve] command object
     *
     * @param request the User id
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun retrieve(request: Long, withAccess: (result: SimpleResult<UserInfo, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if (context.currentUserId() != null) {
            if (!userRepo.existsById(request)) {
                return AccessReport(missingRoles = null, invalidResourceId = invalidUserIdErrors(request))
            } else {
                // If the user exists, they must either be the owner or an admin
                val theUser = userRepo.findById(request).get()
                return if (theUser.id == context.currentUserId()) {
                    context.require(
                            requiredRoles = listOf(UserRole.Role.USER),
                            successCommand = factory.retrieve(request)
                    ).execute(withAccess)
                } else {
                    context.requireAny(
                            requiredRoles = listOf(UserRole.Role.ADMIN, UserRole.Role.ALUMNUS),
                            successCommand = factory.retrieve(request)
                    ).execute(withAccess)
                }
            }
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }

    /**
     * Wrapper method for [UserFactory.list] method used to add Spring Security
     * authentication to the [List] command object
     *
     * @param request the [Pageable] object
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun list(request: Pageable, withAccess: (result: SimpleResult<Page<UserInfo>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if (context.currentUserId() != null) {
            return context.requireAny(
                    requiredRoles = listOf(UserRole.Role.ADMIN, UserRole.Role.ALUMNUS),
                    successCommand = factory.list(request)
            ).execute(withAccess)
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER, UserRole.Role.ADMIN, UserRole.Role.ALUMNUS), invalidResourceId = null)
    }

    /**
     * Wrapper method for the [UserFactory.update] method used to add Spring Security
     * authentication to the [Update] command object
     *
     * @param request the [Update.Request] object
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun update(request: Update.Request, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        // If the user is logged in
        if (context.currentUserId() != null) {
            if (!userRepo.existsById(request.id)) {
                return AccessReport(missingRoles = null, invalidResourceId = invalidUserIdErrors(request.id))
            } else {
                // If the user exists, they must either be the owner or an admin
                val theUser = userRepo.findById(request.id).get()
                return if (theUser.id == context.currentUserId()) {
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

    /**
     * Wrapper method for the [UserFactory.updateProfilePicture] method used to add
     * Spring Security authentication to the [UpdateProfilePicture] command object
     *
     * @param request the [UpdateProfilePicture.Request] object
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun updateProfilePicture(request: UpdateProfilePicture.Request, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        // If the user is logged in
        if (context.currentUserId() != null) {
            if (!userRepo.existsById(request.id)) {
                return AccessReport(missingRoles = null, invalidResourceId = invalidUserIdErrors(request.id))
            } else {
                // If the user exists, they must either be the owner or an admin
                val theUser = userRepo.findById(request.id).get()
                return if (theUser.id == context.currentUserId()) {
                    context.require(
                            requiredRoles = listOf(UserRole.Role.USER),
                            successCommand = factory.updateProfilePicture(request)
                    ).execute(withAccess)
                } else {
                    context.require(
                            requiredRoles = listOf(UserRole.Role.ADMIN),
                            successCommand = factory.updateProfilePicture(request)
                    ).execute(withAccess)
                }
            }
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }

    /**
     * Wrapper method for the [UserFactory.approveDenyProfilePicture] method used to add
     * Spring Security authentication to the [ApproveDeny] command object
     *
     * @param request the [ApproveDeny.Request] object
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun approveDenyProfilePicture(request: ApproveDeny.Request, withAccess: (result: SimpleResult<User, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        // If the user is logged in
        if (context.currentUserId() != null) {
            return context.require(
                    requiredRoles = listOf(UserRole.Role.ADMIN),
                    successCommand = factory.approveDenyProfilePicture(request)
            ).execute(withAccess)
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }

    /**
     *  Wrapper method for the [UserFactory.delete] method that adds Spring
     *  Security authentication to the [Delete] command object
     *
     *  @param id the User id
     *  @param withAccess anonymous function that uses the command's result object
     *  @return An [AccessReport] if authentication fails, null otherwise
     */
    fun delete(id: Long, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        // If the user is logged in
        if (context.currentUserId() != null) {
            val theUser = userRepo.findById(context.currentUserId()!!)

            // If the user exists, they must have the same id as the to-be-deleted record
            // or they must be an admin
            if (theUser.isPresent) {
                return if (theUser.isPresent && theUser.get().id == id) {
                    context.require(
                            requiredRoles = listOf(UserRole.Role.USER),
                            successCommand = factory.delete(id)
                    ).execute(withAccess)
                } else {
                    context.require(
                            requiredRoles = listOf(UserRole.Role.ADMIN),
                            successCommand = factory.delete(id)
                    ).execute(withAccess)
                }
            }
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }

    /**
     * Wrapper method for the [UserFactory.ban] method that adds Spring
     * Security authentication to the [Ban] command object
     *
     * @param id, a User id
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun ban(id: Long, withAccess: (result: SimpleResult<Long?, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if (context.currentUserId() != null) {
            return context.require(
                    requiredRoles = listOf(UserRole.Role.ADMIN),
                    successCommand = factory.ban(id)
            ).execute(withAccess)
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }

    /**
     *  Wrapper method for the [UserFactory.unban] method that adds Spring
     *  Security authentication to the [Unban] command object
     *
     *  @param [Unban.Response] object
     *  @param withAccess anonymous function that uses the command's result object
     *  @return An [AccessReport] if authentication fails, null otherwise
     */
    fun unban(id: Long, withAccess: (result: SimpleResult<Unban.Response, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        // If the user is logged in
        if (context.currentUserId() != null) {
            return context.require(
                    requiredRoles = listOf(UserRole.Role.ADMIN),
                    successCommand = factory.unban(id)
            ).execute(withAccess)
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }


    /**
     *  Wrapper method for the [UserFactory.changePassword] method that adds Spring
     *  Security authentication to the [ChangePassword] command object
     *
     *  @param request the [ChangePassword.Request] request
     *  @param withAccess anonymous function that uses the command's result object
     *  @return An [AccessReport] if authentication fails, null otherwise
     */
    fun changePassword(request: ChangePassword.Request, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if (context.currentUserId() != null) {
            if (!userRepo.existsById(request.id))
                return AccessReport(missingRoles = null, invalidResourceId = invalidUserIdErrors(request.id))

            val theUser = userRepo.findById(request.id).get()

            return if (context.currentUserId() == theUser.id) {
                context.require(
                        requiredRoles = listOf(UserRole.Role.USER),
                        successCommand = factory.changePassword(request)
                ).execute(withAccess)
            } else {
                return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
            }
        }
        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }

    /**
     * Wrapper method for the [UserFactory.search] method that adds Spring
     * Security authentication to the [Search] command object
     *
     * @param searchCriteria a [List] of [SearchCriteria]
     * @param pageable the [Pageable] interface
     * @param withAccess anonymous function that uses the command's result object
     * @return an [AccessReport] if authentications fails, null otherwise
     */
    fun search(searchCriteria: List<SearchCriteria>, pageable: Pageable, withAccess: (result: SimpleResult<Page<UserInfo>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if (context.currentUserId() != null) {
            return context.require(
                    requiredRoles = listOf(),
                    successCommand = factory.search(searchCriteria, pageable)
            ).execute(withAccess)
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }

    /**
     *  Wrapper method for the [UserFactory.invite] method that adds Spring
     *  Security authentication to the [Invite] command object
     *
     *  @param email an email to send the invite to
     *  @return An [AccessReport] if authentication fails, null otherwise
     */
    fun invite(email: String, withAccess: (result: SimpleResult<Boolean, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        // If the user is logged in
        if (context.currentUserId() != null) {
            return context.require(
                    requiredRoles = listOf(UserRole.Role.USER),
                    successCommand = factory.invite(email)
            ).execute(withAccess)
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }

    /**
     * Private method to return a [Map] of errors when a user could not be found.
     *
     * @param id the User id
     * @return a [Map] of errors
     */
    private fun invalidUserIdErrors(id: Long): Map<String, Collection<String>> {
        val errors = HashMultimap.create<ErrorTag, String>()
        errors.put(ErrorTag.ID, "User #$id could not be found")
        return errors.toStringMap()
    }
}