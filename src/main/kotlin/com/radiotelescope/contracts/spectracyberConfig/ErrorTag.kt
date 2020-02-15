package com.radiotelescope.contracts.spectracyberConfig

import com.radiotelescope.repository.spectracyberConfig.SpectracyberConfig

/**
 * Enum representing field validation failures for the [SpectracyberConfig]] Entity
 */
enum class ErrorTag {
    ID,
    MODE,
    INTEGRATION_TIME,
    OFFSET_VOLTAGE,
    IF_GAIN,
    DC_GAIN,
    BANDWIDTH
}