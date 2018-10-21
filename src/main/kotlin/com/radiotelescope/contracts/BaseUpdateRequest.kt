package com.radiotelescope.contracts

internal interface BaseUpdateRequest<ENTITY> {
    fun updateEntity(entity: ENTITY): ENTITY
}