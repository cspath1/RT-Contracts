package com.radiotelescope.contracts.spectracyberConfig

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.spectracyberConfig.SpectracyberConfig

/**
 * Abstract factory interface with methods for all [SpectracyberConfig] operations
 */
interface SpectracyberConfigFactory {
    /**
     * Abstract command used to update a [SpectracyberConfig] object
     *
     * @param request the [Update.Request] request
     * @return a [Command] object
     */
    fun update(request: Update.Request): Command<Long, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to retrieve a [SpectracyberConfig] object
     *
     * @param spectracyberConfigId the id of the [SpectracyberConfig] to retrieve
     * @return a [Command] object
     */
    fun retrieve(spectracyberConfigId: Long): Command<SpectracyberConfig, Multimap<ErrorTag, String>>
}