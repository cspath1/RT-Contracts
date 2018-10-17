package com.radiotelescope.contracts

import com.radiotelescope.repository.user.User

internal interface BaseUpdateRequest<ENTITY> {
    fun updateEntity(user: User): ENTITY
}