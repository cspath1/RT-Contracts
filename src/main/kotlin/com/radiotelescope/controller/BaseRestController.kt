package com.radiotelescope.controller

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.security.UserPreconditionFailure
import com.radiotelescope.security.UserPreconditionFailureTag

abstract class BaseRestController(
        val logger: Logger
) {
    var result = Result()


    fun userPreconditionFailure(): UserPreconditionFailure {
        return object : UserPreconditionFailure {
            override fun addError(error: Pair<UserPreconditionFailureTag, String>) {
                errors.put(error.first, error.second)
            }

            override fun addErrors(errors: Multimap<UserPreconditionFailureTag, String>) {
                this.errors.putAll(errors)
            }

            override val errors: Multimap<UserPreconditionFailureTag, String>
                get() = HashMultimap.create()
        }
    }
}