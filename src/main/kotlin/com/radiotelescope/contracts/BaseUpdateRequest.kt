package com.radiotelescope.contracts

import com.radiotelescope.repository.user.IUserRepository

internal interface BaseUpdateRequest<ENTITY> {
    fun updateEntity(userRepo: IUserRepository): ENTITY
}