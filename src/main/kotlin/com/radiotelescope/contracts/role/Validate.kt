package com.radiotelescope.contracts.role

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole

/**
 * Implementation of the [Command] interface used to approve a [UserRole].
 * The command works by letting the admin select the desired role and
 * the associated [UserRole] object will be updated to have this role
 * and with then be set to approved
 *
 * @param request the [Validate.Request] object
 * @param userRoleRepo the [IUserRoleRepository] interface
 */
class Validate(
        private val request: Validate.Request,
        private val userRoleRepo: IUserRoleRepository
) : Command<Long, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method that will validate the [validateRequest]
     * method and if there are no errors, will update the [UserRole] object and persist
     * the changes, setting it to approved. As part of this, it will remove any pre-existing
     * approved roles, as this role will replace any existing roles. This excludes the base
     * "user" role, as this just indicates that the user is logged in.
     *
     * If there are errors, it will respond with them
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val id = updateRole()

            // Delete any old roles or any other requested roles
            val roleList = userRoleRepo.findAllByUserId(userRoleRepo.findById(request.id).get().user.id)
            roleList.forEach { theRole ->
                if(theRole.id != request.id && theRole.role != UserRole.Role.USER)
                    userRoleRepo.delete(theRole)
            }

            return SimpleResult(id, null)
        }
    }

    /**
     * Private method to update the [UserRole] based on the
     * [Validate.Request] object
     */
    private fun updateRole(): Long {
        val userRole = userRoleRepo.findById(request.id).get()

        userRole.role = request.role
        userRole.approved = true
        userRoleRepo.save(userRole)

        return userRole.id
    }

    /**
     * Method responsible for constraint checking and validations for the
     * [UserRole] approval. It ensures the [UserRole] exists and is not
     * already approved. It also ensures that the role the [UserRole] will
     * be set to is not an admin
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        with(request) {
            if (!userRoleRepo.existsById(id)) {
                errors.put(ErrorTag.ID, "UserRole #$id not found")
                return errors
            }

            val userRole = userRoleRepo.findById(id).get()

            if (userRole.approved) {
                errors.put(ErrorTag.APPROVED, "Role is already approved")
                return errors
            }

            if (role == UserRole.Role.ADMIN)
                errors.put(ErrorTag.ROLE, "Cannot set role to admin")

        }

        return if (errors.isEmpty) null else errors
    }

    /**
     * Data class containing all fields necessary for role approval
     *
     * @param id the [UserRole.user]'s id
     * @param role the desired [UserRole.Role] value
     */
    data class Request(
            val id: Long,
            val role: UserRole.Role
    )
}