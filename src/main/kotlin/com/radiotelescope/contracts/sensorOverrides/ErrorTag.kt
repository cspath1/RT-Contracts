package com.radiotelescope.contracts.sensorOverrides

/**
 * Enum representing field validation failures for the Sensor Overrides Entity
 */
enum class ErrorTag {
    ID,
    NAME,
    OVERRIDDEN,
    GATE,
    PROXIMITY,
    AZIMUTH_MOTOR,
    ELEVATION_MOTOR,
    WEATHER_STATION
}