package com.radiotelescope.repository.spectracyberConfig

import java.util.*
import javax.persistence.*

/**
 * Entity Class representing an Spectracyber Configuration for the web-application
 *
 * This Entity correlates to the spectracyber_config SQL Table
 */
@Entity
@Table(name = "spectracyber_config")
data class SpectracyberConfig(
        @Column(name = "mode", nullable = false)
        @Enumerated(value = EnumType.STRING)
        var mode: Mode,
        @Column(name = "integration_time", nullable = false)
        var integrationTime: Double,
        @Column(name = "offset_voltage", nullable = false)
        var offsetVoltage: Double,
        @Column(name = "IF_GAIN", nullable = false)
        var IFGain: Double,
        @Column(name = "DC_GAIN", nullable = false)
        var DCGain: Int,
        @Column(name = "BANDWIDTH", nullable = false)
        var bandwidth: Int
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    enum class Mode(val label: String) {
        UNKNOWN("Unknown"),
        CONTINUUM("Continuum"),
        SPECTRAL("Spectral")
    }


        @Column(name = "insert_timestamp", nullable = false)
    var recordCreatedTimestamp: Date = Date()

    @Column(name = "update_timestamp", nullable = true)
    var recordUpdatedTimestamp: Date = Date()
}