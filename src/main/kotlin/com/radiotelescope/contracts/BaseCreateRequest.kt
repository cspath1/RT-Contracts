package com.radiotelescope.contracts

/**
 * Each SQL Create operation will need to use this. It will take a request
 * and adapt it into a respective entity object that will be able to be
 * immediately persisted
 */
internal interface BaseCreateRequest<ENTITY> {
    fun toEntity(): ENTITY
}