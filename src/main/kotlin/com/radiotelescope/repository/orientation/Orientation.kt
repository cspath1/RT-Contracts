package com.radiotelescope.repository.orientation

import javax.persistence.*

@Entity
@Table(name = "orientation")
data class Orientation(
        @Column(name = "right_ascension")
        var rightAscension: Double,
        @Column(name = "declination")
        var declination: Double
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0
}