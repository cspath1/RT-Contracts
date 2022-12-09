package com.radiotelescope.contracts.sensorOverrides

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.sensorOverrides.ISensorOverridesRepository
import com.radiotelescope.repository.sensorOverrides.SensorOverrides

/**
 * Base concrete implementation of the [SensorOverridesFactory] interface
 *
 * @param sensorOverridesRepo the [ISensorOverridesRepository] interface
 */
class BaseSensorOverridesFactory (
        private val sensorOverridesRepo: ISensorOverridesRepository
) : SensorOverridesFactory {

    /**
     * Override of the [SensorOverridesFactory] method that will
     * return a [Update] command object
     *
     * @param sensorName the name of the sensor to update
     * @param overridden the overridden status value to update
     * @return a [Update] command object
     */
    override fun update(sensorName: String, overridden: Boolean): Command<SensorOverrides, Multimap<ErrorTag, String>> {
        return Update(
                request = Update.Request (
                        sensorName = sensorName,
                        overridden = overridden
                ),
                sensorOverridesRepo = sensorOverridesRepo
        )
    }

    /**
     * Override of the [SensorOverridesFactory] method that will
     * return a [RetrieveList] command object
     *
     * @return a [RetrieveList] command object
     */
    override fun retrieveList(): Command<List<SensorOverrides>, Multimap<ErrorTag, String>> {
        return RetrieveList(
                sensorOverridesRepo = sensorOverridesRepo
        )
    }
}