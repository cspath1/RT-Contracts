package com.radiotelescope.contracts.log

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Wrapper that takes a [LogFactory] and is responsible for all
 * user role validations for endpoints for the Log Entity.
 * NOTE: All endpoints require the user to be an admin.
 *
 * @property context the [UserContext] interface
 * @property factory the [LogFactory] interface
 */
class AdminLogWrapper(
        private val context: UserContext,
        private val factory: LogFactory
) {
    /**
     * Wrapper method for the [LogFactory.list] method that adds Spring Security
     * authentication to the [LogList] command object.
     *
     * @param pageable the [Pageable] interface
     * @return An [AccessReport]
     */
    fun list(pageable: Pageable, withAccess: (result: SimpleResult<Page<LogInfo>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        return context.require(
                requiredRoles = listOf(UserRole.Role.USER, UserRole.Role.ADMIN),
                successCommand = factory.list(
                        pageable = pageable
                )
        ).execute(withAccess)
    }
}