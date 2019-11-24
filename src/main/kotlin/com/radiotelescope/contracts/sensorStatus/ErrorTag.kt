package com.radiotelescope.contracts.sensorStatus

/**
 * Enum representing field validation failures for the [SensorStatus] Entity
 */
enum class ErrorTag {
    GATE,
    PROXIMITY,
    AZIMUTH_MOTOR,
    ELEVATION_MOTOR,
    WEATHER_STATION,
    TOKEN,
    RECORD_CREATED_TIMESTAMP,
    RECORD_UPDATED_TIMESTAMP
}