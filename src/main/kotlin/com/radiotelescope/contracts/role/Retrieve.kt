package com.radiotelescope.contracts.role

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.user.UserInfo
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository

/**
 * Implementation of the [Command] interface used to retrieve a
 * specific user role for an admin to then approve
 *
 * @param roleId the Role id
 * @param userRoleRepo the [IUserRoleRepository] interface
 * @param userRepo the [IUserRepository] interface
 */
class Retrieve(
        private val roleId: Long,
        private val userRoleRepo: IUserRoleRepository,
        private val userRepo: IUserRepository
) : Command<UserRoleInfo, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command] execute method. Calls the [validateRequest] method
     * which ensures the role exists.
     *
     * If the role exists, it will adapt the role into a [UserRoleInfo] object and return
     * it in the [SimpleResult].
     *
     * Otherwise, it will return a list of errors in the [SimpleResult]
     */
    override fun execute(): SimpleResult<UserRoleInfo, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val theRole = userRoleRepo.findById(roleId).get()
            val userInfo = UserInfo(userRepo.findById(theRole.userId!!).get(), null)
            val roleInfo = UserRoleInfo(
                    userInfo = userInfo,
                    userRole = theRole
            )
            return SimpleResult(roleInfo, null)
        }
    }

    /**
     * Method responsible for constraint checking for the user role retrieval.
     * It simply makes sure the role exists
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (!userRoleRepo.existsById(roleId))
            errors.put(ErrorTag.ID, "Role Id $roleId not found")

        return if (errors.isEmpty) null else errors
    }
}