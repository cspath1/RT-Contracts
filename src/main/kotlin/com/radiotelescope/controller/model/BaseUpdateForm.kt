package com.radiotelescope.controller.model

/**
 * Interface that all Create forms will inherit from
 */
interface BaseUpdateForm<REQUEST> {
    /**
     * Used to adapt a form into contracts request
     */
    fun toRequest(): REQUEST
}