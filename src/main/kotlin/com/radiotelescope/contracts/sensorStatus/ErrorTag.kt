package com.radiotelescope.contracts.sensorStatus

import com.radiotelescope.repository.sensorStatus.SensorStatus

/**
 * Enum representing field validation failures for the [SensorStatus] Entity
 */
enum class ErrorTag {
    ID,
    GATE,
    PROXIMITY,
    AZIMUTH_MOTOR,
    ELEVATION_MOTOR,
    WEATHER_STATION,
    TOKEN,
    RECORD_CREATED_TIMESTAMP,
    RECORD_UPDATED_TIMESTAMP
}