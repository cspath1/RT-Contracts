package com.radiotelescope.repository.orientation

import javax.persistence.*

/**
 * Entity Class representing a Orientation for the web-application
 *
 * This Entity correlates to the Orientation SQL Table
 */
@Entity
@Table(name = "orientation")
data class Orientation(
        @Column(name = "azimuth")
        var azimuth: Double,
        @Column(name = "elevation")
        var elevation: Double
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0
}