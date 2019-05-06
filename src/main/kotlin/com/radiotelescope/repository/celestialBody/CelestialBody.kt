package com.radiotelescope.repository.celestialBody

import com.radiotelescope.repository.coordinate.Coordinate
import javax.persistence.*

/**
 * Entity Class representing a Celestial Body for the web-application
 *
 * This Entity correlates to the Celestial Body SQL Table
 */
@Entity
@Table(name = "celestial_body")
data class CelestialBody(
        @Column(name = "name")
        var name: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    @OneToOne
    @JoinColumn(name = "coordinate_id")
    var coordinate: Coordinate? = null
}