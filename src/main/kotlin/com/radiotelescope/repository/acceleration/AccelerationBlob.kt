package com.radiotelescope.repository.acceleration

import java.util.*
import javax.persistence.*

/**
 * Entity Class representing a Acceleration record stored by the control room
 *
 * This Entity correlates to the acceleration SQL table
 */
@Entity
@Table(name = "acceleration_blob")
data class AccelerationBlob (
        @Column(name = "acc_blob", nullable = true)
        var acc_blob: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private var id: Long = 0

    @Column(name = "time_captured", nullable = false)
    private var TimeCaptured: Long = 0

    fun getId(): Long {
        return id
    }
}

