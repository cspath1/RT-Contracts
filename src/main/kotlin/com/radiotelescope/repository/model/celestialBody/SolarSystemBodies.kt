package com.radiotelescope.repository.model.celestialBody

/**
 * Due to the proximity of celestial bodies within our solar system,
 * right ascension and declination must be calculated. This is different
 * than other celestial bodies outside of our solar system, which are far
 * enough away that the right ascension is declination is effectively static
 *
 * This enum is denotes celestial bodies that will not need a right ascension
 * and declination in order to create. If a celestial body is entered by a user
 * in order to create it, and it is not in this list, right ascension and declination
 * are required.
 */
enum class SolarSystemBodies(val label: String) {
    SUN("The Sun"),
    MERCURY("Mercury"),
    VENUS("Venus"),
    MARS("Mars"),
    MOON("The Moon"),
    SATURN("Saturn"),
    JUPITER("Jupiter"),
    URANUS("Uranus"),
    NEPTUNE("Neptune"),
    PLUTO("Pluto")
}