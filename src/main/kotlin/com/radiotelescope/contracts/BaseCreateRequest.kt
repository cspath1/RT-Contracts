package com.radiotelescope.contracts

/**
 * Each SQL CoordinateCreate operation will need to use this. It will take a request
 * and adapt it into a respective entity object that will be able to be
 * immediately persisted
 */
interface BaseCreateRequest<ENTITY> {
    fun toEntity(): ENTITY
}