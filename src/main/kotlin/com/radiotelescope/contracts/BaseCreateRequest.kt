package com.radiotelescope.project.contracts

/**
 * Interface for Create Request objects
 */

internal interface BaseCreateRequest<ENTITY>{
    fun toEntity(): ENTITY
}