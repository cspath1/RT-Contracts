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
     * @return a [Command] object
     */
    fun retrieve(sensorName: String): Command<Thresholds, Multimap<ErrorTag, String>>
}