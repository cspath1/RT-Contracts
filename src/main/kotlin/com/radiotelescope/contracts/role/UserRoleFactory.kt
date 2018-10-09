package com.radiotelescope.contracts.role

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.role.UserRole
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Abstract factory wrapper for all [UserRole] CRUD operations. Each factory interface
 * needs to have a method for each command object for its respective entity
 */
interface UserRoleFactory {
    /**
     * Abstract command used to retrieve unapproved [UserRole] objects
     *
     * @param pageable the [Pageable] request
     * @return a [Command] object
     */
    fun unapprovedList(pageable: Pageable): Command<Page<UserRoleInfo>, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to validate an unapproved [UserRole] object
     *
     * @param request the [Validate.Request] object
     * @return a [Command] object
     */
    fun validate(request: Validate.Request): Command<Long, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to retrieve a specific [UserRole]
     *
     * @param id the [UserRole] id
     * @return a [Command] object
     */
    fun retrieve(id: Long): Command<UserRoleInfo, Multimap<ErrorTag, String>>
}