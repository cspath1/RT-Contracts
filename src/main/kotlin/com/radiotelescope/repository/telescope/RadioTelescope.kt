package com.radiotelescope.repository.telescope

import com.radiotelescope.repository.orientation.Orientation
import javax.persistence.*

/**
 * Entity Class representing a Radio Telescope. This is used with
 * scheduling appointments. This is information that will be
 * updated by the control room software, and will not be modified by
 * this application.
 *
 * This Entity correlates to the Radio Telescope SQL Table
 */
@Entity
@Table(name = "radio_telescope")
class RadioTelescope {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private var id: Long = 0

    @Column(name = "online")
    private var online: Boolean = true

    @OneToOne
    @JoinColumn(name = "current_orientation_id")
    private lateinit var currentOrientation: Orientation

    @OneToOne
    @JoinColumn(name = "calibration_orientation_id")
    private lateinit var calibrationOrientation: Orientation

    // This is a regular column because it is not a reference to a different table
    @Column(name = "telescope_type", nullable = true)
    private var telescopeType: TelescopeType = TelescopeType.NONE

    fun getId(): Long {
        return id
    }

    fun getOnline(): Boolean {
        return online
    }
    
    fun getCurrentOrientation(): Orientation {
        return currentOrientation
    }
    
    fun getCalibrationOrientation(): Orientation {
        return calibrationOrientation
    }

    fun getTelescopeType(): TelescopeType {
        return telescopeType
    }
}