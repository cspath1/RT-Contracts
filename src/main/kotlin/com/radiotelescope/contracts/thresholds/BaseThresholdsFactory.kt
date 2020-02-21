package com.radiotelescope.contracts.thresholds

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.thresholds.IThresholdsRepository
import com.radiotelescope.repository.thresholds.Thresholds

/**
 * Base concrete implementation of the [ThresholdsFactory] interface
 *
 * @param thresholdsRepo the [IThresholdsRepository] interface
 */
class BaseThresholdsFactory(
        private val thresholdsRepo: IThresholdsRepository,
        private val sensorName: Thresholds.Name
) : ThresholdsFactory {

    /**
     * Override of the [ThresholdsFactory] method that will return a [Retrieve]
     * command object
     *
     * @return a [Retrieve] command object
     */
    override fun retrieve(): Command<Thresholds, Multimap<ErrorTag, String>> {
        return Retrieve (
                thresholdsRepo = thresholdsRepo,
                sensorName = sensorName
        )
    }
}