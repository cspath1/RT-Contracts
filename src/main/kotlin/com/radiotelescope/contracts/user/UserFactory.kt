package com.radiotelescope.contracts.user

import com.google.common.collect.Multimap
import com.radiotelescope.repository.user.User
import com.radiotelescope.contracts.Command

/**
 * Abstract factory wrapper to all [User] CRUD operations. Each factory interface needs
 * to have a method for each command object for its respective entity.
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
}