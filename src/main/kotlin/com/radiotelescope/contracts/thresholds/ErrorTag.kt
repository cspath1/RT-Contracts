package com.radiotelescope.contracts.thresholds

/**
 * Enum representing field validation failures for the Thresholds Entity
 */
enum class ErrorTag {
    ID,
    NAME,
    MAXIMUM,
    WIND,
    AZ_MOTOR_TEMP,
    ELEV_MOTOR_TEMP,
    AZ_MOTOR_VIBRATION,
    ELEV_MOTOR_VIBRATION,
    AZ_MOTOR_CURRENT,
    ELEV_MOTOR_CURRENT,
    COUNTER_BALANCE_VIBRATION
}