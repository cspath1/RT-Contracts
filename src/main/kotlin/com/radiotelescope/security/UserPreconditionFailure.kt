package com.radiotelescope.security

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command

interface UserPreconditionFailure : Command<Nothing?, Multimap<UserPreconditionFailureTag, String>> {
    val errors: Multimap<UserPreconditionFailureTag, String>

    /**
     * Used to add a single error to the [Multimap] before responding
     */
    fun addError(error: Pair<UserPreconditionFailureTag, String>)

    /**
     * Used to add a full list of errors before responding
     */
    fun addErrors(errors: Multimap<UserPreconditionFailureTag, String>)

    override fun execute(): Pair<Nothing?, Multimap<UserPreconditionFailureTag, String>> {
        return Pair(null, errors)
    }
}

enum class UserPreconditionFailureTag {
    MISSING_ROLE,
    MISSING_PERMISSION
}