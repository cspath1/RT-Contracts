package com.radiotelescope.contracts.sensorOverrides

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.sensorOverrides.SensorOverrides

/**
 * Abstract factory interface with methods for all [SensorOverrides] operations
 */
interface SensorOverridesFactory {

    /**
     * Abstract command used to update the [SensorOverrides] object
     *
     * @return a [Command] object
     */
    fun update(sensorName: String, overridden: Boolean): Command<SensorOverrides, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to retrieve newest [SensorOverrides] objects by name
     *
     * @return a [Command] object
     */
    fun retrieveList(): Command<List<SensorOverrides>, Multimap<ErrorTag, String>>
}