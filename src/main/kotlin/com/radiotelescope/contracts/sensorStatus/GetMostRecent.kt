package com.radiotelescope.contracts.sensorStatus

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.sensorStatus.ISensorStatusRepository
import com.radiotelescope.repository.sensorStatus.SensorStatus

/**
 * Override of the [Command] interface method used to retrieve
 * the most recent [SensorStatus] information
 *
 * @param sensorStatusRepo the [ISensorStatusRepository] interface
 */
class GetMostRecent (
        private val sensorStatusRepo: ISensorStatusRepository
) : Command<SensorStatus, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command] execute method. It checks if there are any
     * records in the sensor_status table and retrieves the most recent
     * through the [ISensorStatusRepository.getMostRecentSensorStatus] method.
     *
     * If there are none, it will return an error in the
     * [SimpleResult].
     */
    override fun execute(): SimpleResult<SensorStatus, Multimap<ErrorTag, String>> {
        return SimpleResult(sensorStatusRepo.getMostRecentSensorStatus(), null)
    }
}