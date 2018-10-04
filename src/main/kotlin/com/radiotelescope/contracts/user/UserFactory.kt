package com.radiotelescope.contracts.user

import com.google.common.collect.Multimap
import com.radiotelescope.repository.user.User
import com.radiotelescope.contracts.Command

/**
 * Abstract factory wrapper to all [User] CRUD operations. Each factory interface needs
 * to have a method for each command object for its respective entity.
 */
interface UserFactory {
    fun register(request: Register.Request): Command<Long, Multimap<ErrorTag, String>>

    fun retrieve(id: Long): Command<UserInfo, Multimap<ErrorTag,String>>
}

