package com.radiotelescope.contracts.telescopeLog

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Wrapper that takes a [TelescopeLogFactory] and is responsible for all
 * user role validations for Telescope Log endpoints.
 * NOTE: All endpoints require the user to be an admin
 *
 * @param context the [UserContext] interface
 * @param factory the [TelescopeLogFactory] interface
 */
class AdminTelescopeLogWrapper(
        private val context: UserContext,
        private val factory: TelescopeLogFactory
) {
    /**
     * Wrapper method for the [TelescopeLogFactory.retrieve] method that adds Spring Security
     * authentication to the [Retrieve] command object.
     *
     * @param id the Telescope Log id
     * @param withAccess anonymous function that uses the command's result object
     * @return an [AccessReport] if authentication fails, null otherwise
     */
    fun retrieve(id: Long, withAccess: (result: SimpleResult<TelescopeLogInfo, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        return context.require(
                requiredRoles = listOf(UserRole.Role.USER, UserRole.Role.ADMIN),
                successCommand = factory.retrieve(
                        id = id
                )
        ).execute(withAccess)
    }

    /**
     * Wrapper method for the [TelescopeLogFactory.list] method that adds Spring Security
     * authentication to the [List] command object.
     *
     * @param pageable the [Pageable] object
     * @param withAccess anonymous function that uses the command's result object
     * @return an [AccessReport] if authentication fails, null otherwise
     */
    fun  list(pageable: Pageable, withAccess: (result: SimpleResult<Page<TelescopeLogInfo>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        return context.require(
                requiredRoles = listOf(UserRole.Role.USER, UserRole.Role.ADMIN),
                successCommand = factory.list(
                        pageable = pageable
                )
        ).execute(withAccess)
    }
}