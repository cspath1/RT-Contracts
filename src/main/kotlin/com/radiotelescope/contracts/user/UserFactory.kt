package com.radiotelescope.contracts.user

import com.google.common.collect.Multimap
import com.radiotelescope.repository.user.User
import com.radiotelescope.contracts.Command
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Abstract factory interface with methods for all [User] CRUD operations.
 */
interface UserFactory {
    /**
     * Abstract command used to register a user for the site
     *
     * @param request the [Register.Request] request
     * @return a [Command] object
     */
    fun register(request: Register.Request): Command<Long, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to log a user in to the site
     *
     * @param request the [Authenticate.Request] request
     * @return a [Command] object
     */
    fun authenticate(request: Authenticate.Request): Command<UserInfo, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to retrieve the user's information
     *
     * @param id the [User] id
     * @return a [Command] object
     */
    fun retrieve(id: Long): Command<UserInfo, Multimap<ErrorTag,String>>

    /**
     * Abstract command used by admins to retrieve a [Page] of [UserInfo]
     *
     * @param pageable the [Pageable] object
     * @return a [Command] object
     */
    fun list(pageable: Pageable): Command<Page<UserInfo>, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to update the user's information
     *
     * @param request the [Update.Request] request
     */
    fun update(request: Update.Request): Command<Long, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to "delete" a user's account
     *
     * @param id the User id
     */
    fun delete(id: Long): Command<Long, Multimap<ErrorTag, String>>

}