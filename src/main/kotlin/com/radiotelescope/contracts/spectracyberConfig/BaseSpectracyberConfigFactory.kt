package com.radiotelescope.contracts.spectracyberConfig

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.spectracyberConfig.ISpectracyberConfigRepository
import com.radiotelescope.repository.spectracyberConfig.SpectracyberConfig

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
     * @return an [Update] command object
     */
    override fun update(request: Update.Request): Command<Long, Multimap<ErrorTag, String>> {
        return Update(
                request = request,
                spectracyberConfigRepo = spectracyberConfigRepo
        )
    }

    /**
     * Override of the [SpectracyberConfigFactory.retrieve] method that will return a [Retrieve] command
     *
     * @param spectracyberConfigId the id of the [SpectracyberConfig] to retrieve
     * @return a [Retrieve] command object
     */
    override fun retrieve(spectracyberConfigId: Long): Command<SpectracyberConfig, Multimap<ErrorTag, String>> {
        return Retrieve(
                spectracyberConfigId = spectracyberConfigId,
                spectracyberConfigRepo = spectracyberConfigRepo
        )
    }
}