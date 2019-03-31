package com.radiotelescope.contracts.log

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import kotlin.collections.List

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
     * authentication to the [List] command object.
     *
     * @param pageable the [Pageable] interface
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails or null
     */
    fun list(pageable: Pageable, withAccess: (result: SimpleResult<Page<LogInfo>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        return context.require(
                requiredRoles = listOf(UserRole.Role.USER, UserRole.Role.ADMIN),
                successCommand = factory.list(
                        pageable = pageable
                )
        ).execute(withAccess)
    }

    /**
     * Wrapper method for the [LogFactory.retrieveErrors] method that adds Spring Security
     * authentication to the [RetrieveErrors] command object
     *
     * @param logId the Log id
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails or null
     */
    fun retrieveErrors(logId: Long, withAccess: (result: SimpleResult<List<ErrorInfo>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        return context.require(
                requiredRoles = listOf(UserRole.Role.USER, UserRole.Role.ADMIN),
                successCommand = factory.retrieveErrors(
                        logId = logId
                )
        ).execute(withAccess)
    }
}