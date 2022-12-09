package com.radiotelescope.repository.acceleration

import java.util.*
import javax.persistence.*

/**
 * Entity Class representing a Acceleration Blob record stored by the control room
 *
 * This Entity correlates to the three Acceleration Blob SQL tables
 */
@Entity
@Table(name = "azimuth_acceleration_blob")
data class AzimuthAccelerationBlob (
        @Column(name = "acc_blob", nullable = true)
        var acc_blob: ByteArray
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private var id: Long = 0

    @Column(name = "first_time_captured", nullable = false)
    private var FirstTimeCaptured: Long = 0

    fun getId(): Long {
        return id
    }
}

@Entity
@Table(name = "elevation_acceleration_blob")
data class ElevationAccelerationBlob (
        @Column(name = "acc_blob", nullable = true)
        var acc_blob: ByteArray
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private var id: Long = 0

    @Column(name = "first_time_captured", nullable = false)
    private var FirstTimeCaptured: Long = 0

    fun getId(): Long {
        return id
    }
}

@Entity
@Table(name = "counterbalance_acceleration_blob")
data class CounterbalanceAccelerationBlob (
        @Column(name = "acc_blob", nullable = true)
        var acc_blob: ByteArray
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private var id: Long = 0

    @Column(name = "first_time_captured", nullable = false)
    private var FirstTimeCaptured: Long = 0

    fun getId(): Long {
        return id
    }
}



