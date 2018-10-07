package com.radiotelescope.controller.spring

import com.radiotelescope.contracts.user.UserUserWrapper

/**
 * Interface to get instantiations of all User Wrappers
 */
interface FactoryProvider {
    /**
     * Abstract method to return the [UserUserWrapper] class
     */
    fun getUserWrapper(): UserUserWrapper
}