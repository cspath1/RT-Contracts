package com.radiotelescope.contracts.spectracyberConfig

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.spectracyberConfig.ISpectracyberConfigRepository

/**
 * Base concrete implementation of the [SpectracyberConfigFactory] interface
 *
 * @param spectracyberConfigRepo the [ISpectracyberConfigRepository] interface
 */
class BaseSpectracyberConfigFactory(
        private val spectracyberConfigRepo: ISpectracyberConfigRepository
) : SpectracyberConfigFactory {

    /**
     * Override of the [SpectracyberConfigFactory.update] method that will return a [Update] command
     *
     * @param request the [Update.Request] object
     * @return a [Update] command object
     */
    override fun update(request: Update.Request): Command<Long, Multimap<ErrorTag, String>> {
        return Update(
                request = request,
                spectracyberConfigRepo = spectracyberConfigRepo
        )
    }
}