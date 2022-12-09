package com.radiotelescope.repository.location

import javax.persistence.*

/**
 * Entity Class representing a Location for the Radio Telescope
 * This is information that will be updated by the control room
 * software, and will not be modified by this application.
 *
 * This Entity correlates to the Location SQL Table
 */

@Entity
@Table(name = "location")
data class Location(
        @Column(name = "latitude")
        var latitude: Double,
        @Column(name = "longitude")
        var longitude: Double,
        @Column(name = "altitude")
        var altitude: Double,
        @Column(name = "name")
        var name: String
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private var id: Long = 0

    fun getId(): Long {
        return id
    }
}