package com.radiotelescope.contracts.thresholds

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.thresholds.Thresholds

/**
 * Abstract factory interface with methods for all [Thresholds] operations
 */
interface ThresholdsFactory {

    /**
     * Abstract command used to retrieve the [Thresholds] object
     *
     * @param sensorName the name of the sensor to retrieve
     * @return a [Command] object
     */
    fun retrieve(sensorName: String): Command<Thresholds, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to retrieve newest [Thresholds] objects by name
     *
     * @return a [Command] object
     */
    fun retrieveList(): Command<List<Thresholds>, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to update the [Thresholds] object
     *
     * @param sensorName the name of the sensor to update
     * @param maximum the maximum sensor value to update
     * @return a [Command] object
     */
    fun update(sensorName: String, maximum: Double): Command<Thresholds, Multimap<ErrorTag, String>>
}