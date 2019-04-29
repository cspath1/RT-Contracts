package com.radiotelescope.contracts.userNotificationType

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext
import com.radiotelescope.toStringMap

class UserUserNotificationTypeWrapper(
        private val context : UserContext,
        private val factory: BaseUserNotificationTypeFactory,
        private val userRepo: IUserRepository
) {

    fun setPhone(id: Long, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if (context.currentUserId() != null) {
            if (!userRepo.existsById(id)) {
                return AccessReport(missingRoles = null, invalidResourceId = invalidUserIdErrors(id))
            } else {
                // If the user exists, they must either be the owner or an admin
                val theUser = userRepo.findById(id).get()
                return if (theUser.id == context.currentUserId()) {
                    context.require(
                            requiredRoles = listOf(UserRole.Role.USER),
                            successCommand = factory.setPhone(id)
                    ).execute(withAccess)
                }else {
                    return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
                }
            }
        }
        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)

    }

    private fun invalidUserIdErrors(id: Long): Map<String, Collection<String>> {
        val errors = HashMultimap.create<com.radiotelescope.contracts.user.ErrorTag, String>()
        errors.put(com.radiotelescope.contracts.user.ErrorTag.ID, "User #$id could not be found")
        return errors.toStringMap()
    }
}