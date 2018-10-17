package com.radiotelescope.contracts

internal interface BaseUpdateRequest<ENTITY> {
    fun toEntity(): ENTITY
}