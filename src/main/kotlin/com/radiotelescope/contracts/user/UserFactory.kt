package com.radiotelescope.contracts.user

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command

interface UserFactory {
    fun register(request: Register.Request): Command<Long, Multimap<ErrorTag, String>>
}