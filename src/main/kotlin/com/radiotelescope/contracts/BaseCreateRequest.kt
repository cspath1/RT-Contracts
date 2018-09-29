package com.radiotelescope.contracts

internal interface BaseCreateRequest<ENTITY> {
    fun toEntity(): ENTITY
}