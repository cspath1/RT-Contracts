package com.radiotelescope.contracts

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.security.UserPreconditionFailure
import com.radiotelescope.security.UserPreconditionFailureTag
import org.junit.Before

abstract class BaseUserWrapperTest {
    var executed = true
    var shouldFailOnPrecondition = false

    val failure = object : UserPreconditionFailure {
        override val errors = HashMultimap.create<UserPreconditionFailureTag, String>()

        override fun addError(error: Pair<UserPreconditionFailureTag, String>) {
            executed = false
            errors.put(error.first, error.second)
        }

        override fun addErrors(errors: Multimap<UserPreconditionFailureTag, String>) {
            executed = false
            this.errors.putAll(errors)
        }
    }

    @Before
    fun initialize() {
        executed = true
        shouldFailOnPrecondition = false
    }
}