package com.radiotelescope.contracts.role

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User

/**
 * Override of the [Command] interface for a user to request new user role
 *
 * @param request the [RequestRole.Request]
 * @param userRepo the [IUserRepository] interface
 * @param userRoleRepo the [IUserRoleRepository] interface
 */
class RequestRole(
        private val request: Request,
        private val userRepo: IUserRepository,
        private val userRoleRepo: IUserRoleRepository
) : Command<Long, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method. Calls the [validateRequest] method
     * that will handle all constraint checking and validation.
     *
     * If validation passes, it will create and persist the [UserRole] object, set [UserRole.approved] to false
     * and return the id in the [SimpleResult] object.
     *
     * If validation fails, it will return a [SimpleResult] with the errors
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        val errors = validateRequest()!!
        if(!errors.isEmpty)
            return SimpleResult(null, errors)

        // Delete any old request
        val roleList = userRoleRepo.findAllByUserId(request.user.id)
        roleList.forEach { role ->
            if(!role.approved)
                userRoleRepo.delete(role)
        }

        val newRole = request.toEntity()
        userRoleRepo.save(newRole)
        return SimpleResult(newRole.id, null)

    }

    /**
     * Method responsible for constraint checking and validations for the
     * [UserRole] request. It ensures the user exists and the role is not the same
     * as the current role. It also ensures that the role the [UserRole] will
     * be set to is not an admin
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        with(request) {
            if(!userRepo.existsById(user.id))
                errors.put(ErrorTag.USER_ID, "User does not exist")
            else{
                if(userRoleRepo.findMembershipRoleByUserId(user.id) != null)
                    if(userRoleRepo.findMembershipRoleByUserId(user.id)!!.role == role)
                        errors.put(ErrorTag.ROLE, "New role is the same as the current role")
                if(role == UserRole.Role.ADMIN)
                    errors.put(ErrorTag.ROLE, "Cannot set request admin role")
                if(role == UserRole.Role.USER)
                    errors.put(ErrorTag.ROLE, "Already a user")
                else
                    return errors
            }
        }

        return if (errors.isEmpty) null else errors
    }

    /**
     * Data class containing all fields necessary for requesting
     * a new role
     *
     * @param user the User
     * @param role the desired [UserRole.Role] value
     */
    data class Request(
            val user: User,
            val role: UserRole.Role
    ) : BaseCreateRequest<UserRole> {
        /**
         * Concrete implementation of the [BaseCreateRequest.toEntity] method that
         * returns a UserRole object
         */
        override fun toEntity(): UserRole {
            val theUserRole = UserRole(
                    user = user,
                    role = role
            )

            theUserRole.approved = false

            return theUserRole
        }
    }
}