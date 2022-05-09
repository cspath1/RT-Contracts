package com.radiotelescope.contracts.sensorStatus

import com.radiotelescope.repository.sensorStatus.SensorStatus

/**
 * Enum representing field validation failures for the [SensorStatus] Entity
 */
enum class ErrorTag {
    ID,
    GATE,
    WEATHER_STATION,
    elevation_abs_encoder,
    azimuth_abs_encoder,
    el_proximity_0,
    el_proximity_90,
    az_motor_temp_1,
    az_motor_temp_2,
    el_motor_temp_1,
    el_motor_temp_2,
    az_accel,
    el_accel,
    counter_balance_accel,
    TOKEN,
    RECORD_CREATED_TIMESTAMP,
    RECORD_UPDATED_TIMESTAMP
}