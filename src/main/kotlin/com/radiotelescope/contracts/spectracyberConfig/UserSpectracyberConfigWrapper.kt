package com.radiotelescope.contracts.spectracyberConfig

import com.google.common.collect.Multimap
import com.radiotelescope.security.UserContext
import com.radiotelescope.contracts.Command

/**
 * Wrapper that takes a [SpectracyberConfigFactory] and is responsible for all
 * user role validations for the SpectracyberConfig Entity
 *
 * @property context the [UserContext] interface
 * @property factory the [SpectracyberConfigFactory] interface
 */
class UserSpectracyberConfigWrapper (
        val context: UserContext,
        val factory: SpectracyberConfigFactory
) {
    /**
     * Wrapper method for the [SpectracyberConfigFactory.update] method.
     *
     * @param request the [Update.Request] object
     * @return a [Command] object
     */
    fun update(request: Update.Request): Command<Long, Multimap<ErrorTag, String>> {
        return factory.update(request)
    }
}