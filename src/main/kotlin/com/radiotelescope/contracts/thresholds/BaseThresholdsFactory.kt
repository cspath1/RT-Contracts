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
        private val thresholdsRepo: IThresholdsRepository
) : ThresholdsFactory {

    /**
     * Override of the [ThresholdsFactory] method that will return a [Retrieve]
     * command object
     *
     * @param sensorName the name of the sensor to retrieve
     * @return a [Command] object
     */
    override fun retrieve(sensorName: String): Command<Thresholds, Multimap<ErrorTag, String>> {
        return Retrieve (
                thresholdsRepo = thresholdsRepo,
                sensorName = sensorName
        )
    }

    /**
     * Override of the [ThresholdsFactory] method that will return a [RetrieveList]
     * command object
     *
     * @return a [Command] object
     */
    override fun retrieveList(): Command<List<Thresholds>, Multimap<ErrorTag, String>> {
        return RetrieveList (
                thresholdsRepo = thresholdsRepo
        )
    }

    /**
     * Override of the [ThresholdsFactory] method that will return a [Update]
     * command object
     *
     * @param sensorName the name of the sensor to update
     * @param maximum the maximum sensor value to update
     * @return a [Command] object
     */
    override fun update(sensorName: String, maximum: Double): Command<Thresholds, Multimap<ErrorTag, String>> {
        return Update (
                request = Update.Request (
                        sensorName = sensorName,
                        maximum = maximum
                ),
                thresholdsRepo = thresholdsRepo
        )
    }
}