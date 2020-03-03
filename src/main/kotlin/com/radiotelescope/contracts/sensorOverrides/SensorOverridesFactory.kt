package com.radiotelescope.contracts.sensorOverrides

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.sensorOverrides.SensorOverrides

interface SensorOverridesFactory {

    fun update(sensorName: String, overridden: Boolean): Command<SensorOverrides, Multimap<ErrorTag, String>>

}